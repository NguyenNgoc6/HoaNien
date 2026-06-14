package com.kawai.controllers.api;

import com.kawai.services.interfaces.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * REST Controller cho Module 5 — Tất toán & Check-out (UC25).
 *
 * <p>Authorization Matrix (BR-SYS-06):
 * <ul>
 *   <li>POST /api/v1/checkout/{id}/process  → RECEPTIONIST (UC25.1)</li>
 *   <li>POST /api/v1/checkout/{id}/settle   → RECEPTIONIST (UC25.1 + UC25.2)</li>
 * </ul>
 *
 * <p>Tham chiếu EDS_MOD5_SPEC.md — Section 9: API Specification.
 *
 * @author Ngo Thi Ngoc Lan (MOD5)
 */
@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutRestController {

    private final CheckoutService checkoutService;

    /**
     * Constructor injection.
     *
     * @param checkoutService service xử lý checkout và tất toán Folio
     */
    public CheckoutRestController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    /**
     * UC25.1 — Xử lý Check-out cuối cùng cho khách.
     *
     * <p>Kiểm tra Folio Balance = 0 (BR-FIN-01) trước khi cho phép khách rời đi.
     * Nếu còn nợ → trả lỗi FOLIO-001 (HTTP 400).
     * Nếu sạch → chuyển phòng sang DIRTY (BR-FO-09) và xác nhận thành công.
     *
     * @param roomBookingDetailId ID của chi tiết đặt phòng
     * @return 200 OK nếu checkout thành công
     *         400 Bad Request + mã FOLIO-001 nếu còn nợ chưa thanh toán
     *
     * Sample success response:
     * <pre>{@code
     * { "status": "success", "message": "Check-out thành công. Phòng đã chuyển sang DIRTY." }
     * }</pre>
     */
    @PostMapping("/{id}/process")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<Map<String, String>> processCheckout(@PathVariable("id") Long roomBookingDetailId) {
        try {
            checkoutService.processCheckout(roomBookingDetailId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Check-out thành công. Phòng đã chuyển sang DIRTY."
            ));
        } catch (RuntimeException e) {
            // BR-FIN-01: FOLIO-001 — trả về 400 Bad Request
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * UC25.1 + UC25.2 — Tất toán Folio và gửi e-Invoice tự động.
     *
     * <p>Đánh dấu toàn bộ FolioItem của phòng là đã thanh toán (BR-FIN-06),
     * sau đó kích hoạt gửi hóa đơn điện tử qua email cho khách (UC25.2).
     *
     * @param roomBookingDetailId ID của chi tiết đặt phòng
     * @param payload             JSON body gồm: {@code amount} (BigDecimal),
     *                            {@code paymentMethod} (CASH / CREDIT_CARD / TRANSFER)
     * @return 200 OK nếu tất toán thành công và e-Invoice đã gửi
     *         400 Bad Request nếu dữ liệu không hợp lệ
     *
     * Sample request body:
     * <pre>{@code
     * { "amount": 4500000, "paymentMethod": "CREDIT_CARD" }
     * }</pre>
     *
     * Sample success response:
     * <pre>{@code
     * { "status": "success", "message": "Tất toán Folio thành công. e-Invoice đã gửi qua email." }
     * }</pre>
     */
    @PostMapping("/{id}/settle")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<Map<String, String>> settleFolio(
            @PathVariable("id") Long roomBookingDetailId,
            @RequestBody Map<String, Object> payload) {
        try {
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            String paymentMethod = (String) payload.get("paymentMethod");

            checkoutService.settleFolio(roomBookingDetailId, amount, paymentMethod);

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tất toán Folio thành công. e-Invoice đã gửi qua email."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
}
