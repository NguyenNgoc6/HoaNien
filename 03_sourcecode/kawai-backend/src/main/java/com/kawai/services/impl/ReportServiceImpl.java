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
 * <p>Business Rules tuân thủ:
 * <ul>
 *   <li>BR-FIN-04: Phân loại doanh thu USALI — 3 nguồn: REV-ROOM / REV-FB / REV-TOUR.</li>
 *   <li>BR-FIN-05: Toàn bộ phép tính tiền sử dụng {@link BigDecimal} + {@link RoundingMode#HALF_UP}.</li>
 * </ul>
 *
 * <p>Use Cases được phục vụ:
 * <ul>
 *   <li>UC26.1 — Giám sát biểu đồ tài chính ({@link #getDashboardFinancialData()})</li>
 *   <li>UC26.2 — Xem Occupancy Rate ({@link #getOccupancyRate()})</li>
 * </ul>
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
     * <p>Thuật toán: duyệt toàn bộ {@link FolioItem}, gom nhóm theo
     * {@code sourceDepartment}, cộng dồn {@code amount} bằng BigDecimal.
     * Kết quả key tuân theo chuẩn USALI (BR-FIN-04):
     * <ul>
     *   <li>{@code "ROOM"}  → REV-ROOM  (tiền phòng, minibar)</li>
     *   <li>{@code "FB"}    → REV-FB    (nhà hàng, room service)</li>
     *   <li>{@code "TOUR"}  → REV-TOUR  (gói lữ hành)</li>
     * </ul>
     *
     * @return Map không null với key là tên bộ phận và value là tổng doanh thu.
     *         Trả về Map rỗng khi chưa có dữ liệu Folio.
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
     * <p>Công thức: {@code Occupancy Rate (%) = (occupiedRooms / totalRooms) × 100}
     *
     * <p>Edge case — tránh chia cho 0: khi Resort chưa có phòng nào (totalRooms = 0),
     * phương thức trả về {@code 0.0} thay vì ném {@link ArithmeticException}.
     *
     * @return tỉ lệ phần trăm dạng {@code double}. Ví dụ: {@code 75.0} = 75 %.
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
    // Private helpers — tái sử dụng, không trùng lặp
    // =========================================================================

    /**
     * Gom nhóm danh sách FolioItem theo {@code sourceDepartment} và cộng dồn
     * {@code amount} bằng BigDecimal với RoundingMode.HALF_UP (BR-FIN-05).
     *
     * @param items danh sách FolioItem cần xử lý (có thể rỗng)
     * @return Map{department → totalAmount}; không bao giờ null
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
     * Tính tỉ lệ phần trăm: {@code (numerator / denominator) × 100}.
     * Caller phải đảm bảo {@code denominator > 0} trước khi gọi hàm này.
     *
     * @param numerator   số lượng phòng OCCUPIED
     * @param denominator tổng số phòng (phải > 0)
     * @return tỉ lệ phần trăm dạng double
     */
    private double calculatePercentage(long numerator, long denominator) {
        return (double) numerator / (double) denominator * 100.0;
    }
}
