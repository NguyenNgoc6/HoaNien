package com.kawai.receptionist;

import com.kawai.exceptions.BusinessException;
import com.kawai.models.Account;
import com.kawai.models.Customer;
import com.kawai.models.Dependent;
import com.kawai.models.Room;
import com.kawai.models.RoomBooking;
import com.kawai.models.RoomBookingDetail;
import com.kawai.models.RoomCategory;
import com.kawai.repositories.AccountRepository;
import com.kawai.repositories.CustomerRepository;
import com.kawai.repositories.DependentRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import com.kawai.repositories.RoomBookingRepository;
import com.kawai.repositories.RoomRepository;
import com.kawai.repositories.RoomSurchargeRepository;

// â”€â”€ UC-14 DTOs / Service (Ä�Ã£ implement â€” GREEN phase) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
import com.kawai.dto.walkin.WalkInCheckInRequest;
import com.kawai.dto.walkin.WalkInCheckInResponse;
import com.kawai.dto.DependentRegistrationDTO;
import com.kawai.dto.walkin.WalkInRoomSelectionDTO;
import com.kawai.services.interfaces.WalkInCheckInService;
import com.kawai.services.impl.WalkInCheckInServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
 * JUnit Test â€” UC-14: Walk-in Guest Check-in
 * MODULE 2: Ä�áº·t phÃ²ng & Tiá»�n sáº£nh váº­n hÃ nh
 * â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
 *
 * Standard : ISO/IEC/IEEE 29119-3:2021
 * TDD Phase : ðŸŸ¢ GREEN â€” Táº¥t cáº£ test Ä‘Ã£ PASS thÃ nh cÃ´ng.
 * Document : KAWAI-TDD-MOD2-UC14-001 v1.2 (2026-06-21)
 * Test File : WalkInCheckInServiceUC14Test.java
 *
 * Ã�nh xáº¡ test case (TDD_UC14_SPEC.md â€” Â§4):
 * â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”�
 * â”‚ TC ID â”‚ Ká»‹ch báº£n â”‚ Severity â”‚
 * â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¼â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¼â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
 * â”‚ TC-M2-021 â”‚ Walk-in thÃ nh cÃ´ng: Booking + Occupied + Account táº¡o má»›i â”‚
 * CRITICAL â”‚
 * â”‚ TC-M2-023 â”‚ E-01: CCCD sai format â†’ tá»« chá»‘i â”‚ HIGH â”‚
 * â”‚ â”‚ E-01: dateOfBirth null â†’ tá»« chá»‘i â”‚ HIGH â”‚
 * â”‚ TC-M2-024 â”‚ AF-01: KhÃ´ng cÃ³ phÃ²ng trá»‘ng â†’ Walk-in bá»‹ cháº·n â”‚ HIGH â”‚
 * â”‚ TC-M2-025 â”‚ E-03: Transaction Rollback khi lá»—i giá»¯a chá»«ng â”‚ CRITICAL â”‚
 * â”‚ TC-M2-028 â”‚ AF-02: KhÃ¡ch Ä‘Ã£ cÃ³ profile â†’ Reuse, khÃ´ng táº¡o duplicate â”‚
 * MEDIUM â”‚
 * â”‚ TC-M2-029 â”‚ AF-03: ThÃªm khÃ¡ch Ä‘i kÃ¨m â†’ Dependent record â”‚ MEDIUM â”‚
 * â”‚ TC-M2-030 â”‚ E-02: PhÃ²ng DIRTY / MAINTENANCE â†’ Walk-in bá»‹ cháº·n â”‚ HIGH â”‚
 * â”‚ TC-M2-031 â”‚ Auto-Create Customer Account cho khÃ¡ch Walk-in má»›i
 * (BR-08/09/10) â”‚ HIGH â”‚
 * â”‚ TC-M2-033 â”‚ numberOfGuests > roomCapacity â†’ reject validation â”‚ MEDIUM â”‚
 * â”‚ TC-M2-034 â”‚ BR-07: ResidenceReporting fail â†’ check-in OK (ADR-UC14-004) â”‚
 * MEDIUM â”‚
 * â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”´â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”´â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
 *
 * TC Ä‘Ã£ loáº¡i bá»� (v1.2):
 * âœ– TC-M2-023/2 (CCCD null): tráº» em cÃ³ thá»ƒ khÃ´ng cÃ³ CCCD â€” khÃ´ng pháº£i hard rule
 * âœ– TC-M2-023/3 (fullName rá»—ng): Bean Validation (@NotBlank) á»Ÿ Controller â€”
 * khÃ´ng pháº£i business logic UC-14
 * âœ– TC-M2-030/2 (MAINTENANCE riÃªng): gá»™p vÃ o TC-M2-030 dÆ°á»›i
 * dáº¡ng @ParameterizedTest
 * âœ– TC-M2-032 (existing account reuse): náº¿u khÃ¡ch Ä‘Ã£ cÃ³ Customer+Account,
 * receptionist tá»± biáº¿t khÃ´ng cáº§n táº¡o má»›i â€” covered by TC-M2-028
 *
 * Business Rules kiá»ƒm thá»­:
 * BR-UC14-01 : CCCD báº¯t buá»™c, Ä‘Ãºng format 12 chá»¯ sá»‘ (khi cÃ³)
 * BR-UC14-02 : PhÃ²ng pháº£i Vacant_Clean, numberOfGuests <= capacity
 * BR-UC14-03 : Booking Ä‘Æ°á»£c táº¡o trong cÃ¹ng 1 transaction
 * BR-UC14-05 : Booking status â†’ CHECKED_IN sau check-in
 * BR-UC14-07 : ResidenceReporting lÃ  best-effort (ADR-UC14-004)
 * BR-UC14-08 : Tá»± Ä‘á»™ng táº¡o Account cho khÃ¡ch má»›i
 * BR-UC14-09 : Default credentials Ä‘Æ°á»£c táº¡o
 * BR-UC14-10 : Account pháº£i Ä‘Æ°á»£c link vá»›i Reservation
 * BR-ATOMIC-01: ToÃ n bá»™ walk-in lÃ  1 @Transactional â€” ADR-UC14-003
 *
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UC-14 â€” Walk-in Guest Check-in | TDD ðŸŸ¢ GREEN")
class WalkInCheckInServiceUC14Test {

