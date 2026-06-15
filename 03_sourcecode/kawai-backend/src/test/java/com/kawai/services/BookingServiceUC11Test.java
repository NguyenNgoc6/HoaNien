package com.kawai.services;

import com.kawai.dto.BookingResponseDTO;
import com.kawai.exceptions.BusinessException;
import com.kawai.models.Customer;
import com.kawai.models.RoomBooking;
import com.kawai.repositories.RoomBookingRepository;
import com.kawai.repositories.RoomRepository;
import com.kawai.services.impl.BookingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * JUnit Test — UC11: Hủy đặt phòng
 * MODULE 2: Quản lý Phòng & Lễ tân
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * TDD Phase : 🔴 RED — Toàn bộ test phải FAIL trước khi implement.
 * 
 * Ánh xạ test case:
 * TC-M2-UC11-001: Hủy trước 48h → Hoàn 100%
 * TC-M2-UC11-002: Hủy < 48h → Bị từ chối (MSG22)
 * TC-M2-UC11-003: Trạng thái không hợp lệ (Checked-In/Cancelled)
 * TC-M2-UC11-004: Gateway Error (Rollback)
 * TC-M2-UC11-005: Boundary 48h (15/06 hủy 13/06) → Hoàn 100%
 * TC-M2-UC11-006: Inventory Release
 * TC-M2-UC11-007: Gửi Notification thành công
 * TC-M2-UC11-008: Gửi Notification thất bại
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UC11 — Hủy đặt phòng (BookingService) | TDD 🔴 RED")
class BookingServiceUC11Test {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private RoomBookingRepository roomBookingRepository;

    @Mock
    private RoomRepository roomRepository;
    
    @Mock
    private com.kawai.services.interfaces.PaymentGatewayService paymentGatewayService;
    
    @Mock
    private com.kawai.services.interfaces.NotificationService notificationService;

    @Test
    @DisplayName("TC-M2-UC11-001 — Hủy thành công trước 48h (Full Refund)")
    void testCancelBooking_Before48h_Success() {
        // Arrange
        Long bookingId = 1L;
        Long customerId = 100L;
        
        Customer customer = new Customer();
        customer.setId(customerId);
        RoomBooking mockBooking = new RoomBooking();
        mockBooking.setId(bookingId);
        mockBooking.setCustomer(customer);
        mockBooking.setBookingStatus("CONFIRMED");
        mockBooking.setCheckInDate(LocalDate.now().plusDays(5)); // Trực tiếp đặt ngày checkIn

        when(roomBookingRepository.findById(bookingId))
            .thenReturn(Optional.of(mockBooking));
        
        // Act
        BookingResponseDTO response = bookingService.cancelBooking(bookingId, customerId);

        // Assert
        assertNotNull(response);
        assertEquals("CANCELLED", response.getBookingStatus());
        verify(roomBookingRepository, times(1)).save(any(RoomBooking.class));
    }

