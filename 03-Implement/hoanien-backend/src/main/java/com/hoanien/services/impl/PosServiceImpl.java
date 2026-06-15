package com.hoanien.services.impl;

import com.hoanien.exceptions.BusinessException;
import com.hoanien.models.FolioItem;
import com.hoanien.models.Room;
import com.hoanien.models.RoomBookingDetail;
import com.hoanien.repositories.FolioItemRepository;
import com.hoanien.repositories.RoomBookingDetailRepository;
import com.hoanien.repositories.RoomRepository;
import com.hoanien.services.interfaces.PosService;

import java.math.BigDecimal;

public class PosServiceImpl implements PosService {

    private final RoomRepository roomRepository;
    private final RoomBookingDetailRepository roomBookingDetailRepository;
    private final FolioItemRepository folioItemRepository;

    public PosServiceImpl(RoomRepository roomRepository,
            RoomBookingDetailRepository roomBookingDetailRepository,
            FolioItemRepository folioItemRepository) {
        this.roomRepository = roomRepository;
        this.roomBookingDetailRepository = roomBookingDetailRepository;
        this.folioItemRepository = folioItemRepository;
    }

    @Override
    public void chargeToRoom(String roomNumber, BigDecimal amount) {
        Room room = getOccupiedRoom(roomNumber);
        RoomBookingDetail detail = getBookingDetail(room.getCurrentBookingDetailId());

        validateCreditLimit(detail, amount);

        postChargeToFolio(detail, amount);
    }

    private Room getOccupiedRoom(String roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new BusinessException("ROOM_NOT_FOUND", "Phòng không tồn tại"));

        if (!"OCCUPIED".equalsIgnoreCase(room.getRoomStatus())) {
            throw new BusinessException("ROOM_NOT_OCCUPIED", "Phòng không ở trạng thái OCCUPIED");
        }
        return room;
    }

    private RoomBookingDetail getBookingDetail(Long bookingDetailId) {
        return roomBookingDetailRepository.findById(bookingDetailId)
                .orElseThrow(() -> new BusinessException("BOOKING_NOT_FOUND", "Không tìm thấy thông tin đặt phòng"));
    }

    private void validateCreditLimit(RoomBookingDetail detail, BigDecimal amount) {
        if (detail.getSubCreditLimit().compareTo(amount) < 0) {
            throw new BusinessException("POS-003", "Post to Room vượt Credit Limit");
        }
    }

    private void postChargeToFolio(RoomBookingDetail detail, BigDecimal amount) {
        FolioItem folioItem = new FolioItem();
        folioItem.setRoomBookingDetail(detail);
        folioItem.setSourceDepartment("POS");
        folioItem.setAmount(amount);
        folioItem.setDescription("Ký gửi hóa đơn từ nhà hàng");

        folioItemRepository.save(folioItem);
    }
}
