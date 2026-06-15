package com.kawai.services;

import com.kawai.dto.RoomSearchRequestDTO;
import com.kawai.dto.RoomSearchResponseDTO;
import com.kawai.models.Room;
import com.kawai.models.RoomCategory;
import com.kawai.repositories.RoomBookingRepository;
import com.kawai.repositories.RoomRepository;
import com.kawai.services.impl.RoomServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * JUnit Test — UC09: Tìm kiếm phòng trống thời gian thực
 * MODULE 2: Quản lý Phòng & Lễ tân
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Standard : ISO/IEC/IEEE 29119-3:2021
 * TDD Phase : 🔴 RED — Toàn bộ test phải FAIL trước khi implement.
 *
 * Ánh xạ test case (TC_MASTER_TABLE.md — MOD2, UC09):
 * ┌──────────────┬─────────────────────────────────────────────────────────┬──────────┐
 * │ TC ID │ Kịch bản │ RED Why │
 * ├──────────────┼─────────────────────────────────────────────────────────┼──────────┤
 * │ TC-M2-001 │ Tìm phòng trống đúng theo ngày nhận/trả — trả danh sách │
 * COMPILE │
 * │ TC-M2-002 │ Không có phòng trống — trả danh sách rỗng │ FAIL │
 * └──────────────┴─────────────────────────────────────────────────────────┴──────────┘
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UC09 — Tìm kiếm phòng trống (RoomService) | TDD 🔴 RED")
class RoomServiceUC09Test {

        // ── SUT ───────────────────────────────────────────────────────────────────
        @InjectMocks
        private RoomServiceImpl roomService;

        // ── Mocks ─────────────────────────────────────────────────────────────────
        @Mock
        private RoomRepository roomRepository;

        @Mock
        private RoomBookingRepository roomBookingRepository;

        // ── Test Fixtures ─────────────────────────────────────────────────────────
        private static final LocalDate CHECK_IN = LocalDate.of(2026, 8, 1);
        private static final LocalDate CHECK_OUT = LocalDate.of(2026, 8, 5);

