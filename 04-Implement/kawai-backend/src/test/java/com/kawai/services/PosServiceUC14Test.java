package com.kawai.services;

import com.kawai.dto.CartItemDto;
import com.kawai.dto.CreateFoodOrderRequest;
import com.kawai.models.Employee;
import com.kawai.models.FoodOrder;
import com.kawai.models.MenuItem;
import com.kawai.models.Room;
import com.kawai.models.RoomBookingDetail;
import com.kawai.repositories.EmployeeRepository;
import com.kawai.repositories.FoodItemRepository;
import com.kawai.repositories.FoodOrderDetailRepository;
import com.kawai.repositories.FoodOrderRepository;
import com.kawai.repositories.RestaurantTableRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import com.kawai.repositories.RoomRepository;
import com.kawai.services.impl.PosServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PosServiceUC14Test {

    @Mock
    private FoodOrderRepository foodOrderRepository;
    @Mock
    private FoodOrderDetailRepository foodOrderDetailRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomBookingDetailRepository roomBookingDetailRepository;
    @Mock
    private RestaurantTableRepository restaurantTableRepository;
    @Mock
    private FoodItemRepository foodItemRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private PosServiceImpl posService;

    @Test
    public void testCreateRoomServiceOrder_Success() {
        // TC-M3-001: Khách quét QR đặt Room Service
        CreateFoodOrderRequest request = new CreateFoodOrderRequest();
        request.setOrderType("room-svc");
        request.setRoomNumber("101");
        request.setPaymentType("Pay_Later");
        CartItemDto item = new CartItemDto();
        item.setId(1L);
        item.setQty(2);
        item.setPrice(new BigDecimal("50000"));
        request.setItems(Collections.singletonList(item));

        Room room = new Room();
        room.setRoomNumber("101");
        room.setRoomStatus("OCCUPIED");
        room.setCurrentBookingDetailId(100L);

        RoomBookingDetail bookingDetail = new RoomBookingDetail();
        bookingDetail.setId(100L);

        Employee emp = new Employee();
        emp.setId(2L);

        MenuItem menuItem = new MenuItem();
        menuItem.setId(1L);

        FoodOrder savedOrder = new FoodOrder();
        savedOrder.setId(10L);

        when(roomRepository.findByRoomNumber("101")).thenReturn(Optional.of(room));
        when(roomBookingDetailRepository.findById(100L)).thenReturn(Optional.of(bookingDetail));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(emp));
        when(foodOrderRepository.save(any(FoodOrder.class))).thenReturn(savedOrder);
        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));

        FoodOrder response = posService.createRoomServiceOrder(request);

        assertNotNull(response, "Response should not be null");
    }

    @Test
    public void testCreateRoomServiceOrder_RoomNotOccupied() {
        // TC-M3-002: Room Service phòng không OCCUPIED → từ chối
        CreateFoodOrderRequest request = new CreateFoodOrderRequest();
        request.setOrderType("room-svc");
        request.setRoomNumber("102"); // Phòng trống

        Room room = new Room();
        room.setRoomNumber("102");
        room.setCurrentBookingDetailId(null); // Không có khách

        when(roomRepository.findByRoomNumber("102")).thenReturn(Optional.of(room));

        assertThrows(Exception.class, () -> {
            posService.createRoomServiceOrder(request);
        });
    }
}
