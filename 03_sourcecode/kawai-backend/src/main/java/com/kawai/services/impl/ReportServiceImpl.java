package com.kawai.services.impl;

import com.kawai.models.FolioItem;
import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.RoomRepository;
import com.kawai.services.interfaces.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Triển khai nghiệp vụ Dashboard & Báo cáo tài chính — Module 5 (MOD5).
 *
 * Business Rules tuân thủ:
 *  - BR-FIN-04: Phân loại doanh thu USALI — 3 nguồn: REV-ROOM / REV-FB / REV-TOUR.
 *  - BR-FIN-05: Toàn bộ phép tính tiền sử dụng BigDecimal + RoundingMode.HALF_UP.
 *
 * Use Cases:
 *  - UC26.1 — Giám sát biểu đồ tài chính (getDashboardFinancialData)
 *  - UC26.2 — Xem Occupancy Rate (getOccupancyRate)
 *
 * @author Ngo Thi Ngoc Lan (MOD5)
 */
@Service
public class ReportServiceImpl implements ReportService {

    /** Đơn vị làm tròn tiền tệ theo BR-FIN-05: VNĐ, không có chữ số thập phân. */
    private static final int CURRENCY_SCALE = 0;

    private final RoomRepository roomRepository;
    private final FolioItemRepository folioItemRepository;

    /**
     * Constructor injection — Spring tự động tiêm phụ thuộc.
     *
     * @param roomRepository       repository truy vấn dữ liệu phòng
     * @param folioItemRepository  repository truy vấn các khoản nợ Folio
     */
    public ReportServiceImpl(RoomRepository roomRepository,
                             FolioItemRepository folioItemRepository) {
        this.roomRepository = roomRepository;
        this.folioItemRepository = folioItemRepository;
    }

    // =========================================================================
    // UC26.1 — Dashboard tài chính
    // =========================================================================

    /**
     * Tổng hợp doanh thu phân loại theo nguồn bộ phận (UC26.1).
     *
     * Thuật toán: duyệt toàn bộ FolioItem, gom nhóm theo sourceDepartment,
     * cộng dồn amount bằng BigDecimal (BR-FIN-04, BR-FIN-05).
     *
     * @return Map không null với key là tên bộ phận, value là tổng doanh thu.
     */
    @Override
    public Map<String, BigDecimal> getDashboardFinancialData() {
        List<FolioItem> allItems = folioItemRepository.findAll();
        return aggregateRevenueByDepartment(allItems);
    }

    // =========================================================================
    // UC26.2 — Occupancy Rate
    // =========================================================================

    /**
     * Tính tỉ lệ phòng đang có khách lưu trú (UC26.2).
     *
     * Công thức: Occupancy Rate (%) = (occupiedRooms / totalRooms) × 100
     * Edge case: totalRooms = 0 → trả 0.0, không ném ArithmeticException.
     *
     * @return tỉ lệ phần trăm dạng double (ví dụ: 75.0 = 75%)
     */
    @Override
    public double getOccupancyRate() {
        long totalRooms = roomRepository.countTotalRooms();
        if (totalRooms == 0) {
            return 0.0;
        }
        long occupiedCount = roomRepository.findOccupied().size();
        return calculatePercentage(occupiedCount, totalRooms);
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    /**
     * Gom nhóm danh sách FolioItem theo sourceDepartment và cộng dồn amount (BR-FIN-05).
     *
     * @param items danh sách FolioItem cần xử lý
     * @return Map{department → totalAmount}
     */
    private Map<String, BigDecimal> aggregateRevenueByDepartment(List<FolioItem> items) {
        Map<String, BigDecimal> revenue = new HashMap<>();
        for (FolioItem item : items) {
            String dept = item.getSourceDepartment();
            BigDecimal accumulated = revenue.getOrDefault(dept, BigDecimal.ZERO)
                                            .add(item.getAmount())
                                            .setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
            revenue.put(dept, accumulated);
        }
        return revenue;
    }

    /**
     * Tính tỉ lệ phần trăm: (numerator / denominator) × 100.
     * Caller phải đảm bảo denominator > 0 trước khi gọi.
     *
     * @param numerator   số phòng OCCUPIED
     * @param denominator tổng số phòng (> 0)
     * @return tỉ lệ phần trăm dạng double
     */
    private double calculatePercentage(long numerator, long denominator) {
        return (double) numerator / (double) denominator * 100.0;
    }
}
