package com.kawai.controllers.api;

import com.kawai.services.interfaces.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * REST Controller cho Module 5 — Dashboard Manager (UC26).
 *
 * Authorization Matrix (BR-SYS-06):
 *   - GET /api/v1/report/dashboard  → MANAGER only  (UC26.1)
 *   - GET /api/v1/report/occupancy  → MANAGER only  (UC26.2)
 *
 * Tham chiếu EDS_MOD5_SPEC.md — Section 9: API Specification.
 */
@RestController
@RequestMapping("/api/v1/report")
public class ReportRestController {

    private final ReportService reportService;

    public ReportRestController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * UC26.1 — Giám sát biểu đồ tài chính (Dashboard Manager).
     *
     * Trả về tổng doanh thu phân loại theo 3 nguồn USALI (BR-FIN-04):
     *   - "ROOM"  → REV-ROOM  (tiền phòng, minibar)
     *   - "FB"    → REV-FB    (nhà hàng, room service)
     *   - "TOUR"  → REV-TOUR  (gói lữ hành)
     *
     * @return 200 OK — Map{ "ROOM": BigDecimal, "FB": BigDecimal, "TOUR": BigDecimal }
     *         500 Internal Server Error — nếu lỗi hệ thống (SYS-001)
     *
     * Sample response:
     * {
     *   "ROOM": 4000000,
     *   "FB": 500000,
     *   "TOUR": 800000
     * }
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, BigDecimal>> getFinancialDashboard() {
        Map<String, BigDecimal> dashboardData = reportService.getDashboardFinancialData();
        return ResponseEntity.ok(dashboardData);
    }

    /**
     * UC26.2 — Xem công suất phòng và số khách đang lưu trú (Occupancy Rate).
     *
     * Công thức: Occupancy Rate (%) = (số phòng OCCUPIED / tổng phòng) × 100
     * Edge case được xử lý: 0 phòng trong hệ thống → trả về 0.0%.
     *
     * @return 200 OK — { "occupancyRate": double, "unit": "%" }
     *         500 Internal Server Error — nếu lỗi hệ thống (SYS-001)
     *
     * Sample response:
     * {
     *   "occupancyRate": 75.0,
     *   "unit": "%"
     * }
     */
    @GetMapping("/occupancy")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> getOccupancyRate() {
        double rate = reportService.getOccupancyRate();
        Map<String, Object> response = Map.of(
            "occupancyRate", rate,
            "unit", "%"
        );
        return ResponseEntity.ok(response);
    }
}