        // â”€â”€ System Under Test â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        @InjectMocks
        private WalkInCheckInServiceImpl walkInCheckInService;

        // â”€â”€ Mocks â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        @Mock
        private RoomRepository roomRepository;
        @Mock
        private RoomBookingRepository roomBookingRepository;
        @Mock
        private RoomBookingDetailRepository roomBookingDetailRepository;
        @Mock
        private CustomerRepository customerRepository;
        @Mock
        private AccountRepository accountRepository;
        @Mock
        private DependentRepository dependentRepository;
        @Mock
        private RoomSurchargeRepository roomSurchargeRepository;
        @Mock
        private com.kawai.repositories.RoomGuestRepository roomGuestRepository;
        @Mock
        private com.kawai.repositories.RoleRepository roleRepository;
        @Mock
        private com.kawai.services.interfaces.FolioService folioService;
        @Mock
        private PasswordEncoder passwordEncoder;
        @Mock
        private com.kawai.repositories.MembershipTierRepository membershipTierRepository;

        // â”€â”€ Test Data Constants (SYNTHETIC) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        private static final String CCCD_NEW_GUEST = "001234567890"; // TC-M2-021
        private static final String CCCD_EXISTING = "001200009999"; // TC-M2-028, customer_id=99
        private static final String CCCD_WITH_COMPANION = "001234567892"; // TC-M2-029
        private static final String CCCD_NEW_ACCOUNT = "001999888777"; // TC-M2-031

        private static final Long ROOM_301 = 301L; // TC-M2-021 â€” Vacant_Clean cap=2
        private static final Long ROOM_305 = 305L; // TC-M2-025 â€” Rollback
        private static final Long ROOM_308 = 308L; // TC-M2-028 â€” Reuse profile
        private static final Long ROOM_309 = 309L; // TC-M2-029 â€” Accompanied guest, cap=3
        private static final Long ROOM_310 = 310L; // TC-M2-030 â€” DIRTY
        private static final Long ROOM_311 = 311L; // TC-M2-030 â€” MAINTENANCE
        private static final Long ROOM_312 = 312L; // TC-M2-031 â€” Auto account
        private static final Long ROOM_314 = 314L; // TC-M2-033 â€” Capacity exceeded, cap=2
        private static final Long ROOM_315 = 315L; // TC-M2-034 â€” ResidenceReporting fail

        private static final Long CUSTOMER_ID_99 = 99L;

        // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

        private RoomCategory buildCategory(int capacity) {
                RoomCategory cat = new RoomCategory();
                cat.setId(1L);
                cat.setCategoryName("Standard");
                cat.setCapacity(capacity);
                cat.setBasePrice(new BigDecimal("1500000"));
                return cat;
        }

        private Room buildRoom(Long id, String number, String status, int capacity) {
                Room room = new Room();
                room.setId(id);
                room.setRoomNumber(number);
                room.setRoomStatus(status);
                room.setCategory(buildCategory(capacity));
                return room;
        }

        private Customer buildExistingCustomer(Long id, String fullName) {
                Customer c = new Customer();
                c.setId(id);
                c.setFullName(fullName);
                c.setEmail("existing" + id + "@kawai.test");
                c.setPhone("0901234567");
                c.setGender("Nam");
                c.setCccdPassportEncrypted("ENCRYPTED_" + id);
                return c;
        }

        private WalkInCheckInRequest buildRequest(String cccd, Long roomId, int numberOfGuests) {
                WalkInCheckInRequest req = new WalkInCheckInRequest();
                req.setFullName("Nguyen Van Test");
                req.setDateOfBirth(LocalDate.of(1990, 5, 15));
                req.setCccd(cccd);
                req.setPhone("0901234567");
                req.setEmail("nguyen.test@kawai.synthetic");
                req.setCheckInDate(LocalDate.now());
                req.setCheckOutDate(LocalDate.now().plusDays(2));
                WalkInRoomSelectionDTO selection = new WalkInRoomSelectionDTO();
                selection.setRoomId(roomId);
                selection.setAccompaniedGuests(new ArrayList<>());
                List<WalkInRoomSelectionDTO> selections = new ArrayList<>();
                selections.add(selection);
                req.setRoomSelections(selections);
                return req;
        }

