package com.kawai.services.interfaces;

import java.math.BigDecimal;

/**
 * EmailService — Gửi e-Invoice qua email sau khi tất toán Folio (UC25.2).
 *
 * Tích hợp với SendGrid để tự động gửi hóa đơn điện tử cho khách
 * ngay sau khi hoàn tất thanh toán tại quầy Check-out.
 */
public interface EmailService {

    /**
     * Gửi e-Invoice đến email của khách sau khi tất toán Folio (UC25.2).
     *
     * @param roomBookingDetailId ID chi tiết đặt phòng — tra cứu thông tin khách và email
     * @param totalAmount         tổng số tiền đã thanh toán (BigDecimal, BR-FIN-05)
     */
    void sendInvoiceEmail(Long roomBookingDetailId, BigDecimal totalAmount);
}
