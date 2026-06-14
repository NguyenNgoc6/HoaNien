package com.kawai.services.interfaces;

import java.math.BigDecimal;

public interface CheckoutService {
    void processCheckout(Long roomBookingDetailId);
    void settleFolio(Long roomBookingDetailId, BigDecimal amount, String paymentMethod);
}
