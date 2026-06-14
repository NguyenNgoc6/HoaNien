package com.kawai.services.interfaces;

import java.math.BigDecimal;
import java.util.Map;

public interface ReportService {

    /**
     * UC26.1 — Trả về dữ liệu biểu đồ tài chính cho Manager Dashboard.
     * Bao gồm tổng doanh thu theo từng nguồn: REV-ROOM, REV-FB, REV-TOUR.
     *
     * @return Map với key là source_department ("ROOM", "FB", "TOUR")
     *         và value là tổng tiền BigDecimal tương ứng.
     */
    Map<String, BigDecimal> getDashboardFinancialData();

    /**
     * UC26.2 — Tính Occupancy Rate theo công thức:
     * Occupancy Rate (%) = (số phòng đang OCCUPIED / tổng số phòng) × 100
     *
     * @return tỉ lệ phần trăm dạng double, ví dụ 75.0 nghĩa là 75%.
     */
    double getOccupancyRate();
}
