package com.kawai.controllers.api;

import com.kawai.models.FolioItem;
import com.kawai.services.interfaces.FolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * FolioApiController — REST API cho Module 5: Folio & Night Audit (UC24).
 *
 * <p>Authorization Matrix:
 * <ul>
 *   <li>GET  /api/v1/folio/{bookingId}            → RECEPTIONIST, MANAGER</li>
 *   <li>POST /api/v1/folio/{bookingId}/payment     → RECEPTIONIST</li>
 *   <li>GET  /api/v1/folio/{bookingId}/total       → RECEPTIONIST, MANAGER</li>
 *   <li>POST /api/v1/audit/run                     → ADMIN, SYSTEM</li>
 * </ul>
 *
 * @author Lan — MOD 5 Finance/Report
 */
@RestController
@RequestMapping("/api/v1")
public class FolioApiController {

    private final FolioService folioService;

    @Autowired
    public FolioApiController(FolioService folioService) {
        this.folioService = folioService;
    }

    /**
     * UC24.1 — Xem danh sách khoản nợ Folio theo bookingId.
     *
     * <p>GET /api/v1/folio/{bookingId}
     *
     * <p>Response 200:
     * <pre>[
     *   { "folio_item_id": 1, "amount": 500000, "sourceDepartment": "ROOM", "description": "Night Audit Room Charge" },
     *   { "folio_item_id": 2, "amount": 150000, "sourceDepartment": "F&B",  "description": "Bữa tối nhà hàng" }
     * ]</pre>
     *
     * <p>Error 404: FOLIO-002 — Booking không tồn tại
     */
    @GetMapping("/folio/{bookingId}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<List<FolioItem>> getFolioItems(@PathVariable Long bookingId) {
        List<FolioItem> items = folioService.getFolioItemsForBooking(bookingId);
        return ResponseEntity.ok(items);
    }

    /**
     * UC24.2 — Ghi nhận một đợt luồng tiền vào Folio (ứng trước / trả thêm / hoàn tiền).
     *
     * <p>POST /api/v1/folio/{bookingId}/payment
     *
     * <p>Request Body:
     * <pre>{
     *   "amount": 500000,
     *   "paymentMethod": "CASH",
     *   "description": "Ứng trước tiền phòng"
     * }</pre>
     *
     * <p>Response 200: { "message": "Payment recorded successfully" }
     * <p>Error 400: FOLIO-002 — Booking không tồn tại
     */
    @PostMapping("/folio/{bookingId}/payment")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<Map<String, String>> addPayment(
            @PathVariable Long bookingId,
            @RequestBody Map<String, Object> body) {
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String paymentMethod = body.get("paymentMethod").toString();
        String description = body.get("description").toString();
        folioService.addPayment(bookingId, amount, paymentMethod, description);
        return ResponseEntity.ok(Map.of("message", "Payment recorded successfully"));
    }

    /**
     * UC24.3 — Tính tổng dư nợ Folio của một booking.
     *
     * <p>GET /api/v1/folio/{bookingId}/total
     *
     * <p>Response 200: { "bookingId": 1, "totalBalance": 650000 }
     * <p>Error 404: FOLIO-002 — Booking không tồn tại
     */
    @GetMapping("/folio/{bookingId}/total")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getTotalFolio(@PathVariable Long bookingId) {
        BigDecimal total = folioService.calculateTotalFolio(bookingId);
        return ResponseEntity.ok(Map.of("bookingId", bookingId, "totalBalance", total));
    }

    /**
     * UC24.4 — Kích hoạt Night Audit thủ công (BR-FIN-03).
     * Thông thường được gọi tự động bởi @Scheduled Cron Job lúc 02:00 AM.
     *
     * <p>POST /api/v1/audit/run
     *
     * <p>Response 200: { "message": "Night Audit completed successfully" }
     * <p>Error 400: FIN-001 — Đã audit trong ngày
     */
    @PostMapping("/audit/run")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, String>> runNightAudit() {
        folioService.runNightAudit();
        folioService.incrementBusinessDate();
        return ResponseEntity.ok(Map.of("message", "Night Audit completed successfully"));
    }
}