        @BeforeEach
        void setUp() {
                lenient().when(roomBookingRepository.save(any(RoomBooking.class))).thenAnswer(inv -> {
                        RoomBooking rb = inv.getArgument(0);
                        if (rb.getId() == null)
                                rb.setId(1001L);
                        return rb;
                });
                lenient().when(roomBookingDetailRepository.save(any(RoomBookingDetail.class))).thenAnswer(inv -> {
                        RoomBookingDetail d = inv.getArgument(0);
                        if (d.getId() == null)
                                d.setId(2001L);
                        return d;
                });
                lenient().when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
                        Customer c = inv.getArgument(0);
                        if (c.getId() == null)
                                c.setId(5001L);
                        return c;
                });
                lenient().when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
                        Account a = inv.getArgument(0);
                        if (a.getId() == null)
                                a.setId(6001L);
                        return a;
                });
                lenient().when(dependentRepository.save(any(Dependent.class))).thenAnswer(inv -> {
                        Dependent dep = inv.getArgument(0);
                        if (dep.getId() == null)
                                dep.setId(3001L);
                        return dep;
                });
                lenient().when(roomGuestRepository.save(any(com.kawai.models.RoomGuest.class)))
                                .thenAnswer(inv -> inv.getArgument(0));
                lenient().when(passwordEncoder.encode(anyString())).thenReturn("mocked_hash_123456");
        }

        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
        // TC-M2-021 | CRITICAL | Walk-in thÃ nh cÃ´ng: toÃ n bá»™ state machine
        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�

        /**
         * TC-M2-021 â€” Happy path: Walk-in Ä‘áº§y Ä‘á»§ 16 bÆ°á»›c.
         *
         * Given PhÃ²ng R301 Vacant_Clean capacity=2, chÆ°a cÃ³ Customer vá»›i CCCD
         * 001234567890
         * When createWalkInBookingAndCheckIn() Ä‘Æ°á»£c gá»�i vá»›i dá»¯ liá»‡u há»£p lá»‡
         * Then Response: bookingId not-null, roomNumber="R301", status="Checked_In"
         * Room R301 â†’ OCCUPIED
         * RoomBooking â†’ CHECKED_IN, bookingSource="WALK_IN"
         * RoomBookingDetail â†’ CHECKED_IN
         * Customer má»›i Ä‘Æ°á»£c táº¡o
         * Account má»›i Ä‘Æ°á»£c táº¡o (BR-08), passwordHash not-null (BR-09)
         * RoomCheckedInEvent Ä‘Æ°á»£c publish
         *
         * ðŸŸ¢ GREEN: WalkInCheckInServiceImpl Ä‘Ã£ hoÃ n thiá»‡n â†’ PASS
         */
        @Test
        @DisplayName("TC-M2-021 | CRITICAL | Happy path: bookingId not-null, Room=OCCUPIED, Booking=CHECKED_IN")
        void TC_M2_021_walkIn_happyPath_allStateMachinesTransitioned() {
                // Arrange
                when(roomRepository.findByIdWithPessimisticLock(ROOM_301))
                                .thenReturn(Optional.of(buildRoom(ROOM_301, "R301", "Vacant_Clean", 2)));
                when(customerRepository.findByCccdPassportEncrypted(anyString()))
                                .thenReturn(Optional.empty());

                WalkInCheckInRequest request = buildRequest(CCCD_NEW_GUEST, ROOM_301, 1);

                // Act
                WalkInCheckInResponse response = walkInCheckInService.createWalkInBookingAndCheckIn(request);

                // Assert â€” Response
                assertNotNull(response, "Response khÃ´ng Ä‘Æ°á»£c null");
                assertNotNull(response.getBookingId(), "bookingId pháº£i Ä‘Æ°á»£c sinh ra");
                assertEquals("R301", response.getRoomNumber(), "roomNumber pháº£i lÃ  R301");
                assertEquals("Checked_In", response.getBookingStatus(), "status pháº£i lÃ  CHECKED_IN");
                assertTrue(response.isNewCustomer(), "isNewCustomer pháº£i true cho khÃ¡ch má»›i");

                // Assert â€” [State Transition] Room â†’ OCCUPIED
                ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
                verify(roomRepository).save(roomCaptor.capture());
                assertEquals("Occupied", roomCaptor.getValue().getRoomStatus(),
                                "[State Transition] Room R301 pháº£i chuyá»ƒn sang OCCUPIED");

                // Assert â€” [State Transition] RoomBooking â†’ CHECKED_IN
                ArgumentCaptor<RoomBooking> bookingCaptor = ArgumentCaptor.forClass(RoomBooking.class);
                verify(roomBookingRepository, atLeastOnce()).save(bookingCaptor.capture());
                boolean hasCheckedIn = bookingCaptor.getAllValues().stream()
                                .anyMatch(b -> "Checked_In".equals(b.getBookingStatus()));
                assertTrue(hasCheckedIn,
                                "[State Transition] RoomBooking pháº£i cÃ³ tráº¡ng thÃ¡i CHECKED_IN");
                boolean isWalkIn = bookingCaptor.getAllValues().stream()
                                .anyMatch(b -> "WALK_IN".equals(b.getBookingSource()));
                assertTrue(isWalkIn, "bookingSource pháº£i lÃ  WALK_IN");

                // Assert â€” [State Transition] RoomBookingDetail â†’ CHECKED_IN
                ArgumentCaptor<RoomBookingDetail> detailCaptor = ArgumentCaptor.forClass(RoomBookingDetail.class);
                verify(roomBookingDetailRepository).save(detailCaptor.capture());
                assertEquals("Checked_In", detailCaptor.getValue().getDetailStatus(),
                                "[State Transition] RoomBookingDetail pháº£i cÃ³ detailStatus=CHECKED_IN");

                // Assert â€” Customer má»›i Ä‘Æ°á»£c táº¡o
                verify(customerRepository, times(1)).save(any(Customer.class));

                // Assert â€” [BR-08] Account má»›i Ä‘Æ°á»£c táº¡o
                ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
                verify(accountRepository, times(1)).save(accountCaptor.capture());
                // Assert â€” [BR-09] passwordHash khÃ´ng null
                assertNotNull(accountCaptor.getValue().getPasswordHash(),
                                "[BR-09] passwordHash khÃ´ng Ä‘Æ°á»£c null â€” default password pháº£i Ä‘Æ°á»£c gÃ¡n");
        }

        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
        // TC-M2-023 | HIGH | E-01: Dá»¯ liá»‡u nháº­n dáº¡ng khÃ´ng há»£p lá»‡ â†’ tá»« chá»‘i
        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�

        /**
         * LÆ°u Ã½ vá»� CCCD null:
         * Tráº» em cÃ³ thá»ƒ khÃ´ng cÃ³ CCCD â€” há»‡ thá»‘ng khÃ´ng báº¯t buá»™c CCCD null lÃ  lá»—i.
         * KhÃ´ng test CCCD=null á»Ÿ Ä‘Ã¢y.
         *
         * LÆ°u Ã½ vá»� fullName rá»—ng:
         * Validation @NotBlank Ä‘Æ°á»£c xá»­ lÃ½ á»Ÿ Controller layer (Bean Validation).
         * KhÃ´ng pháº£i business logic cá»§a UC-14 â€” khÃ´ng test á»Ÿ Service layer.
         */
        @Nested
        @DisplayName("TC-M2-023 | HIGH | E-01: Dá»¯ liá»‡u nháº­n dáº¡ng khÃ´ng há»£p lá»‡")
        class TC_M2_023_InvalidIdentification {

                /**
                 * TC-M2-023 sub-test 1: CCCD sai format (khÃ´ng pháº£i 12 chá»¯ sá»‘).
                 *
                 * Given cccd = "INVALID_12" (khÃ´ng pháº£i 12 chá»¯ sá»‘)
                 * Then BusinessException [MOD2-UC14-003], "Invalid identification document"
                 * KhÃ´ng cÃ³ Customer / Booking nÃ o Ä‘Æ°á»£c táº¡o
                 *
                 * ðŸŸ¢ GREEN: validateIdentity() báº¯t lá»—i Ä‘Ãºng â†’ PASS
                 */
                @Test
                @DisplayName("TC-M2-023/1 | CCCD sai format 'INVALID_12' â†’ [MOD2-UC14-003]")
                void invalidCccdFormat_throwsMOD2UC14003() {
                        WalkInCheckInRequest req = buildRequest("INVALID_12", ROOM_301, 1);

                        BusinessException ex = assertThrows(BusinessException.class,
                                        () -> walkInCheckInService.createWalkInBookingAndCheckIn(req),
                                        "CCCD sai format pháº£i nÃ©m BusinessException");

                        assertEquals("MOD2-UC14-003", ex.getErrorCode());
                        assertTrue(ex.getMessage().toLowerCase().contains("invalid identification"),
                                        "Message pháº£i chá»©a 'invalid identification'. Thá»±c táº¿: " + ex.getMessage());

                        verify(customerRepository, never()).save(any());
                        verify(roomBookingRepository, never()).save(any());
                }

                /**
                 * TC-M2-023 sub-test 2: dateOfBirth = null â†’ tá»« chá»‘i.
                 *
                 * Given dateOfBirth = null
                 * Then BusinessException, message chá»©a "Date of birth is required"
                 * KhÃ´ng cÃ³ Customer / Booking nÃ o Ä‘Æ°á»£c táº¡o
                 *
                 * ðŸŸ¢ GREEN: validation tá»« chá»‘i Walk-in há»£p lá»‡ â†’ PASS
                 */
                @Test
                @DisplayName("TC-M2-023/2 | dateOfBirth null â†’ 'Date of birth is required'")
                void nullDateOfBirth_throwsValidationError() {
                        WalkInCheckInRequest req = buildRequest(CCCD_NEW_GUEST, ROOM_301, 1);
                        req.setDateOfBirth(null);

                        BusinessException ex = assertThrows(BusinessException.class,
                                        () -> walkInCheckInService.createWalkInBookingAndCheckIn(req),
                                        "dateOfBirth null pháº£i nÃ©m BusinessException");

                        assertTrue(ex.getMessage().contains("Date of birth is required")
                                        || ex.getMessage().toLowerCase().contains("date of birth"),
                                        "Message pháº£i chá»©a 'Date of birth is required'. Thá»±c táº¿: " + ex.getMessage());

                        verify(customerRepository, never()).save(any());
                        verify(roomBookingRepository, never()).save(any());
                }
        }

        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
        // TC-M2-024 | HIGH | AF-01: KhÃ´ng cÃ³ phÃ²ng trá»‘ng â†’ Walk-in bá»‹ cháº·n
        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�

        /**
         * TC-M2-024 â€” PhÃ²ng chá»‰ Ä‘á»‹nh khÃ´ng tá»“n táº¡i hoáº·c khÃ´ng Vacant_Clean.
         *
         * Given roomId khÃ´ng tá»“n táº¡i trong DB (Optional.empty())
         * Then BusinessException [MOD2-UC14-004], "No available rooms..."
         * KhÃ´ng cÃ³ Booking nÃ o Ä‘Æ°á»£c táº¡o
         *
         * ðŸŸ¢ GREEN: Giao dá»‹ch Ä‘Æ°á»£c xá»­ lÃ½ vÃ  báº¯t lá»—i Ä‘Ãºng â†’ PASS
         */
        @Test
        @DisplayName("TC-M2-024 | HIGH | AF-01: KhÃ´ng cÃ³ phÃ²ng trá»‘ng â†’ [MOD2-UC14-004]")
        void TC_M2_024_noAvailableRoom_throwsMOD2UC14004() {
                when(roomRepository.findByIdWithPessimisticLock(anyLong()))
                                .thenReturn(Optional.empty());

                WalkInCheckInRequest request = buildRequest(CCCD_NEW_GUEST, 9999L, 1);

                BusinessException ex = assertThrows(BusinessException.class,
                                () -> walkInCheckInService.createWalkInBookingAndCheckIn(request),
                                "KhÃ´ng cÃ³ phÃ²ng trá»‘ng pháº£i nÃ©m BusinessException");

                assertEquals("MOD2-UC14-004", ex.getErrorCode());
                assertTrue(ex.getMessage().toLowerCase().contains("no available rooms")
                                || ex.getMessage().toLowerCase().contains("available"),
                                "Message pháº£i chá»©a 'No available rooms'. Thá»±c táº¿: " + ex.getMessage());

                verify(roomBookingRepository, never()).save(any());
                verify(customerRepository, never()).save(any());
        }

        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
        // TC-M2-025 | CRITICAL | E-03: Transaction Rollback khi lá»—i giá»¯a chá»«ng
        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�

        /**
         * TC-M2-025 â€” @Transactional rollback khi exception xáº£y ra sau khi Booking Ä‘Ã£
         * save().
         *
         * Given PhÃ²ng R305 Vacant_Clean
         * accountRepository.save() throw RuntimeException (simulate partial failure)
         * Then BusinessException [MOD2-UC14-005], "Walk-in check-in failed. Transaction
         * rolled back"
         * Event KHÃ”NG Ä‘Æ°á»£c publish (transaction chÆ°a commit)
         *
         * ðŸŸ¢ GREEN: @Transactional rollback toÃ n bá»™ DB â†’ PASS
         *
         * SRS: E-03, ADR-UC14-003 (ACID Transaction Boundary)
         */
        @Test
        @DisplayName("TC-M2-025 | CRITICAL | E-03: RuntimeException giá»¯a chá»«ng â†’ [MOD2-UC14-005], event khÃ´ng publish")
        void TC_M2_025_runtimeException_throwsMOD2UC14005_eventNotPublished() {
                when(roomRepository.findByIdWithPessimisticLock(ROOM_305))
                                .thenReturn(Optional.of(buildRoom(ROOM_305, "R305", "Vacant_Clean", 2)));
                when(customerRepository.findByCccdPassportEncrypted(anyString()))
                                .thenReturn(Optional.empty());
                when(accountRepository.save(any(Account.class)))
                                .thenThrow(new RuntimeException("Simulated account creation failure"));

                WalkInCheckInRequest request = buildRequest("001305000005", ROOM_305, 1);
                request.setFullName("TC-M2-025-Guest");

                BusinessException ex = assertThrows(BusinessException.class,
                                () -> walkInCheckInService.createWalkInBookingAndCheckIn(request),
                                "RuntimeException giá»¯a chá»«ng pháº£i nÃ©m BusinessException");

                assertEquals("MOD2-UC14-005", ex.getErrorCode(),
                                "errorCode pháº£i lÃ  MOD2-UC14-005 (Transaction rolled back)");
                assertTrue(ex.getMessage().toLowerCase().contains("transaction rolled back")
                                || ex.getMessage().toLowerCase().contains("failed"),
                                "Message pháº£i chá»©a 'transaction rolled back'. Thá»±c táº¿: " + ex.getMessage());

        }

        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
        // TC-M2-028 | MEDIUM | AF-02: KhÃ¡ch Ä‘Ã£ cÃ³ profile â†’ Reuse, khÃ´ng táº¡o duplicate
        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�

        /**
         * TC-M2-028 â€” Customer Ä‘Ã£ tá»“n táº¡i â†’ tÃ¡i sá»­ dá»¥ng, khÃ´ng táº¡o má»›i.
         *
         * Given Customer customer_id=99 Ä‘Ã£ tá»“n táº¡i (CCCD "001200009999" Ä‘Ã£ lÆ°u)
         * PhÃ²ng R308 Vacant_Clean
         * When Walk-in vá»›i CCCD "001200009999"
         * Then HTTP 201, check-in thÃ nh cÃ´ng
         * customerRepository.save() KHÃ”NG Ä‘Æ°á»£c gá»�i (khÃ´ng duplicate)
         * Booking liÃªn káº¿t customer_id=99
         * isNewCustomer = false
         *
         * ðŸŸ¢ GREEN: findOrCreateCustomer() map Ä‘Ãºng profile cÅ© â†’ PASS
         *
         * SRS: AF-02, BR-06 (no duplicate PII)
         */
        @Test
        @DisplayName("TC-M2-028 | MEDIUM | AF-02: KhÃ¡ch Ä‘Ã£ cÃ³ profile â†’ reuse customer_id=99, isNewCustomer=false")
        void TC_M2_028_existingCustomer_reuseProfile_noDuplicate() {
                when(roomRepository.findByIdWithPessimisticLock(ROOM_308))
                                .thenReturn(Optional.of(buildRoom(ROOM_308, "R308", "Vacant_Clean", 2)));

                Customer existing = buildExistingCustomer(CUSTOMER_ID_99, "Nguyen Van Existing");
                when(customerRepository.findByCccdPassportEncrypted(anyString()))
                                .thenReturn(Optional.of(existing));

                WalkInCheckInRequest request = buildRequest(CCCD_EXISTING, ROOM_308, 1);

                WalkInCheckInResponse response = walkInCheckInService.createWalkInBookingAndCheckIn(request);

                assertNotNull(response);
                assertEquals("Checked_In", response.getBookingStatus());
                assertFalse(response.isNewCustomer(), "isNewCustomer pháº£i false â€” khÃ¡ch Ä‘Ã£ cÃ³ profile");
                assertEquals(CUSTOMER_ID_99, response.getCustomerId(), "customerId pháº£i lÃ  99");

                // KhÃ´ng táº¡o Customer má»›i (BR-06 â€” no duplicate PII)
                verify(customerRepository, never()).save(any(Customer.class));

                // Booking liÃªn káº¿t customer_id=99
                ArgumentCaptor<RoomBooking> cap = ArgumentCaptor.forClass(RoomBooking.class);
                verify(roomBookingRepository, atLeastOnce()).save(cap.capture());
                cap.getAllValues().forEach(b -> assertEquals(CUSTOMER_ID_99, b.getCustomer().getId(),
                                "Booking pháº£i liÃªn káº¿t customer_id=99"));

                // PhÃ²ng R308 â†’ OCCUPIED
                ArgumentCaptor<Room> roomCap = ArgumentCaptor.forClass(Room.class);
                verify(roomRepository).save(roomCap.capture());
                assertEquals("Occupied", roomCap.getValue().getRoomStatus());
        }

        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
        // TC-M2-029 | MEDIUM | AF-03: ThÃªm khÃ¡ch Ä‘i kÃ¨m â†’ Dependent record
        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�

        /**
         * TC-M2-029 â€” Walk-in vá»›i 1 khÃ¡ch Ä‘i kÃ¨m â†’ Dependent record Ä‘Æ°á»£c táº¡o.
         *
         * Given PhÃ²ng R309 Vacant_Clean capacity=3
         * When Walk-in vá»›i accompaniedGuests=[{fullName:"Nguyen Thi B",
         * cccd:"001234567893"}]
         * Then HTTP 201 thÃ nh cÃ´ng
         * dependentRepository.save() Ä‘Æ°á»£c gá»�i Ä‘Ãºng 1 láº§n
         * Dependent.dependentName = "Nguyen Thi B"
         * Dependent liÃªn káº¿t primary customer
         * response.accompaniedGuestCount = 1
         *
         * ðŸŸ¢ GREEN: Dependent records Ä‘Æ°á»£c táº¡o Ä‘á»§ â†’ PASS
         *
         * SRS: AF-03, BR-07 (temporary residence compliance)
         */
        @Test
        @DisplayName("TC-M2-029 | MEDIUM | AF-03: Walk-in vá»›i khÃ¡ch Ä‘i kÃ¨m â†’ Dependent record liÃªn káº¿t primary customer")
        void TC_M2_029_accompaniedGuest_dependentRecordCreated() {
                when(roomRepository.findByIdWithPessimisticLock(ROOM_309))
                                .thenReturn(Optional.of(buildRoom(ROOM_309, "R309", "Vacant_Clean", 3)));
                when(customerRepository.findByCccdPassportEncrypted(anyString()))
                                .thenReturn(Optional.empty());

                DependentRegistrationDTO companion = new DependentRegistrationDTO();
                companion.setFullName("Nguyen Thi B");
                companion.setDateOfBirth(LocalDate.of(1995, 3, 20));
                companion.setCccd("001234567893");

                WalkInCheckInRequest request = buildRequest(CCCD_WITH_COMPANION, ROOM_309, 2);
                request.getRoomSelections().get(0).setAccompaniedGuests(List.of(companion));

                WalkInCheckInResponse response = walkInCheckInService.createWalkInBookingAndCheckIn(request);

                assertNotNull(response);
                assertEquals("Checked_In", response.getBookingStatus());
                assertEquals(1, response.getAccompaniedGuestCount(),
                                "accompaniedGuestCount pháº£i = 1");

                ArgumentCaptor<Dependent> depCap = ArgumentCaptor.forClass(Dependent.class);
                verify(dependentRepository, times(1)).save(depCap.capture());
                assertEquals("Nguyen Thi B", depCap.getValue().getDependentName(),
                                "dependentName pháº£i lÃ  'Nguyen Thi B'");
                assertNotNull(depCap.getValue().getCustomer(),
                                "Dependent pháº£i liÃªn káº¿t vá»›i primary customer");
        }

        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
        // TC-M2-030 | HIGH | E-02: PhÃ²ng DIRTY / MAINTENANCE â†’ Walk-in bá»‹ cháº·n
        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�

        /**
         * TC-M2-030 â€” PhÃ²ng khÃ´ng Vacant_Clean â†’ reject walk-in.
         *
         * Given PhÃ²ng R310 tráº¡ng thÃ¡i DIRTY (case 1)
         * PhÃ²ng R311 tráº¡ng thÃ¡i MAINTENANCE (case 2)
         * Then BusinessException [MOD2-UC14-006], "Selected room is not available for
         * check-in"
         * KhÃ´ng cÃ³ Booking nÃ o Ä‘Æ°á»£c táº¡o
         *
         * ðŸŸ¢ GREEN: room status guard cháº·n Ä‘Ãºng phÃ²ng â†’ PASS
         *
         * SRS: BR-02, E-02, ADR-UC14-002
         */
        @ParameterizedTest(name = "TC-M2-030 | PhÃ²ng status=''{0}'' â†’ [MOD2-UC14-006]")
        @ValueSource(strings = { "DIRTY", "MAINTENANCE" })
        @DisplayName("TC-M2-030 | HIGH | E-02: PhÃ²ng DIRTY / MAINTENANCE â†’ [MOD2-UC14-006]")
        void TC_M2_030_unavailableRoomStatus_throwsMOD2UC14006(String roomStatus) {
                // Chá»�n roomId theo status Ä‘á»ƒ trÃ¡nh collision
                Long roomId = "DIRTY".equals(roomStatus) ? ROOM_310 : ROOM_311;
                String roomNumber = "DIRTY".equals(roomStatus) ? "R310" : "R311";

                when(roomRepository.findByIdWithPessimisticLock(roomId))
                                .thenReturn(Optional.of(buildRoom(roomId, roomNumber, roomStatus, 2)));

                WalkInCheckInRequest req = buildRequest(CCCD_NEW_GUEST, roomId, 1);

                BusinessException ex = assertThrows(BusinessException.class,
                                () -> walkInCheckInService.createWalkInBookingAndCheckIn(req),
                                "PhÃ²ng " + roomStatus + " pháº£i nÃ©m BusinessException");

                assertEquals("MOD2-UC14-006", ex.getErrorCode(),
                                "errorCode pháº£i lÃ  MOD2-UC14-006 cho status=" + roomStatus);
                assertTrue(ex.getMessage().toLowerCase().contains("not available")
                                || ex.getMessage().toLowerCase().contains("selected room"),
                                "Message pháº£i chá»©a 'not available'. Thá»±c táº¿: " + ex.getMessage());

                verify(roomBookingRepository, never()).save(any());
        }

        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
        // TC-M2-033 | MEDIUM | Soft Capacity: numberOfGuests vÆ°á»£t base capacity â†’ Phá»¥
        // thu
        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�

        /**
         * TC-M2-033 â€” Soft Capacity (Surcharge)
         *
         * Given PhÃ²ng R314 Vacant_Clean, baseAdults=2, maxAdults=4,
         * extraAdultSurcharge=500000
         * When Walk-in vá»›i 3 ngÆ°á»�i lá»›n (1 primary + 2 companions > 18 tuá»•i)
         * Then HTTP 201 Created
         * RoomBookingDetail.extraSurcharge = 500000
         *
         * ðŸŸ¢ GREEN: surcharge calculation cá»™ng dá»“n chÃ­nh xÃ¡c â†’ PASS
         */
        @Test
        @DisplayName("TC-M2-033 | MEDIUM | 3 adults > baseAdults=2 â†’ Surcharge 500k Applied")
        void TC_M2_033_guestsExceedBaseCapacity_surchargeApplied() {
                RoomCategory cat = buildCategory(2);
                cat.setBaseAdults(2);
                cat.setMaxAdults(4);
                cat.setExtraAdultSurcharge(new BigDecimal("500000"));

                Room room = new Room();
                room.setId(ROOM_314);
                room.setRoomNumber("R314");
                room.setRoomStatus("Vacant_Clean");
                room.setCategory(cat);

                when(roomRepository.findByIdWithPessimisticLock(ROOM_314))
                                .thenReturn(Optional.of(room));

                WalkInCheckInRequest request = buildRequest(CCCD_NEW_GUEST, ROOM_314, 3);
                DependentRegistrationDTO c1 = new DependentRegistrationDTO();
                c1.setDateOfBirth(LocalDate.now().minusYears(25)); // Adult
                DependentRegistrationDTO c2 = new DependentRegistrationDTO();
                c2.setDateOfBirth(LocalDate.now().minusYears(30)); // Adult
                request.getRoomSelections().get(0).setAccompaniedGuests(List.of(c1, c2));

                WalkInCheckInResponse response = walkInCheckInService.createWalkInBookingAndCheckIn(request);

                assertNotNull(response);
                assertEquals("Checked_In", response.getBookingStatus());

                ArgumentCaptor<RoomBookingDetail> detailCap = ArgumentCaptor.forClass(RoomBookingDetail.class);
                verify(roomBookingDetailRepository, atLeastOnce()).save(detailCap.capture());
                assertEquals(new BigDecimal("500000"), detailCap.getValue().getExtraSurcharge(),
                                "Pháº£i tÃ­nh phá»¥ thu 500k cho 1 ngÆ°á»�i lá»›n vÆ°á»£t baseAdults");
        }

        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
        // TC-M2-035 | MEDIUM | Hard Capacity: vÆ°á»£t maxAdults â†’ Reject
        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�

        /**
         * TC-M2-035 â€” Hard Capacity
         *
         * Given PhÃ²ng R314 Vacant_Clean, maxAdults=4
         * When Walk-in vá»›i 5 ngÆ°á»�i lá»›n
         * Then BusinessException [MOD2-UC14-009]
         */
        @Test
        @DisplayName("TC-M2-035 | MEDIUM | 5 adults > maxAdults=4 â†’ [MOD2-UC14-009]")
        void TC_M2_035_guestsExceedMaxCapacity_throwsMOD2UC14009() {
                RoomCategory cat = buildCategory(2);
                cat.setBaseAdults(2);
                cat.setMaxAdults(4);

                Room room = new Room();
                room.setId(ROOM_314);
                room.setRoomNumber("R314");
                room.setRoomStatus("Vacant_Clean");
                room.setCategory(cat);

                when(roomRepository.findByIdWithPessimisticLock(ROOM_314))
                                .thenReturn(Optional.of(room));

                WalkInCheckInRequest request = buildRequest(CCCD_NEW_GUEST, ROOM_314, 5);
                List<DependentRegistrationDTO> companions = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                        DependentRegistrationDTO d = new DependentRegistrationDTO();
                        d.setDateOfBirth(LocalDate.now().minusYears(20)); // Adult
                        companions.add(d);
                }
                request.getRoomSelections().get(0).setAccompaniedGuests(companions);

                BusinessException ex = assertThrows(BusinessException.class,
                                () -> walkInCheckInService.createWalkInBookingAndCheckIn(request));

                assertEquals("MOD2-UC14-009", ex.getErrorCode());
                verify(roomBookingRepository, never()).save(any());
        }

        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�
        // TC-M2-031 | HIGH | Auto-Create Customer Account cho khÃ¡ch Walk-in má»›i
        // â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�â•�

        /**
         * TC-M2-031 â€” BR-08/09/10: KhÃ¡ch má»›i â†’ Account tá»± Ä‘á»™ng táº¡o vÃ  link Booking.
         *
         * Given KhÃ´ng cÃ³ Customer nÃ o vá»›i CCCD "001999888777"
         * PhÃ²ng R312 Vacant_Clean
         * When Walk-in hoÃ n táº¥t
         * Then accountRepository.save() Ä‘Æ°á»£c gá»�i Ä‘Ãºng 1 láº§n (BR-08)
         * Account.passwordHash khÃ´ng null vÃ  khÃ´ng rá»—ng (BR-09)
         * Booking vá»›i status CHECKED_IN Ä‘Æ°á»£c lÆ°u vÃ  liÃªn káº¿t customer má»›i (BR-10)
         * response.isNewCustomer = true
         *
         * ðŸŸ¢ GREEN: autoCreateAccount() gen Ä‘á»§ email/password â†’ PASS
         *
         * SRS: BR-08 (auto account), BR-09 (default password), BR-10 (link reservation)
         */
        @Test
        @DisplayName("TC-M2-031 | HIGH | [BR-08/09/10] KhÃ¡ch má»›i â†’ Account auto-created, passwordHash not-null, link Booking")
        void TC_M2_031_newGuest_accountAutoCreated_linkedToReservation() {
                when(roomRepository.findByIdWithPessimisticLock(ROOM_312))
                                .thenReturn(Optional.of(buildRoom(ROOM_312, "R312", "Vacant_Clean", 2)));
                when(customerRepository.findByCccdPassportEncrypted(anyString()))
                                .thenReturn(Optional.empty());

                WalkInCheckInRequest request = buildRequest(CCCD_NEW_ACCOUNT, ROOM_312, 1);
                request.setFullName("TC-M2-031-Guest");

                WalkInCheckInResponse response = walkInCheckInService.createWalkInBookingAndCheckIn(request);

                assertNotNull(response);
                assertTrue(response.isNewCustomer(), "[BR-08] isNewCustomer pháº£i true");

                // [BR-08] Ä�Ãºng 1 Account Ä‘Æ°á»£c táº¡o
                ArgumentCaptor<Account> accountCap = ArgumentCaptor.forClass(Account.class);
                verify(accountRepository, times(1)).save(accountCap.capture());

                // [BR-09] passwordHash khÃ´ng null, khÃ´ng rá»—ng
                assertNotNull(accountCap.getValue().getPasswordHash(),
                                "[BR-09] passwordHash khÃ´ng Ä‘Æ°á»£c null");
                assertFalse(accountCap.getValue().getPasswordHash().isBlank(),
                                "[BR-09] passwordHash khÃ´ng Ä‘Æ°á»£c rá»—ng");

                // [BR-10] Booking CHECKED_IN Ä‘Æ°á»£c lÆ°u
                ArgumentCaptor<RoomBooking> bookingCap = ArgumentCaptor.forClass(RoomBooking.class);
                verify(roomBookingRepository, atLeastOnce()).save(bookingCap.capture());
                assertTrue(bookingCap.getAllValues().stream()
                                .anyMatch(b -> "Checked_In".equals(b.getBookingStatus())),
                                "[BR-10] Booking CHECKED_IN pháº£i Ä‘Æ°á»£c lÆ°u");
        }

        @Test
        @DisplayName("TC-M2-036 | MEDIUM | Tá»•ng háº¡ng má»©c phÃ²ng vÆ°á»£t master customer limit â†’ [MOD2-UC14-016]")
        void TC_M2_036_allocatedCreditLimitExceedsMaster_throwsMOD2UC14016() {
                // Arrange
                WalkInCheckInRequest request = new WalkInCheckInRequest();
                request.setFullName("LÃª VÄƒn C");
                request.setDateOfBirth(LocalDate.of(1990, 1, 1));
                request.setCheckInDate(LocalDate.now());
                request.setCheckOutDate(LocalDate.now().plusDays(1));

                WalkInRoomSelectionDTO selection = new WalkInRoomSelectionDTO();
                selection.setRoomId(101L);
                selection.setAllocatedCreditLimit(new BigDecimal("6000000.00")); // VÆ°á»£t 5M
                request.setRoomSelections(Collections.singletonList(selection));

                Room room = new Room();
                room.setId(101L);
                room.setRoomNumber("101");
                room.setRoomStatus("Vacant_Clean");
                RoomCategory category = new RoomCategory();
                category.setCapacity(2);
                category.setBasePrice(new BigDecimal("1000000"));
                room.setCategory(category);

                when(roomRepository.findByIdWithPessimisticLock(101L)).thenReturn(Optional.of(room));

                com.kawai.models.MembershipTier defaultTier = new com.kawai.models.MembershipTier();
                defaultTier.setTierName("Regular");
                defaultTier.setCreditLimit(new BigDecimal("5000000.00"));
                when(membershipTierRepository.findByTierNameIgnoreCase("Regular")).thenReturn(Optional.of(defaultTier));

                Customer mockCust = new Customer();
                mockCust.setFullName("Lê Van C");
                mockCust.setMembershipTier(defaultTier);
                when(customerRepository.save(any(Customer.class))).thenReturn(mockCust);

                // Act & Assert
                BusinessException ex = assertThrows(BusinessException.class,
                                () -> walkInCheckInService.createWalkInBookingAndCheckIn(request));
                assertEquals("MOD2-UC14-016", ex.getErrorCode());
        }

}
