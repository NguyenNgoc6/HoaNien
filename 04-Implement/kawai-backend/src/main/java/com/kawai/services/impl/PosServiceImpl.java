package com.kawai.services.impl;

import com.kawai.dto.CartItemDto;
import com.kawai.dto.CreateFoodOrderRequest;
import com.kawai.exceptions.BusinessException;
import com.kawai.models.Employee;
import com.kawai.models.FolioItem;
import com.kawai.models.FoodOrder;
import com.kawai.models.FoodOrderDetail;
import com.kawai.models.Room;
import com.kawai.models.RoomBookingDetail;
import com.kawai.repositories.EmployeeRepository;
import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.FoodItemRepository;
import com.kawai.repositories.FoodOrderDetailRepository;
import com.kawai.repositories.FoodOrderRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import com.kawai.repositories.RoomRepository;
import com.kawai.services.interfaces.PosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PosServiceImpl implements PosService {

    private final RoomRepository roomRepository;
    private final RoomBookingDetailRepository roomBookingDetailRepository;
    private final FolioItemRepository folioItemRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final FoodOrderDetailRepository foodOrderDetailRepository;
    private final FoodItemRepository foodItemRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Khởi tạo một đơn đặt thức ăn tại phòng (Room Service).
     * Yêu cầu phòng phải có khách (OCCUPIED). Đơn hàng sẽ tự động cộng 10% phí phục vụ theo quy tắc BR-FB-05.
     *
     * @param request Thông tin yêu cầu đặt thức ăn
     * @return FoodOrder Đơn hàng đã được tạo
     */
    @Override
    @Transactional
    public FoodOrder createRoomServiceOrder(CreateFoodOrderRequest request) {
        Room room = getOccupiedRoom(request.getRoomNumber());
        RoomBookingDetail detail = getBookingDetail(room.getCurrentBookingDetailId());

        FoodOrder order = new FoodOrder();
        order.setOrderType("Room Service");
        order.setRoomBookingDetail(detail);
        order.setOrderStatus("Pending");
        order.setPaymentType(request.getPaymentType() != null ? request.getPaymentType() : "Pay_Later");
        order.setIsPaidInPos(false);
        order.setNote(request.getNote());

        // Dummy logic to find an employee, using ID 2 as per previous controller logic
        Employee emp = employeeRepository.findById(2L)
                .orElseGet(() -> employeeRepository.findAll().stream().findFirst().orElse(null));
        order.setCreatedByStaff(emp);

        FoodOrder savedOrder = foodOrderRepository.save(order);

        BigDecimal totalOrderAmount = createAndSaveOrderDetails(savedOrder, request.getItems());
        
        // Trừ tiền vào credit limit
        if (detail.getSubCreditLimit().compareTo(totalOrderAmount) < 0) {
            throw new BusinessException("CREDIT_LIMIT_EXCEEDED", "Vượt quá credit limit");
        }
        detail.setSubCreditLimit(detail.getSubCreditLimit().subtract(totalOrderAmount));
        roomBookingDetailRepository.save(detail);

        return savedOrder;
    }

    /**
     * Helper method để tạo và lưu danh sách món ăn vào CSDL, đồng thời áp dụng phụ phí 10%.
     * 
     * @param savedOrder Đơn hàng cha
     * @param items Danh sách món được đặt từ request
     */
    private BigDecimal createAndSaveOrderDetails(FoodOrder savedOrder, java.util.List<CartItemDto> items) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (items == null) return totalAmount;
        for (CartItemDto itemDto : items) {
            if (itemDto.getPrice() != null) {
                BigDecimal itemTotal = itemDto.getPrice().multiply(new BigDecimal(itemDto.getQty()));
                BigDecimal priceWithFee = itemTotal.multiply(new BigDecimal("1.10"));
                totalAmount = totalAmount.add(priceWithFee);
            }

            foodItemRepository.findById(itemDto.getId()).ifPresent(menuItem -> {
                FoodOrderDetail orderDetail = new FoodOrderDetail();
                orderDetail.setFoodOrder(savedOrder);
                orderDetail.setMenuItem(menuItem);
                orderDetail.setQuantity(itemDto.getQty());

                // BR-FB-05: Đơn hàng Room Service cộng thêm phí phục vụ 10%
                if (itemDto.getPrice() != null) {
                    BigDecimal priceWithFee = itemDto.getPrice().multiply(new BigDecimal("1.10"));
                    orderDetail.setPriceAtOrder(priceWithFee);
                }

                orderDetail.setKotStatus("Pending");
                foodOrderDetailRepository.save(orderDetail);
            });
        }
        return totalAmount;
    }

    @Override
    @Transactional
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
        if (bookingDetailId == null) {
            throw new BusinessException("BOOKING_NOT_FOUND", "Phòng trống, không có thông tin đặt phòng");
        }
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