        private Room createRoom(Long id, String roomNo, String categoryName, BigDecimal price, int capacity) {
                RoomCategory cat = new RoomCategory();
                cat.setId(id);
                cat.setCategoryName(categoryName);
                cat.setBasePrice(price);
                cat.setCapacity(capacity);

                Room room = new Room();
                room.setId(id);
                room.setRoomNumber(roomNo);
                room.setCategory(cat);
                room.setRoomStatus("Vacant_Clean");
                return room;
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-001 | HIGH | Tìm phòng trống — có kết quả
        // ══════════════════════════════════════════════════════════════════════════

        /**
         * TC-M2-001 — Tìm phòng trống đúng theo ngày nhận/trả.
         *
         * Expected:
         * - Trả về danh sách các phòng trống trong khoảng [checkIn, checkOut)
         * - Mỗi phòng có thông tin: roomNumber, categoryName, pricePerNight, capacity
         * - Giá phòng lấy từ DailyRate.computedPrice
         *
         * 🔴 RED — COMPILE ERROR:
         * RoomServiceImpl chưa có method searchAvailableRooms().
         * → Trình biên dịch báo: cannot find symbol
         *
         * Fix cần làm:
         * Thêm method searchAvailableRooms() vào RoomService interface
         * và RoomServiceImpl.
         */
        @Test
        @DisplayName("TC-M2-001 | HIGH | Tìm phòng trống — trả danh sách phòng khả dụng")
        void TC_M2_001_searchAvailableRooms_returnsAvailableRooms() {
                // Arrange
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);

                Room room1 = createRoom(1L, "R101", "Deluxe", new BigDecimal("2000000"), 2);
                Room room2 = createRoom(2L, "R201", "Suite", new BigDecimal("3500000"), 4);
                List<Room> allRooms = Arrays.asList(room1, room2);

                when(roomRepository.findAll()).thenReturn(allRooms);
                // Không có booking nào overlapping
                when(roomBookingRepository.countOverlappingBookings(anyString(), any(), any()))
                                .thenReturn(0L);

                // Act
                List<RoomSearchResponseDTO> result = roomService.searchAvailableRooms(request);

                // Assert
                assertNotNull(result, "Danh sách kết quả không được null");
                assertEquals(2, result.size(), "Phải trả 2 phòng trống");

                // Verify phòng R101
                RoomSearchResponseDTO r1 = result.stream()
                                .filter(r -> "R101".equals(r.getRoomNumber()))
                                .findFirst().orElse(null);
                assertNotNull(r1, "R101 phải có trong kết quả");
                assertEquals("Deluxe", r1.getCategoryName(), "Category của R101 phải là Deluxe");
                assertEquals(new BigDecimal("2000000"), r1.getPricePerNight(),
                                "Giá R101 phải là 2,000,000/đêm");
                assertEquals(Integer.valueOf(2), r1.getCapacity(), "Sức chứa R101 phải là 2");

                // Verify phòng R201
                RoomSearchResponseDTO r2 = result.stream()
                                .filter(r -> "R201".equals(r.getRoomNumber()))
                                .findFirst().orElse(null);
                assertNotNull(r2, "R201 phải có trong kết quả");
                assertEquals("Suite", r2.getCategoryName(), "Category của R201 phải là Suite");
                assertEquals(new BigDecimal("3500000"), r2.getPricePerNight(),
                                "Giá R201 phải là 3,500,000/đêm");
                assertEquals(Integer.valueOf(4), r2.getCapacity(), "Sức chứa R201 phải là 4");

                // Verify dates
                assertEquals(CHECK_IN, r1.getCheckInDate(), "CheckIn phải được set");
                assertEquals(CHECK_OUT, r1.getCheckOutDate(), "CheckOut phải được set");
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002 | MEDIUM | Không có phòng trống → danh sách rỗng
        // ══════════════════════════════════════════════════════════════════════════

        /**
         * TC-M2-002 — Không có phòng trống → trả danh sách rỗng.
         *
         * Expected:
         * - Trả về danh sách rỗng (không null)
         *
         * 🔴 RED — RUNTIME FAILURE:
         * Implementation trả về null thay vì Collections.emptyList()
         * → assertNotNull hoặc kiểm tra size sẽ FAIL.
         *
         * Fix cần làm:
         * Đảm bảo luôn trả về empty list (không null) khi không có phòng trống.
         */
        @Test
        @DisplayName("TC-M2-002 | MEDIUM | Không có phòng trống → trả danh sách rỗng")
        void TC_M2_002_searchAvailableRooms_noRooms_returnsEmptyList() {
                // Arrange
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);

                when(roomRepository.findAll()).thenReturn(Collections.emptyList());

                // Act
                List<RoomSearchResponseDTO> result = roomService.searchAvailableRooms(request);

                // Assert
                // 🔴 RED — FAIL: nếu trả về null thay vì empty list
                assertNotNull(result, "Kết quả không được null");
                assertTrue(result.isEmpty(), "Danh sách phải rỗng khi không có phòng nào");
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002b | MEDIUM | Tất cả phòng đều bị booked → danh sách rỗng
        // ══════════════════════════════════════════════════════════════════════════

        /**
         * TC-M2-002b — Tất cả phòng đều có booking overlapping → danh sách rỗng.
         *
         * 🔴 RED — RUNTIME FAILURE:
         * Implementation không lọc booking overlapping đúng cách.
         */
        @Test
        @DisplayName("TC-M2-002b | MEDIUM | Tất cả phòng đều bận → trả danh sách rỗng")
        void TC_M2_002b_searchAvailableRooms_allRoomsBooked_returnsEmptyList() {
                // Arrange
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);

                Room room1 = createRoom(1L, "R101", "Deluxe", new BigDecimal("2000000"), 2);
                when(roomRepository.findAll()).thenReturn(Arrays.asList(room1));
                // R101 bị overlapping booking
                when(roomBookingRepository.countOverlappingBookings(eq("R101"), any(), any()))
                                .thenReturn(1L);

                // Act
                List<RoomSearchResponseDTO> result = roomService.searchAvailableRooms(request);

                // Assert
                assertNotNull(result, "Kết quả không được null");
                assertTrue(result.isEmpty(), "Phải trả danh sách rỗng vì R101 đã có booking");
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002c | MEDIUM | Lọc bỏ phòng đang Bảo trì hoặc Ngừng hoạt động
        // ══════════════════════════════════════════════════════════════════════════
        @Test
        @DisplayName("TC-M2-002c | MEDIUM | Phòng đang Bảo trì/Lỗi → loại khỏi kết quả")
        void TC_M2_002c_searchAvailableRooms_filterOutMaintenanceAndOutOfServiceRooms() {
                // Arrange
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);

                Room room1 = createRoom(1L, "R101", "Deluxe", new BigDecimal("2000000"), 2);
                room1.setRoomStatus("Maintenance");

                Room room2 = createRoom(2L, "R102", "Suite", new BigDecimal("3500000"), 4);
                room2.setRoomStatus("OutOfService");

                Room room3 = createRoom(3L, "R103", "Standard", new BigDecimal("1000000"), 2);
                room3.setRoomStatus("Vacant_Clean");

                when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2, room3));
                when(roomBookingRepository.countOverlappingBookings(eq("R103"), any(), any())).thenReturn(0L);

                // Act
                List<RoomSearchResponseDTO> result = roomService.searchAvailableRooms(request);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals("R103", result.get(0).getRoomNumber());
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002d | LOW | Lọc theo hạng phòng
        // ══════════════════════════════════════════════════════════════════════════
        @Test
        @DisplayName("TC-M2-002d | LOW | Lọc theo hạng phòng categoryName")
        void TC_M2_002d_searchAvailableRooms_filterByCategoryName() {
                // Arrange
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);
                request.setCategoryName("Deluxe");

                Room room1 = createRoom(1L, "R101", "Deluxe", new BigDecimal("2000000"), 2);
                Room room2 = createRoom(2L, "R201", "Suite", new BigDecimal("3500000"), 4);

                when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2));
                when(roomBookingRepository.countOverlappingBookings(eq("R101"), any(), any())).thenReturn(0L);

                // Act
                List<RoomSearchResponseDTO> result = roomService.searchAvailableRooms(request);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals("R101", result.get(0).getRoomNumber());
                assertEquals("Deluxe", result.get(0).getCategoryName());
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002e | LOW | Lọc theo sức chứa tối thiểu
        // ══════════════════════════════════════════════════════════════════════════
        @Test
        @DisplayName("TC-M2-002e | LOW | Lọc theo sức chứa tối thiểu minCapacity")
        void TC_M2_002e_searchAvailableRooms_filterByMinCapacity() {
                // Arrange
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);
                request.setMinCapacity(3);

                Room room1 = createRoom(1L, "R101", "Deluxe", new BigDecimal("2000000"), 2);
                Room room2 = createRoom(2L, "R201", "Suite", new BigDecimal("3500000"), 4);

                when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2));
                when(roomBookingRepository.countOverlappingBookings(eq("R201"), any(), any())).thenReturn(0L);

                // Act
                List<RoomSearchResponseDTO> result = roomService.searchAvailableRooms(request);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals("R201", result.get(0).getRoomNumber());
                assertTrue(result.get(0).getCapacity() >= 3);
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002f | LOW | Lọc theo mức giá tối đa
        // ══════════════════════════════════════════════════════════════════════════
        @Test
        @DisplayName("TC-M2-002f | LOW | Lọc theo giá tối đa maxPricePerNight")
        void TC_M2_002f_searchAvailableRooms_filterByMaxPrice() {
                // Arrange
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);
                request.setMaxPricePerNight(new BigDecimal("2500000"));

                Room room1 = createRoom(1L, "R101", "Deluxe", new BigDecimal("2000000"), 2);
                Room room2 = createRoom(2L, "R201", "Suite", new BigDecimal("3500000"), 4);

                when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2));
                when(roomBookingRepository.countOverlappingBookings(eq("R101"), any(), any())).thenReturn(0L);

                // Act
                List<RoomSearchResponseDTO> result = roomService.searchAvailableRooms(request);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals("R101", result.get(0).getRoomNumber());
                assertTrue(result.get(0).getPricePerNight().compareTo(new BigDecimal("2500000")) <= 0);
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002g | HIGH | Validate ngày check-in > check-out (TC-COND-008)
        // ══════════════════════════════════════════════════════════════════════════
        @Test
        @DisplayName("TC-M2-002g | HIGH | Check-in sau Check-out → ném lỗi IllegalArgumentException")
        void TC_M2_002g_searchAvailableRooms_checkInAfterCheckOut_throwsException() {
                // Arrange
                LocalDate invalidCheckIn = LocalDate.of(2026, 8, 10);
                LocalDate invalidCheckOut = LocalDate.of(2026, 8, 5);
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(invalidCheckIn, invalidCheckOut);

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> {
                        roomService.searchAvailableRooms(request);
                }, "Phải ném IllegalArgumentException khi check-in sau check-out");
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002h | MEDIUM | Tìm kiếm kết hợp (Category + Price + Capacity) (TC-COND-009)
        // ══════════════════════════════════════════════════════════════════════════
        @Test
        @DisplayName("TC-M2-002h | MEDIUM | Tìm kiếm kết hợp nhiều bộ lọc")
        void TC_M2_002h_searchAvailableRooms_combinedFilters() {
                // Arrange
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);
                request.setCategoryName("Deluxe");
                request.setMinCapacity(2);
                request.setMaxPricePerNight(new BigDecimal("2500000"));

                Room room1 = createRoom(1L, "R101", "Deluxe", new BigDecimal("2000000"), 2); // Thỏa mãn
                Room room2 = createRoom(2L, "R102", "Deluxe", new BigDecimal("3000000"), 2); // Giá quá cao
                Room room3 = createRoom(3L, "R201", "Suite", new BigDecimal("2200000"), 4);   // Sai category

                when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2, room3));
                when(roomBookingRepository.countOverlappingBookings(eq("R101"), any(), any())).thenReturn(0L);

                // Act
                List<RoomSearchResponseDTO> result = roomService.searchAvailableRooms(request);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals("R101", result.get(0).getRoomNumber());
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002i | MEDIUM | Tìm kiếm với maxPrice quá thấp (không có phòng) (TC-COND-010)
        // ══════════════════════════════════════════════════════════════════════════
        @Test
        @DisplayName("TC-M2-002i | MEDIUM | Giá maxPrice quá thấp → trả danh sách rỗng, không lỗi")
        void TC_M2_002i_searchAvailableRooms_maxPriceTooLow_returnsEmpty() {
                // Arrange
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);
                request.setMaxPricePerNight(new BigDecimal("500000")); // Quá thấp

                Room room1 = createRoom(1L, "R101", "Deluxe", new BigDecimal("2000000"), 2);
                when(roomRepository.findAll()).thenReturn(Arrays.asList(room1));

                // Act
                List<RoomSearchResponseDTO> result = roomService.searchAvailableRooms(request);

                // Assert
                assertNotNull(result);
                assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng");
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002j | HIGH | Tìm kiếm với đầu vào là giá trị âm (TC-COND-011)
        // ══════════════════════════════════════════════════════════════════════════
        @Test
        @DisplayName("TC-M2-002j | HIGH | Đầu vào âm (capacity hoặc price) → ném lỗi")
        void TC_M2_002j_searchAvailableRooms_negativeInput_throwsException() {
                // Arrange 1: minCapacity âm
                RoomSearchRequestDTO request1 = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);
                request1.setMinCapacity(-1);

                // Arrange 2: maxPrice âm
                RoomSearchRequestDTO request2 = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);
                request2.setMaxPricePerNight(new BigDecimal("-10000"));

                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> roomService.searchAvailableRooms(request1));
                assertThrows(IllegalArgumentException.class, () -> roomService.searchAvailableRooms(request2));
        }

        // ══════════════════════════════════════════════════════════════════════════
        // TC-M2-002k | MEDIUM | Phân trang kết quả tìm kiếm (TC-COND-012)
        // ══════════════════════════════════════════════════════════════════════════
        @Test
        @DisplayName("TC-M2-002k | MEDIUM | Phân trang kết quả tìm kiếm đúng vị trí và kích thước")
        void TC_M2_002k_searchAvailableRooms_pagination() {
                // Arrange: Yêu cầu trang 1, kích thước trang 2
                RoomSearchRequestDTO request = new RoomSearchRequestDTO(CHECK_IN, CHECK_OUT);
                request.setPage(1);
                request.setSize(2);

                Room room1 = createRoom(1L, "R101", "Deluxe", new BigDecimal("2000000"), 2);
                Room room2 = createRoom(2L, "R102", "Deluxe", new BigDecimal("2000000"), 2);
                Room room3 = createRoom(3L, "R103", "Deluxe", new BigDecimal("2000000"), 2);
                Room room4 = createRoom(4L, "R104", "Deluxe", new BigDecimal("2000000"), 2);

                when(roomRepository.findAll()).thenReturn(Arrays.asList(room1, room2, room3, room4));
                when(roomBookingRepository.countOverlappingBookings(anyString(), any(), any())).thenReturn(0L);

                // Act
                List<RoomSearchResponseDTO> result = roomService.searchAvailableRooms(request);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.size(), "Trang 1 size 2 phải trả về đúng 2 kết quả");
                assertEquals("R103", result.get(0).getRoomNumber(), "Phần tử đầu của trang 1 phải là R103");
                assertEquals("R104", result.get(1).getRoomNumber(), "Phần tử thứ hai của trang 1 phải là R104");
        }
}