package com.kawai.services;

import com.kawai.exceptions.BusinessException;
import java.util.Collections;

import com.kawai.dto.OrderItemRequest;
import com.kawai.models.FoodOrder;
import com.kawai.models.MenuItem;
import com.kawai.models.RestaurantTable;
import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import com.kawai.repositories.RoomRepository;
import com.kawai.services.impl.PosServiceImpl;
import com.kawai.repositories.FoodItemRepository;
import com.kawai.repositories.FoodOrderRepository;
import com.kawai.repositories.RestaurantTableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PosServiceUC16Test {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomBookingDetailRepository roomBookingDetailRepository;

    @Mock
    private FolioItemRepository folioItemRepository;

    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private FoodOrderRepository foodOrderRepository;

    @InjectMocks
    private PosServiceImpl posService;

    @Test
    void testCreateDineInOrder_Success() {
        // TC-M3-005: POS tạo order đúng bàn/món/giá
        Long tableId = 1L;
        OrderItemRequest req1 = new OrderItemRequest();
        req1.setMenuItemId(101L);
        req1.setQuantity(2);
        
        OrderItemRequest req2 = new OrderItemRequest();
        req2.setMenuItemId(102L);
        req2.setQuantity(1);
        
        List<OrderItemRequest> requests = Arrays.asList(req1, req2);

        RestaurantTable table = new RestaurantTable();
        table.setId(tableId);

        MenuItem item1 = new MenuItem();
        item1.setId(101L);
        item1.setPrice(new BigDecimal("100000"));
        item1.setIsAvailable(true);

        MenuItem item2 = new MenuItem();
        item2.setId(102L);
        item2.setPrice(new BigDecimal("50000"));
        item2.setIsAvailable(true);

        when(restaurantTableRepository.findById(tableId)).thenReturn(Optional.of(table));
        when(foodItemRepository.findById(101L)).thenReturn(Optional.of(item1));
        when(foodItemRepository.findById(102L)).thenReturn(Optional.of(item2));
        
        when(foodOrderRepository.save(any(FoodOrder.class))).thenAnswer(i -> {
            FoodOrder saved = i.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        FoodOrder order = posService.createDineInOrder(tableId, requests);

        assertNotNull(order, "Order should not be null");
        assertEquals("UNPAID", order.getOrderStatus(), "Order status should be UNPAID");
        assertEquals(2, order.getDetails().size(), "Order should have 2 details");
    }

    @Test
    void testCreateDineInOrder_EmptyRequests() {
        // TC-M3-007: Empty array throws POS-001
        BusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
            BusinessException.class,
            () -> posService.createDineInOrder(1L, Collections.emptyList())
        );
        assertEquals("POS-001", exception.getErrorCode());
    }

    @Test
    void testCreateDineInOrder_NegativeQuantity() {
        // TC-M3-008: Quantity <= 0 throws POS-004
        OrderItemRequest req = new OrderItemRequest();
        req.setMenuItemId(101L);
        req.setQuantity(0);

        when(restaurantTableRepository.findById(1L)).thenReturn(Optional.of(new RestaurantTable()));

        BusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
            BusinessException.class,
            () -> posService.createDineInOrder(1L, Arrays.asList(req))
        );
        assertEquals("POS-004", exception.getErrorCode());
    }

    @Test
    void testCreateDineInOrder_ItemUnavailable() {
        // TC-M3-009: Item unavailable throws POS-002
        OrderItemRequest req = new OrderItemRequest();
        req.setMenuItemId(101L);
        req.setQuantity(1);

        MenuItem item = new MenuItem();
        item.setId(101L);
        item.setIsAvailable(false);

        when(restaurantTableRepository.findById(1L)).thenReturn(Optional.of(new RestaurantTable()));
        when(foodItemRepository.findById(101L)).thenReturn(Optional.of(item));

        BusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
            BusinessException.class,
            () -> posService.createDineInOrder(1L, Arrays.asList(req))
        );
        assertEquals("POS-002", exception.getErrorCode());
    }

    @Test
    void testCreateDineInOrder_ItemNotFound() {
        // TC-M3-010: Item not found throws ITEM_NOT_FOUND
        OrderItemRequest req = new OrderItemRequest();
        req.setMenuItemId(999L);
        req.setQuantity(1);

        when(restaurantTableRepository.findById(1L)).thenReturn(Optional.of(new RestaurantTable()));
        when(foodItemRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
            BusinessException.class,
            () -> posService.createDineInOrder(1L, Arrays.asList(req))
        );
        assertEquals("ITEM_NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void testCreateDineInOrder_TableNotFound() {
        // TC-M3-011: Table not found throws TABLE_NOT_FOUND
        OrderItemRequest req = new OrderItemRequest();
        req.setMenuItemId(101L);
        req.setQuantity(1);

        when(restaurantTableRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
            BusinessException.class,
            () -> posService.createDineInOrder(999L, Arrays.asList(req))
        );
        assertEquals("TABLE_NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void testPayOrder_Success() {
        // TC-M3-006: POS thanh toán → PAID
        Long orderId = 10L;
        String paymentMethod = "CASH";

        FoodOrder existingOrder = new FoodOrder();
        existingOrder.setId(orderId);
        existingOrder.setOrderStatus("UNPAID");

        when(foodOrderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(foodOrderRepository.save(any(FoodOrder.class))).thenAnswer(i -> i.getArgument(0));

        FoodOrder paidOrder = posService.payOrder(orderId, paymentMethod);

        assertNotNull(paidOrder, "Paid order should not be null");
        assertEquals("PAID", paidOrder.getOrderStatus(), "Order status should be PAID");
    }
}
