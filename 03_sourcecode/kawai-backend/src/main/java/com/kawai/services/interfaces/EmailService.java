package com.kawai.services.interfaces;

import java.math.BigDecimal;

public interface EmailService {
    void sendInvoiceEmail(Long roomBookingDetailId, BigDecimal totalAmount);
}
