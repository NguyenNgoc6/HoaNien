package com.kawai.services.interfaces;

import java.math.BigDecimal;

/**
 * CheckoutService — Nghiệp vụ tất toán & Check-out (UC25).
 *
 * Use Cases:
 *   - UC25.1: Xử lý thanh toán Check-out cuối cùng
 *   - UC25.2: Kích hoạt tất toán Folio và gửi e-Invoice tự động
 */
public interface CheckoutService {

    /**
     * Xử lý quy trình Check-out cuối cùng cho một chi tiết đặt phòng (UC25.1).
     *
     * Điều kiện tiên quyết (BR-FIN-01): Toàn bộ FolioItem phải được tất toán
     * ({@code isSettledSeparately = true}) trước khi cho phép Check-out.
     * Nếu vi phạm → ném RuntimeException mã FOLIO-001.
     *
     * Sau khi Check-out thành công → phòng chuyển sang DIRTY (BR-FO-09).
     *
     * @param roomBookingDetailId ID của chi tiết đặt phòng cần Check-out
     * @throws RuntimeException mã FOLIO-001 nếu còn nợ chưa tất toán
     */
    void processCheckout(Long roomBookingDetailId);

    /**
     * Tất toán toàn bộ FolioItem và kích hoạt gửi e-Invoice tự động (UC25.2).
     *
     * Đánh dấu từng khoản chưa tất toán thành {@code isSettledSeparately = true} (BR-FIN-06),
     * sau đó gọi EmailService để gửi hóa đơn điện tử cho khách.
     *
     * @param roomBookingDetailId ID của chi tiết đặt phòng cần tất toán
     * @param amount              tổng số tiền thanh toán (BigDecimal, BR-FIN-05)
     * @param paymentMethod       hình thức thanh toán: CASH / CREDIT_CARD / TRANSFER
     */
    void settleFolio(Long roomBookingDetailId, BigDecimal amount, String paymentMethod);
}
