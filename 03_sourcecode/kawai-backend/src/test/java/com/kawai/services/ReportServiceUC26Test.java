package com.kawai.services;

import com.kawai.models.Room;
import com.kawai.models.FolioItem;
import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.RoomRepository;
import com.kawai.services.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ReportService — Module 5 Finance & Dashboard
 * Feature Under Test:
 *   TC-M5-010: UC26.1 — Dashboard trả dữ liệu biểu đồ tài chính đúng
 *   TC-M5-011: UC26.2 — Occupancy Rate = (phòng OCCUPIED / tổng phòng) × 100%
 *
 * Severity:
 *   TC-M5-010: MEDIUM
 *   TC-M5-011: MEDIUM
 *
 * Author: Ngo Thi Ngoc Lan (MOD5)
 * Date: 2026-06-14
 */
@ExtendWith(MockitoExtension.class)
public class ReportServiceUC26Test {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private FolioItemRepository folioItemRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    // ============================================================
    // === HELPER: Build a mock FolioItem with a given source dept and amount
    // ============================================================
    private FolioItem buildFolioItem(String sourceDepartment, BigDecimal amount) {
        FolioItem item = new FolioItem();
        item.setSourceDepartment(sourceDepartment);
        item.setAmount(amount);
        item.setIsSettledSeparately(false);
        item.setDescription("Test folio item");
        return item;
    }

    // ============================================================
    // === HELPER: Build a mock Room with a given status
    // ============================================================
    private Room buildRoom(String status) {
        Room room = new Room();
        room.setRoomStatus(status);
        return room;
    }

    // ============================================================
    // TC-M5-010 — Dashboard trả dữ liệu biểu đồ tài chính đúng (UC26.1)
    // ============================================================

    /**
     * Kịch bản 1: Có folio items từ cả 3 nguồn (ROOM, FB, TOUR).
     * Kết quả mong đợi: Map trả về đúng tổng BigDecimal cho từng key.
     */
    @Test
    @DisplayName("TC-M5-010-01: Dashboard tài chính — có đủ 3 nguồn doanh thu → tổng đúng BigDecimal")
    void testGetDashboardFinancialData_AllDepartments_ReturnsCorrectTotals() {
        // Arrange — 3 ROOM, 2 FB, 1 TOUR
        List<FolioItem> mockFolios = Arrays.asList(
            buildFolioItem("ROOM", new BigDecimal("1500000")),
            buildFolioItem("ROOM", new BigDecimal("2000000")),
            buildFolioItem("ROOM", new BigDecimal("500000")),
            buildFolioItem("FB",   new BigDecimal("350000")),
            buildFolioItem("FB",   new BigDecimal("150000")),
            buildFolioItem("TOUR", new BigDecimal("800000"))
        );
        when(folioItemRepository.findAll()).thenReturn(mockFolios);

        // Act
        Map<String, BigDecimal> result = reportService.getDashboardFinancialData();

        // Assert
        assertNotNull(result, "Result map must not be null");
        assertEquals(3, result.size(), "Should have exactly 3 revenue sources");

        assertEquals(new BigDecimal("4000000"), result.get("ROOM"),
            "ROOM revenue should be 1500000 + 2000000 + 500000 = 4000000");
        assertEquals(new BigDecimal("500000"), result.get("FB"),
            "FB revenue should be 350000 + 150000 = 500000");
        assertEquals(new BigDecimal("800000"), result.get("TOUR"),
            "TOUR revenue should be 800000");

        verify(folioItemRepository, times(1)).findAll();
    }

    /**
     * Kịch bản 2: Không có folio item nào → tất cả doanh thu = 0 (không ném exception).
     */
    @Test
    @DisplayName("TC-M5-010-02: Dashboard tài chính — không có folio item → trả về Map rỗng hoặc 0")
    void testGetDashboardFinancialData_NoFolioItems_ReturnsZeroOrEmpty() {
        // Arrange
        when(folioItemRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        Map<String, BigDecimal> result = reportService.getDashboardFinancialData();

        // Assert
        assertNotNull(result, "Result map must not be null even when no data");
        // Tổng mỗi key phải là 0 hoặc key không xuất hiện (không được null value)
        result.forEach((key, value) ->
            assertNotNull(value, "No revenue source should map to null value"));

        verify(folioItemRepository, times(1)).findAll();
    }

    /**
     * Kịch bản 3: Chỉ có doanh thu từ 1 nguồn (ROOM) → chỉ trả đúng nguồn đó.
     */
    @Test
    @DisplayName("TC-M5-010-03: Dashboard tài chính — chỉ có ROOM folio → không có FB/TOUR")
    void testGetDashboardFinancialData_OnlyRoomItems_ReturnsOnlyRoomRevenue() {
        // Arrange
        List<FolioItem> mockFolios = Arrays.asList(
            buildFolioItem("ROOM", new BigDecimal("3000000")),
            buildFolioItem("ROOM", new BigDecimal("1000000"))
        );
        when(folioItemRepository.findAll()).thenReturn(mockFolios);

        // Act
        Map<String, BigDecimal> result = reportService.getDashboardFinancialData();

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("4000000"), result.get("ROOM"),
            "ROOM total should be 3000000 + 1000000");
        // FB và TOUR không được xuất hiện với giá trị > 0
        BigDecimal fbRevenue = result.getOrDefault("FB", BigDecimal.ZERO);
        BigDecimal tourRevenue = result.getOrDefault("TOUR", BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, fbRevenue, "FB revenue should be 0 when no FB folios");
        assertEquals(BigDecimal.ZERO, tourRevenue, "TOUR revenue should be 0 when no TOUR folios");
    }

