package com.hoanien.services.interfaces;

import java.math.BigDecimal;

public interface PosService {
    void chargeToRoom(String roomNumber, BigDecimal amount);
}
