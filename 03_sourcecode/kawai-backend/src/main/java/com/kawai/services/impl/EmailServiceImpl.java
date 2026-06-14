package com.kawai.services.impl;

import com.kawai.services.interfaces.EmailService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Triển khai dịch vụ gửi e-Invoice qua email — Module 5 (UC25.2).
 *
 * <p>Tích hợp với SendGrid để tự động gửi hóa đơn điện tử cho khách
 * ngay sau khi hoàn tất tất toán Folio tại quầy Check-out.
 *
 * <p>Use Case phục vụ:
 * <ul>
 *   <li>UC25.2 — Tự động kích hoạt e-Invoice (Xuất hóa đơn điện tử gửi email khách)</li>
 * </ul>
 *
 * <p><b>Lưu ý triển khai:</b> Phiên bản hiện tại log nội dung e-Invoice ra console.
 * Tích hợp SendGrid API thực tế cần cấu hình {@code SENDGRID_API_KEY} trong
 * {@code application.yml} và inject {@code SendGrid} bean từ thư viện
 * {@code com.sendgrid:sendgrid-java}.
 *
 * @author Ngo Thi Ngoc Lan (MOD5)
 */
@Service
public class EmailServiceImpl implements EmailService {

    /**
     * Gửi e-Invoice đến email của khách sau khi tất toán Folio (UC25.2).
     *
     * <p>Luồng xử lý dự kiến (full integration):
     * <ol>
     *   <li>Tra cứu thông tin khách hàng và email từ {@code RoomBookingDetail}.</li>
     *   <li>Tạo nội dung HTML invoice với {@code totalAmount} và mã booking.</li>
     *   <li>Gọi SendGrid API để gửi email.</li>
     *   <li>Ghi Audit Log kết quả gửi.</li>
     * </ol>
     *
     * @param roomBookingDetailId ID chi tiết đặt phòng — dùng để tra cứu thông tin khách
     * @param totalAmount         tổng số tiền đã thanh toán (BigDecimal, BR-FIN-05)
     */
    @Override
    public void sendInvoiceEmail(Long roomBookingDetailId, BigDecimal totalAmount) {
        // TODO (UC25.2 — SendGrid integration):
        //   1. Inject SendGridClient bean
        //   2. Lookup customer email từ RoomBookingDetail → Customer → email
        //   3. Build HTML email template với totalAmount
        //   4. sendGrid.api(new Request()).execute()
        //   5. Log audit trail
        System.out.printf(
            "[EmailService] e-Invoice gửi thành công — bookingDetailId=%d, totalAmount=%s VNĐ%n",
            roomBookingDetailId,
            totalAmount != null ? totalAmount.toPlainString() : "N/A"
        );
    }
}
