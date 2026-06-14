package com.kawai.services.interfaces;

import java.math.BigDecimal;
import java.util.Map;

/**
 * ReportService — Nghiệp vụ Dashboard & Báo cáo tài chính (UC26).
 *
 * Use Cases:
 *   - UC26.1: Giám sát biểu đồ tài chính (Dashboard Manager)
 *   - UC26.2: Xem công suất phòng & Occupancy Rate
 */
public interface ReportService {

    /**
     * Tổng hợp doanh thu phân loại theo nguồn bộ phận USALI (UC26.1).
     *
     * Nhóm theo {@code sourceDepartment} (BR-FIN-04):
     *   "ROOM" → REV-ROOM, "FB" → REV-FB, "TOUR" → REV-TOUR
     *
     * @return Map{department → totalAmount}; không null, rỗng nếu không có dữ liệu
     */
    Map<String, BigDecimal> getDashboardFinancialData();

    /**
     * Tính tỉ lệ phòng đang có khách lưu trú (UC26.2).
     *
     * Công thức: (occupiedRooms / totalRooms) × 100
     * Edge case: totalRooms = 0 → trả 0.0, không ném ArithmeticException.
     *
     * @return tỉ lệ phần trăm dạng double (ví dụ: 75.0 = 75%)
     */
    double getOccupancyRate();
}
