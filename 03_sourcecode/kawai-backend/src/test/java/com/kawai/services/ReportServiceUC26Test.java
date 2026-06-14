package com.kawai.services;

import com.kawai.models.FolioItem;
import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.RoomRepository;
import com.kawai.services.impl.ReportServiceImpl;
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

@ExtendWith(MockitoExtension.class)
public class ReportServiceUC26Test {

    @Mock
    private FolioItemRepository folioItemRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    // ── TC-M5-010: getDashboardFinancialData ─────────────────────────────────

    // Kịch bản 1: Có 3 khoản Folio với 3 nguồn khác nhau
    @Test
    void testGetDashboardFinancialData_ReturnsRevenueByDepartment() {
        FolioItem roomItem  = buildItem("ROOM", new BigDecimal("2000000"));
        FolioItem fbItem    = buildItem("FB",   new BigDecimal("500000"));
        FolioItem tourItem  = buildItem("TOUR", new BigDecimal("800000"));
        when(folioItemRepository.findAll()).thenReturn(Arrays.asList(roomItem, fbItem, tourItem));

        Map<String, BigDecimal> result = reportService.getDashboardFinancialData();

        assertEquals(new BigDecimal("2000000"), result.get("ROOM"));
        assertEquals(new BigDecimal("500000"),  result.get("FB"));
        assertEquals(new BigDecimal("800000"),  result.get("TOUR"));
    }

    // Kịch bản 2: Hai khoản cùng nguồn ROOM → cộng dồn đúng
    @Test
    void testGetDashboardFinancialData_AccumulatesSameDepartment() {
        FolioItem item1 = buildItem("ROOM", new BigDecimal("1000000"));
        FolioItem item2 = buildItem("ROOM", new BigDecimal("500000"));
        when(folioItemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        Map<String, BigDecimal> result = reportService.getDashboardFinancialData();

        assertEquals(new BigDecimal("1500000"), result.get("ROOM"));
        assertEquals(1, result.size());
    }

    // Kịch bản 3: Không có Folio → trả Map rỗng
    @Test
    void testGetDashboardFinancialData_EmptyWhenNoFolioItems() {
        when(folioItemRepository.findAll()).thenReturn(Collections.emptyList());

        Map<String, BigDecimal> result = reportService.getDashboardFinancialData();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── TC-M5-011: getOccupancyRate ───────────────────────────────────────────

    // Kịch bản 4: 3 phòng OCCUPIED / 4 tổng → 75%
    @Test
    void testGetOccupancyRate_ReturnsCorrectPercentage() {
        when(roomRepository.countTotalRooms()).thenReturn(4L);
        when(roomRepository.findOccupied()).thenReturn(List.of(
            new com.kawai.models.Room(),
            new com.kawai.models.Room(),
            new com.kawai.models.Room()
        ));

        double rate = reportService.getOccupancyRate();

        assertEquals(75.0, rate, 0.001);
    }

    // Kịch bản 5: 0 phòng OCCUPIED / 4 tổng → 0%
    @Test
    void testGetOccupancyRate_ZeroOccupied() {
        when(roomRepository.countTotalRooms()).thenReturn(4L);
        when(roomRepository.findOccupied()).thenReturn(Collections.emptyList());

        double rate = reportService.getOccupancyRate();

        assertEquals(0.0, rate, 0.001);
    }

    // Kịch bản 6: Tất cả phòng đều OCCUPIED → 100%
    @Test
    void testGetOccupancyRate_AllRoomsOccupied() {
        when(roomRepository.countTotalRooms()).thenReturn(3L);
        when(roomRepository.findOccupied()).thenReturn(List.of(
            new com.kawai.models.Room(),
            new com.kawai.models.Room(),
            new com.kawai.models.Room()
        ));

        double rate = reportService.getOccupancyRate();

        assertEquals(100.0, rate, 0.001);
    }

    // Kịch bản 7 (Edge case): totalRooms = 0 → trả 0.0, không ném exception
    @Test
    void testGetOccupancyRate_NoRoomsInSystem_ReturnsZero() {
        when(roomRepository.countTotalRooms()).thenReturn(0L);

        double rate = reportService.getOccupancyRate();

        assertEquals(0.0, rate, 0.001, "Should return 0.0 when no rooms exist to avoid division by zero");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private FolioItem buildItem(String department, BigDecimal amount) {
        FolioItem item = new FolioItem();
        item.setSourceDepartment(department);
        item.setAmount(amount);
        return item;
    }
}
