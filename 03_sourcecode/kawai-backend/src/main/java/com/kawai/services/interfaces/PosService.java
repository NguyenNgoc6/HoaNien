package com.kawai.services.interfaces;

import java.math.BigDecimal;

public interface PosService {
    void chargeToRoom(String roomNumber, BigDecimal amount);
    
    // Stub methods for UC16 TDD
    com.kawai.models.FoodOrder createDineInOrder(Long tableId, java.util.List<com.kawai.dto.OrderItemRequest> requests);
    com.kawai.models.FoodOrder payOrder(Long orderId, String paymentMethod);
}
