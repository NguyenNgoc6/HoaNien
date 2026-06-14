package com.kawai.services.impl;

import com.kawai.exceptions.BusinessException;
import com.kawai.models.FolioItem;
import com.kawai.models.Room;
import com.kawai.models.RoomBookingDetail;
import com.kawai.models.FoodOrder;
import com.kawai.models.FoodOrderDetail;
import com.kawai.models.MenuItem;
import com.kawai.models.RestaurantTable;
import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import com.kawai.repositories.RoomRepository;
import com.kawai.repositories.RestaurantTableRepository;
import com.kawai.repositories.FoodItemRepository;
import com.kawai.repositories.FoodOrderRepository;
import com.kawai.dto.OrderItemRequest;
import com.kawai.services.interfaces.PosService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PosService handling POS related business logic
 * such as charging to room, creating dine-in orders, and processing payments.
 */
public class PosServiceImpl implements PosService {

    private final RoomRepository roomRepository;
    private final RoomBookingDetailRepository roomBookingDetailRepository;
    private final FolioItemRepository folioItemRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final FoodItemRepository foodItemRepository;
    private final FoodOrderRepository foodOrderRepository;

    public PosServiceImpl(RoomRepository roomRepository,
            RoomBookingDetailRepository roomBookingDetailRepository,
            FolioItemRepository folioItemRepository,
            RestaurantTableRepository restaurantTableRepository,
            FoodItemRepository foodItemRepository,
            FoodOrderRepository foodOrderRepository) {
        this.roomRepository = roomRepository;
        this.roomBookingDetailRepository = roomBookingDetailRepository;
        this.folioItemRepository = folioItemRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.foodItemRepository = foodItemRepository;
        this.foodOrderRepository = foodOrderRepository;
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

    /**
     * Creates a new dine-in order for a specific table with a list of menu items.
     *
     * @param tableId     The ID of the restaurant table.
     * @param requests    The list of menu item requests to order.
     * @return The saved FoodOrder.
     * @throws BusinessException if item list is empty, table not found, or item unavailable.
     */
    @Override
    public FoodOrder createDineInOrder(Long tableId, List<OrderItemRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new BusinessException("POS-001", "Đơn hàng trống — không có món");
        }

        RestaurantTable table = getRestaurantTable(tableId);

        FoodOrder order = new FoodOrder();
        order.setTable(table);
        order.setOrderType("DINE_IN");
        order.setOrderStatus("UNPAID");
        order.setIsPaidInPos(false);

        List<FoodOrderDetail> details = createOrderDetails(order, requests);
        order.setDetails(details);

        return foodOrderRepository.save(order);
    }

    /**
     * Helper method to fetch and validate a restaurant table.
     */
    private RestaurantTable getRestaurantTable(Long tableId) {
        return restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new BusinessException("TABLE_NOT_FOUND", "Bàn không tồn tại"));
    }

    /**
     * Helper method to create order details from a list of item IDs.
     */
    private List<FoodOrderDetail> createOrderDetails(FoodOrder order, List<OrderItemRequest> requests) {
        List<FoodOrderDetail> details = new ArrayList<>();
        for (OrderItemRequest req : requests) {
            if (req.getQuantity() == null || req.getQuantity() <= 0) {
                throw new BusinessException("POS-004", "Số lượng món phải lớn hơn 0");
            }
            MenuItem item = getAvailableMenuItem(req.getMenuItemId());

            FoodOrderDetail detail = new FoodOrderDetail();
            detail.setFoodOrder(order);
            detail.setMenuItem(item);
            detail.setQuantity(req.getQuantity());
            detail.setPriceAtOrder(item.getPrice());
            detail.setKotStatus("Pending");
            details.add(detail);
        }
        return details;
    }

    /**
     * Helper method to fetch and validate a menu item.
     */
    private MenuItem getAvailableMenuItem(Long itemId) {
        MenuItem item = foodItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("ITEM_NOT_FOUND", "Món không tồn tại"));

        if (!Boolean.TRUE.equals(item.getIsAvailable())) {
            throw new BusinessException("POS-002", "Món đã hết — không thể order");
        }
        return item;
    }

    /**
     * Pays a food order using the specified payment method.
     *
     * @param orderId       The ID of the order to pay.
     * @param paymentMethod The payment method used (e.g., CASH, CREDIT_CARD).
     * @return The updated FoodOrder.
     * @throws BusinessException if order not found.
     */
    @Override
    public FoodOrder payOrder(Long orderId, String paymentMethod) {
        FoodOrder order = foodOrderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Hóa đơn không tồn tại"));

        order.setOrderStatus("PAID");
        order.setPaymentType(paymentMethod);
        order.setIsPaidInPos(true);

        return foodOrderRepository.save(order);
    }
}
