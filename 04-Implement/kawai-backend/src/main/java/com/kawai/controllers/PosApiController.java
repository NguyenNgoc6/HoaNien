package com.kawai.controllers;

import com.kawai.dto.CartItemDto;
import com.kawai.dto.CreateFoodOrderRequest;
import com.kawai.models.Employee;
import com.kawai.models.FoodOrder;
import com.kawai.models.FoodOrderDetail;
import com.kawai.models.RestaurantTable;
import com.kawai.repositories.EmployeeRepository;
import com.kawai.repositories.FoodItemRepository;
import com.kawai.repositories.FoodOrderDetailRepository;
import com.kawai.repositories.FoodOrderRepository;
import com.kawai.repositories.RestaurantTableRepository;
import com.kawai.services.interfaces.PosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class PosApiController {

    private final PosService posService;
    private final FoodOrderRepository foodOrderRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final EmployeeRepository employeeRepository;
    private final FoodItemRepository foodItemRepository;
    private final FoodOrderDetailRepository foodOrderDetailRepository;

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody CreateFoodOrderRequest request) {
        try {
            if ("room-svc".equals(request.getOrderType())) {
                FoodOrder savedOrder = posService.createRoomServiceOrder(request);
                return ResponseEntity.ok().body(Map.of("status", "success", "orderId", savedOrder.getId()));
            } else {
                // Dine-In
                FoodOrder order = new FoodOrder();
                order.setOrderType("Dine In");
                if (request.getTableId() != null) {
                    restaurantTableRepository.findById(request.getTableId()).ifPresent(order::setTable);
                }
                order.setOrderStatus("Pending");
                order.setPaymentType(request.getPaymentType() != null ? request.getPaymentType() : "Pay_Later");
                order.setIsPaidInPos(false);
                order.setCustomerName(request.getCustomerName());
                order.setNote(request.getNote());

                Employee emp = employeeRepository.findById(2L)
                        .orElseGet(() -> employeeRepository.findAll().stream().findFirst().orElse(null));
                order.setCreatedByStaff(emp);

                FoodOrder savedOrder = foodOrderRepository.save(order);

                if (request.getItems() != null) {
                    for (CartItemDto itemDto : request.getItems()) {
                        foodItemRepository.findById(itemDto.getId()).ifPresent(menuItem -> {
                            FoodOrderDetail detail = new FoodOrderDetail();
                            detail.setFoodOrder(savedOrder);
                            detail.setMenuItem(menuItem);
                            detail.setQuantity(itemDto.getQty());
                            detail.setPriceAtOrder(itemDto.getPrice());
                            detail.setKotStatus("Pending");
                            foodOrderDetailRepository.save(detail);
                        });
                    }
                }
                return ResponseEntity.ok().body(Map.of("status", "success", "orderId", savedOrder.getId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    @PostMapping("/orders/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long id) {
        try {
            Optional<FoodOrder> orderOpt = foodOrderRepository.findById(id);
            if (orderOpt.isPresent()) {
                FoodOrder order = orderOpt.get();
                order.setIsPaidInPos(true);
                order.setOrderStatus("Paid");
                foodOrderRepository.save(order);

                if ("Dine In".equalsIgnoreCase(order.getOrderType()) && order.getTable() != null) {
                    RestaurantTable table = order.getTable();
                    table.setTableStatus("Vacant");
                    restaurantTableRepository.save(table);
                }

                return ResponseEntity.ok().body(Map.of("status", "success"));
            }
            return ResponseEntity.status(404).body(Map.of("error", "Order not found"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