    // ============================================================
    // TC-M5-011 — Occupancy Rate (UC26.2)
    // Công thức: occupiedRooms / totalRooms * 100
    // ============================================================

    /**
     * Kịch bản 1: 3/4 phòng đang OCCUPIED → occupancy rate = 75.0%
     */
    @Test
    @DisplayName("TC-M5-011-01: Occupancy Rate — 3 OCCUPIED / 4 tổng → 75.0%")
    void testGetOccupancyRate_ThreeOutOfFourOccupied_Returns75Percent() {
        // Arrange
        List<Room> occupiedRooms = Arrays.asList(
            buildRoom("Occupied"),
            buildRoom("Occupied"),
            buildRoom("Occupied")
        );
        when(roomRepository.findOccupied()).thenReturn(occupiedRooms);
        when(roomRepository.countTotalRooms()).thenReturn(4L);

        // Act
        double result = reportService.getOccupancyRate();

        // Assert
        assertEquals(75.0, result, 0.001,
            "Occupancy rate should be (3/4)*100 = 75.0%");

        verify(roomRepository, times(1)).findOccupied();
        verify(roomRepository, times(1)).countTotalRooms();
    }

    /**
     * Kịch bản 2: Không có phòng nào OCCUPIED → 0.0%
     */
    @Test
    @DisplayName("TC-M5-011-02: Occupancy Rate — 0 phòng OCCUPIED → 0.0%")
    void testGetOccupancyRate_NoOccupiedRooms_ReturnsZero() {
        // Arrange
        when(roomRepository.findOccupied()).thenReturn(Collections.emptyList());
        when(roomRepository.countTotalRooms()).thenReturn(10L);

        // Act
        double result = reportService.getOccupancyRate();

        // Assert
        assertEquals(0.0, result, 0.001,
            "Occupancy rate should be 0.0% when no rooms are occupied");
    }

    /**
     * Kịch bản 3: Toàn bộ phòng đều OCCUPIED → 100.0%
     */
    @Test
    @DisplayName("TC-M5-011-03: Occupancy Rate — tất cả phòng OCCUPIED → 100.0%")
    void testGetOccupancyRate_AllOccupied_Returns100Percent() {
        // Arrange
        List<Room> allOccupied = Arrays.asList(
            buildRoom("Occupied"),
            buildRoom("Occupied"),
            buildRoom("Occupied"),
            buildRoom("Occupied"),
            buildRoom("Occupied")
        );
        when(roomRepository.findOccupied()).thenReturn(allOccupied);
        when(roomRepository.countTotalRooms()).thenReturn(5L);

        // Act
        double result = reportService.getOccupancyRate();

        // Assert
        assertEquals(100.0, result, 0.001,
            "Occupancy rate should be 100.0% when all rooms are occupied");
    }

    /**
     * Kịch bản 4 (Edge Case): Không có phòng nào trong hệ thống → tránh chia cho 0.
     * Expected: trả về 0.0 thay vì ném ArithmeticException.
     * Note: khi totalRooms = 0, impl trả về sớm — không gọi findOccupied().
     */
    @Test
    @DisplayName("TC-M5-011-04: Occupancy Rate — 0 tổng phòng (edge case) → 0.0% không crash")
    void testGetOccupancyRate_ZeroTotalRooms_ReturnsZeroWithoutException() {
        // Arrange — chỉ mock countTotalRooms, KHÔNG mock findOccupied vì impl trả về sớm
        when(roomRepository.countTotalRooms()).thenReturn(0L);

        // Act & Assert — không được ném exception
        assertDoesNotThrow(() -> {
            double result = reportService.getOccupancyRate();
            assertEquals(0.0, result, 0.001,
                "Should return 0.0 when there are no rooms to avoid division by zero");
        });
    }
}
