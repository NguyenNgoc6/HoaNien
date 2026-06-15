package com.kawai.services.interfaces;

import java.math.BigDecimal;

public interface PosService {
    void chargeToRoom(String roomNumber, BigDecimal amount);
    com.kawai.models.FoodOrder createRoomServiceOrder(com.kawai.dto.CreateFoodOrderRequest request);
}
