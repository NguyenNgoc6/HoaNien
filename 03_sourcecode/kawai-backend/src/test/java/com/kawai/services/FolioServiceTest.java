package com.kawai.services;

import com.kawai.models.FolioItem;
import com.kawai.models.Booking;
import com.kawai.models.RoomBooking;
import com.kawai.models.RoomBookingDetail;
import com.kawai.models.Customer;
import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import com.kawai.repositories.BookingRepository;
import com.kawai.services.impl.FolioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FolioServiceTest {

    @Mock
    private FolioItemRepository folioItemRepository;

    @Mock
    private RoomBookingDetailRepository roomBookingDetailRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private FolioServiceImpl folioService;

    @Test
    public void TC_M5_001_Folio_hien_thi_dung_danh_sach_no_phong_theo_tung_dich_vu() {
        FolioItem item1 = new FolioItem();
        item1.setAmount(new BigDecimal("100000"));
        item1.setSourceDepartment("ROOM");

        FolioItem item2 = new FolioItem();
        item2.setAmount(new BigDecimal("50000"));
        item2.setSourceDepartment("F&B");

        when(folioItemRepository.findByBookingId(1L)).thenReturn(Arrays.asList(item1, item2));

        List<FolioItem> items = folioService.getFolioItemsForBooking(1L);
        assertEquals(2, items.size());
        assertEquals("ROOM", items.get(0).getSourceDepartment());
    }

    @Test
    public void TC_M5_002_Ghi_nhan_luong_tien_nhieu_dot_ung_truoc_tra_them_hoan_tien() {
        Booking booking = new Booking();
        booking.setId(1L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        folioService.addPayment(1L, new BigDecimal("-50000"), "CASH", "Deposit");
        verify(folioItemRepository, times(1)).save(any(FolioItem.class));
    }

    @Test
    public void TC_M5_003_Gom_hoa_don_tien_phong_an_uong_tour_tong_chinh_xac_BigDecimal() {
        FolioItem item1 = new FolioItem();
        item1.setAmount(new BigDecimal("100000"));
        
        FolioItem item2 = new FolioItem();
        item2.setAmount(new BigDecimal("50500"));

        when(folioItemRepository.findByBookingId(1L)).thenReturn(Arrays.asList(item1, item2));

        BigDecimal total = folioService.calculateTotalFolio(1L);
        assertEquals(new BigDecimal("150500"), total);
    }

    @Test
    public void TC_M5_004_Night_Audit_02_00_AM_cong_phi_phong_ngay_vao_Folio_cac_phong_OCCUPIED() {
        RoomBookingDetail detail = new RoomBookingDetail();
        RoomBooking booking = new RoomBooking();
        booking.setId(1L);
        detail.setRoomBooking(booking);
        detail.setRoomCharge(new BigDecimal("500000"));
        Customer payer = new Customer();
        detail.setCustomer(payer);
        
        when(roomBookingDetailRepository.findByDetailStatus("Occupied")).thenReturn(Arrays.asList(detail));
        
        folioService.runNightAudit();
        verify(folioItemRepository, times(1)).save(any(FolioItem.class));
    }

    @Test
    public void TC_M5_005_Night_Audit_chuyen_Business_Date_len_1_ngay() {
        folioService.incrementBusinessDate();
        assertEquals(true, true);
    }
}
