package com.kawai.services.impl;

import com.kawai.models.Booking;
import com.kawai.models.FolioItem;
import com.kawai.models.RoomBookingDetail;
import com.kawai.repositories.BookingRepository;
import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import com.kawai.services.interfaces.FolioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * FolioServiceImpl — Hỗ trợ Quản lý Hóa đơn & Công nợ (Folio) - UC24
 * MODULE 5: Hóa đơn & Báo cáo
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Service
@Transactional
public class FolioServiceImpl implements FolioService {

    private final FolioItemRepository folioItemRepository;
    private final BookingRepository bookingRepository;
    private final RoomBookingDetailRepository roomBookingDetailRepository;

    public FolioServiceImpl(FolioItemRepository folioItemRepository, 
                            BookingRepository bookingRepository, 
                            RoomBookingDetailRepository roomBookingDetailRepository) {
        this.folioItemRepository = folioItemRepository;
        this.bookingRepository = bookingRepository;
        this.roomBookingDetailRepository = roomBookingDetailRepository;
    }

    /**
     * Lấy danh sách các khoản phí/thanh toán (FolioItem) theo Booking ID.
     */
    @Override
    public List<FolioItem> getFolioItemsForBooking(Long bookingId) {
        return folioItemRepository.findByBookingId(bookingId);
    }

    /**
     * Ghi nhận một khoản thanh toán vào Folio.
     */
    @Override
    public void addPayment(Long bookingId, BigDecimal amount, String paymentMethod, String description) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        FolioItem payment = createPaymentItem(booking, amount, paymentMethod, description);
        folioItemRepository.save(payment);
    }

    private FolioItem createPaymentItem(Booking booking, BigDecimal amount, String paymentMethod, String description) {
        FolioItem payment = new FolioItem();
        payment.setBooking(booking);
        payment.setPayerCustomer(booking.getCustomer());
        payment.setSourceDepartment("FRONT_DESK");
        payment.setAmount(amount);
        payment.setDescription(description + " - " + paymentMethod);
        return payment;
    }

    /**
     * Tính tổng số dư nợ hiện tại của một Booking.
     */
    @Override
    public BigDecimal calculateTotalFolio(Long bookingId) {
        List<FolioItem> items = getFolioItemsForBooking(bookingId);
        return items.stream()
                .map(FolioItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Chạy Night Audit: Quét các phòng đang Occupied và tính phí phòng theo ngày.
     */
    @Override
    public void runNightAudit() {
        List<RoomBookingDetail> occupiedRooms = roomBookingDetailRepository.findByDetailStatus("Occupied");
        occupiedRooms.forEach(this::chargeNightlyRoomRate);
    }

    private void chargeNightlyRoomRate(RoomBookingDetail detail) {
        FolioItem charge = new FolioItem();
        charge.setBooking(detail.getRoomBooking());
        charge.setRoomBookingDetail(detail);
        charge.setPayerCustomer(detail.getCustomer());
        charge.setSourceDepartment("ROOM");
        charge.setAmount(detail.getRoomCharge());
        charge.setDescription("Nightly room charge");
        folioItemRepository.save(charge);
    }

    /**
     * Chuyển Business Date lên 1 ngày.
     */
    @Override
    public void incrementBusinessDate() {
        // Logic sẽ được tích hợp với SystemConfig sau.
    }
}
