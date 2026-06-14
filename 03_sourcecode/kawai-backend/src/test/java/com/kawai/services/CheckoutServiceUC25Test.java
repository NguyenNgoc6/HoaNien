package com.kawai.services;

import com.kawai.models.FolioItem;
import com.kawai.models.RoomBookingDetail;
import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import com.kawai.services.interfaces.CheckoutService;
import com.kawai.services.interfaces.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckoutServiceUC25Test {

    @Mock
    private FolioItemRepository folioItemRepository;

    @Mock
    private RoomBookingDetailRepository roomBookingDetailRepository;

    @Mock
    private EmailService emailService;

    // We assume the existence of CheckoutService. If not, it will fail to compile.
    @InjectMocks
    private com.kawai.services.impl.CheckoutServiceImpl checkoutService;

    private RoomBookingDetail mockRoomDetail;

    @BeforeEach
    void setUp() {
        mockRoomDetail = new RoomBookingDetail();
        mockRoomDetail.setId(100L);
        mockRoomDetail.setDetailStatus("Occupied");
    }

    // TC-M5-006 — Check-out khi Folio = 0 → thành công, phòng chuyển DIRTY
    @Test
    void testCheckoutWithZeroFolioBalance_Success() {
        // Arrange
        when(roomBookingDetailRepository.findById(100L)).thenReturn(Optional.of(mockRoomDetail));
        when(folioItemRepository.findByRoomBookingDetailId(100L)).thenReturn(Collections.emptyList());

        // Act
        checkoutService.processCheckout(100L);

        // Assert
        assertEquals("DIRTY", mockRoomDetail.getDetailStatus(), "Room status should be changed to DIRTY");
        verify(roomBookingDetailRepository, times(1)).save(mockRoomDetail);
    }

    // TC-M5-007 — Check-out khi Folio > 0 → chặn, trả FOLIO-001 (BR-FIN-01)
    @Test
    void testCheckoutWithUnpaidFolio_ThrowsException() {
        // Arrange
        FolioItem unpaidItem = new FolioItem();
        unpaidItem.setAmount(new BigDecimal("500000"));

        when(roomBookingDetailRepository.findById(100L)).thenReturn(Optional.of(mockRoomDetail));
        when(folioItemRepository.findByRoomBookingDetailId(100L)).thenReturn(Arrays.asList(unpaidItem));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkoutService.processCheckout(100L);
        });
        assertTrue(exception.getMessage().contains("FOLIO-001"), "Should throw FOLIO-001 error due to unpaid folio");
        assertEquals("Occupied", mockRoomDetail.getDetailStatus(), "Room status should NOT change");
    }

    // TC-M5-008 — Thanh toán tất toán Folio (Tiền mặt/Thẻ) → Folio = SETTLED
    @Test
    void testSettleFolio_Success() {
        // Arrange
        FolioItem unpaidItem = new FolioItem();
        unpaidItem.setAmount(new BigDecimal("500000"));
        unpaidItem.setIsSettledSeparately(false);

        when(folioItemRepository.findByRoomBookingDetailId(100L)).thenReturn(Arrays.asList(unpaidItem));

        // Act
        checkoutService.settleFolio(100L, new BigDecimal("500000"), "CREDIT_CARD");

        // Assert
        assertTrue(unpaidItem.getIsSettledSeparately(), "Folio should be marked as settled");
        verify(folioItemRepository, times(1)).save(unpaidItem);
    }

    // TC-M5-009 — Sau tất toán → tự động gửi e-Invoice qua email (SendGrid)
    @Test
    void testSendInvoiceEmailAfterSettlement() {
        // Arrange
        FolioItem unpaidItem = new FolioItem();
        unpaidItem.setAmount(new BigDecimal("500000"));
        unpaidItem.setIsSettledSeparately(false);

        when(folioItemRepository.findByRoomBookingDetailId(100L)).thenReturn(Arrays.asList(unpaidItem));

        // Act
        checkoutService.settleFolio(100L, new BigDecimal("500000"), "CASH");

        // Assert
        verify(emailService, times(1)).sendInvoiceEmail(anyLong(), any());
    }
}