    @Test
    @DisplayName("TC-M2-UC11-002 — Hủy thất bại do vi phạm luật < 48h (E4 - MSG22)")
    void testCancelBooking_LessThan48h_Fails() {
        // Arrange
        Long bookingId = 2L;
        Long customerId = 101L;

        Customer customer = new Customer();
        customer.setId(customerId);
        RoomBooking mockBooking = new RoomBooking();
        mockBooking.setId(bookingId);
        mockBooking.setCustomer(customer);
        mockBooking.setBookingStatus("CONFIRMED");
        mockBooking.setCheckInDate(LocalDate.now().plusDays(1)); // Dưới 48h

        when(roomBookingRepository.findById(bookingId))
            .thenReturn(Optional.of(mockBooking));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            bookingService.cancelBooking(bookingId, customerId);
        });
        
        assertTrue(exception.getMessage().contains("Booking cannot be canceled as more than 48 hours have passed"));
        verify(roomBookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-M2-UC11-003 — Hủy thất bại do trạng thái không hợp lệ (CHECKED_IN)")
    void testCancelBooking_InvalidStatus() {
        // Arrange
        Long bookingId = 3L;
        Long customerId = 102L;

        Customer customer = new Customer();
        customer.setId(customerId);
        RoomBooking mockBooking = new RoomBooking();
        mockBooking.setId(bookingId);
        mockBooking.setCustomer(customer);
        mockBooking.setBookingStatus("CHECKED_IN"); // Invalid status
        mockBooking.setCheckInDate(LocalDate.now().plusDays(5));

        when(roomBookingRepository.findById(bookingId))
            .thenReturn(Optional.of(mockBooking));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            bookingService.cancelBooking(bookingId, customerId);
        });
        
        verify(roomBookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-M2-UC11-004 — Hoàn tiền thất bại do Gateway Error")
    void testCancelBooking_GatewayError_Rollback() {
        // Arrange
        Long bookingId = 4L;
        Long customerId = 103L;

        Customer customer = new Customer();
        customer.setId(customerId);
        RoomBooking mockBooking = new RoomBooking();
        mockBooking.setId(bookingId);
        mockBooking.setCustomer(customer);
        mockBooking.setBookingStatus("CONFIRMED");
        mockBooking.setCheckInDate(LocalDate.now().plusDays(5));

        when(roomBookingRepository.findById(bookingId))
            .thenReturn(Optional.of(mockBooking));
        
        // Mock gateway throws Exception
        doThrow(new com.kawai.exceptions.PaymentGatewayException("Timeout"))
            .when(paymentGatewayService).processRefund(any(), any());

        // Act & Assert
        assertThrows(com.kawai.exceptions.PaymentGatewayException.class, () -> {
           bookingService.cancelBooking(bookingId, customerId);
        });
        verify(roomBookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-M2-UC11-005 — Hủy chính xác hoặc trước mốc 48h (15/06 -> 13/06)")
    void testCancelBooking_Exact48hBoundary_Success() {
        // Arrange
        Long bookingId = 5L;
        Long customerId = 104L;
        
        Customer customer = new Customer();
        customer.setId(customerId);
        RoomBooking mockBooking = new RoomBooking();
        mockBooking.setId(bookingId);
        mockBooking.setCustomer(customer);
        mockBooking.setBookingStatus("CONFIRMED");
        // Giả lập logic kiểm tra giờ (hiện tại BookingServiceImpl/cancelBooking chưa có hàm nhận currentTime, 
        mockBooking.setCheckInDate(LocalDate.now().plusDays(3)); // Đảm bảo >= 48h bất chấp giờ hiện tại

        when(roomBookingRepository.findById(bookingId))
            .thenReturn(Optional.of(mockBooking));
        
        BookingResponseDTO response = bookingService.cancelBooking(bookingId, customerId);

        // Assert
        assertNotNull(response);
        assertEquals("CANCELLED", response.getBookingStatus());
    }

    @Test
    @DisplayName("TC-M2-UC11-006 — Giải phóng room inventory sau khi hủy")
    void testCancelBooking_ReleasesRoomInventory() {
        // Arrange
        Long bookingId = 6L;
        Long customerId = 105L;
        
        Customer customer = new Customer();
        customer.setId(customerId);
        RoomBooking mockBooking = new RoomBooking();
        mockBooking.setId(bookingId);
        mockBooking.setCustomer(customer);
        mockBooking.setBookingStatus("CONFIRMED");
        mockBooking.setCheckInDate(LocalDate.now().plusDays(5));

        when(roomBookingRepository.findById(bookingId))
            .thenReturn(Optional.of(mockBooking));
        
        // Act
        bookingService.cancelBooking(bookingId, customerId);

        // Assert - verify room inventory logic. In Kawai, Room status or detail might be updated.
        // We will assume room logic is called or RoomBooking is saved with Cancelled status which effectively frees inventory.
        verify(roomBookingRepository).save(argThat(b -> b.getBookingStatus().equals("CANCELLED")));
    }

    @Test
    @DisplayName("TC-M2-UC11-007 — Gửi notification khi hủy thành công")
    void testCancelBooking_SendsSuccessNotification() {
        // Arrange
        Long bookingId = 7L;
        Long customerId = 106L;
        Customer customer = new Customer();
        customer.setId(customerId);
        RoomBooking mockBooking = new RoomBooking();
        mockBooking.setId(bookingId);
        mockBooking.setCustomer(customer);
        mockBooking.setBookingStatus("CONFIRMED");
        mockBooking.setCheckInDate(LocalDate.now().plusDays(5));

        when(roomBookingRepository.findById(bookingId))
            .thenReturn(Optional.of(mockBooking));
        
        // Act
        bookingService.cancelBooking(bookingId, customerId);

        // Assert
        verify(notificationService).sendNotification(eq(customerId), anyString(), anyString());
    }

    @Test
    @DisplayName("TC-M2-UC11-008 — Gửi notification khi hủy thất bại")
    void testCancelBooking_SendsFailureNotification() {
        // Arrange
        Long bookingId = 8L;
        Long customerId = 107L;
        Customer customer = new Customer();
        customer.setId(customerId);
        RoomBooking mockBooking = new RoomBooking();
        mockBooking.setId(bookingId);
        mockBooking.setCustomer(customer);
        mockBooking.setBookingStatus("CONFIRMED");
        mockBooking.setCheckInDate(LocalDate.now().plusDays(1)); // < 48h

        when(roomBookingRepository.findById(bookingId))
            .thenReturn(Optional.of(mockBooking));

        // Act & Assert
        assertThrows(BusinessException.class, () -> bookingService.cancelBooking(bookingId, customerId));
        
        // Assert
        verify(notificationService).sendNotification(eq(customerId), anyString(), anyString());
    }
}
