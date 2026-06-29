# SYSTEM WORKFLOWS — KAWAI RETREAT RESORT & HUB

**Project:** SWP391 — Group 2 — SE2023-NET  
**Version:** 1.0 | **Date:** 2026-06-29  
**Based on:** `business_rule.md` · `UC_MASTER_TABLE.md` · `UC_DETAIL_SPEC.md` · `requirement_traceability_matrix.md`

---

## Mục lục

| WF ID | Tên Workflow | Actor chính |
|---|---|---|
| [WF-01](#wf-01) | Customer Registration & Login | Guest, Customer |
| [WF-02](#wf-02) | Room Search & Online Booking | Customer |
| [WF-03](#wf-03) | Check-In (Sảnh) | Receptionist |
| [WF-04](#wf-04) | Check-Out & Invoice Settlement | Receptionist |
| [WF-05](#wf-05) | Room Service (E-Menu / Post-to-Room) | Customer |
| [WF-06](#wf-06) | POS Dine-In & F&B Payment | F&B Staff |
| [WF-07](#wf-07) | KDS Kitchen Order Lifecycle | Kitchen Staff |
| [WF-08](#wf-08) | Table Reservation | Customer |
| [WF-09](#wf-09) | Tour Booking | Customer |
| [WF-10](#wf-10) | AI Face Scan Attendance | Tour Guide |
| [WF-11](#wf-11) | Room State Machine (HK & Maintenance) | System, HK, Maintenance |
| [WF-12](#wf-12) | Night Audit (02:00 AM Cron) | System |
| [WF-13](#wf-13) | Folio Aggregation & Settlement | Receptionist |
| [WF-14](#wf-14) | Walk-In Check-In | Receptionist |
| [WF-15](#wf-15) | Password Reset | Customer, Employee |
| [WF-16](#wf-16) | Booking Cancellation & Refund | Customer |
| [WF-17](#wf-17) | Admin Master Data Management | Admin |
| [WF-18](#wf-18) | Scheduled Jobs Automation | System |

---

<a id="wf-01"></a>
## WF-01 — Customer Registration & Login

**Actor:** Guest, Customer  
**Business Rules:** BR-SYS-01, BR-SYS-02, BR-SYS-07  
**Use Cases:** UC01.1, UC01.3, UC01.5  
**Status:** ✅ Implemented

### Registration Flow

```
Guest
  │
  ├─ Opens /booking or landing page
  │
  ▼
[Registration Modal / Form]
  │
  ├─ Input: username, email, password, fullName, gender, dob
  │
  ▼
[System Validates]
  ├─ Email format valid?         → NO  → ERR: AUTH-001
  ├─ Email unique in Accounts?   → NO  → ERR: Email already exists
  └─ Password >= 8 chars, 1 upper, 1 lower, 1 digit?  [BR-SYS-07]
              → NO  → ERR: AUTH-001 (MSG14)
  │
  ▼
[BCrypt hash password (cost=10)]  [BR-SYS-01]
  │
  ▼
[@Transactional INSERT]
  ├─ Accounts (is_active=true, role=CUSTOMER)
  └─ Customers (fullName, gender, dob, email)
  │
  ▼
[Send Welcome Email via SendGrid]
  │
  ▼
[Redirect → Login page]
```

### Login Flow

```
User
  │
  ├─ POST /auth/login { email, password }
  │
  ▼
[Spring Security UserDetailsService]
  ├─ Account found?    → NO  → ERR: AUTH-002
  ├─ is_active=true?   → NO  → ERR: MSG12 (account blocked)
  └─ BCrypt matches?   → NO  → Increment failedAttempts
                                  failedAttempts >= 5?
                                    YES → lock 15 min → ERR: AUTH-005 [BR-SYS-02]
                                    NO  → ERR: AUTH-002
  │
  ▼
[Auth SUCCESS]
  ├─ Reset failedAttempts = 0
  ├─ INSERT Audit_Log (event=LOGIN)  [BR-SYS-04]
  │
  ▼
[Role-Based Redirect]
  ├─ CUSTOMER    → /booking
  ├─ RECEPTIONIST → /receptionist/dashboard
  ├─ ADMIN       → /admin/dashboard
  ├─ MANAGER     → /manager/dashboard
  ├─ F&B STAFF   → /fbStaff/table-management
  ├─ KITCHEN     → /kitchenStaff/kitchen
  └─ TOUR GUIDE  → /tourguide/dashboard
```

### Google OAuth2 Alternative

```
Guest → [Sign In with Google] → GET /auth/google-login
  │
  ▼
[Google Auth Provider] → Returns email + displayName
  │
  ▼
[Email exists in DB?]
  ├─ YES → Login as existing Customer → Grant session
  └─ NO  → Auto-create Customer (random password) → Grant session
  │
  ▼
[Redirect /booking]
```

| Rule | Constraint |
|---|---|
| BR-SYS-01 | BCrypt hash mandatory; no plaintext password stored |
| BR-SYS-02 | Lock 15 min after 5 consecutive failures |
| BR-SYS-07 | Min 8 chars with upper, lower, digit |

---

<a id="wf-02"></a>
## WF-02 — Room Search & Online Booking

**Actor:** Guest, Customer  
**Business Rules:** BR-FO-01, BR-FO-02, BR-FIN-05  
**Use Cases:** UC10, UC11, UC12.1  
**Status:** ✅ Implemented

### Search Flow

```
Guest/Customer
  │
  ├─ Input: checkInDate, checkOutDate, guestsCount
  │
  ▼
[GET /api/rooms/search]
  │
  ▼
[RoomServiceImpl validates]
  ├─ checkIn >= today?           → NO  → ERR: BOOK-003
  └─ checkOut > checkIn + 1 day? → NO  → ERR: BOOK-003
  │
  ▼
[Query available RoomCategories]
  ├─ Exclude rooms with overlapping RoomBookingDetails
  └─ JOIN Daily_Rates → calculate price per category per night
  │
  ▼
[Return: categories + availability count + total estimated price]
```

### Booking & Payment Flow

```
Customer selects category + quantity
  │
  ├─ Not logged in? → Show auth modal → Login → Resume
  │
  ▼
[POST /api/bookings]
  ├─ INSERT Booking (status=Pending)
  ├─ INSERT RoomBooking
  ├─ INSERT RoomBookingDetails (status=Pending)
  └─ SET holdExpiresAt = now + 15 minutes  [BR-FO-02]
  │
  ▼
[Apply Voucher? — Optional]  [BR-FIN-05]
  ├─ POST /api/bookings/{id}/apply-coupon
  ├─ Validate: is_active, valid date range, max_uses not exceeded
  └─ Max 1 promo code per booking enforced
  │
  ▼
[Redirect to VNPay Sandbox]
  │
  ▼
[Customer completes deposit payment]
  │
  ▼
[VNPay IPN Callback → POST /api/v1/payments/vnpay-ipn]
  ├─ Verify HMAC signature
  │
  ├─ SUCCESS →
  │     UPDATE Booking.status = Confirmed
  │     INSERT PaymentTransaction (type=DEPOSIT)
  │     Send confirmation email
  │     Redirect /booking-confirmation
  │
  └─ FAILURE / TIMEOUT (> 15 min) →
        job booking_cleanup triggers
        UPDATE Booking.status = Cancelled
        Release room hold
        ERR: BOOK-002
```

### Overbooking Prevention (BR-FO-01)

```
Layer 1: SELECT ... FOR UPDATE on Room_Booking_Details
Layer 2: @Version optimistic lock on Bookings JPA entity
Layer 3: TRG_Prevent_Overbooking at database level
```

---

<a id="wf-03"></a>
## WF-03 — Check-In (Sảnh)

**Actor:** Receptionist  
**Business Rules:** BR-FO-03, BR-FO-04, BR-FO-06, BR-SYS-01  
**Use Cases:** UC12.2, UC12.3, UC12.4  
**Status:** ⚠️ Partial (no OCR CCCD)

### Check-In Flow

```
Receptionist
  │
  ├─ GET /receptionist/check-in → Search booking
  │
  ▼
[Verify Booking Status]
  ├─ status = Confirmed?   → NO → Cannot check-in
  └─ Deposit paid?         → NO → Collect payment first
  │
  ▼
[Verify Guest Identity]  [BR-FO-03]
  ├─ Primary contact age >= 18?  → NO → Reject
  └─ Valid ID (CCCD/Passport)?   → NO → Reject
  │
  ▼
[Register Room Guests]  [UC12.2]
  ├─ POST /api/bookings/{id}/guests
  ├─ Link Dependents / companions
  ├─ Set Primary Contact (legal representative)
  └─ Validate: total guests <= room max_occupancy  [BR-FO-07]
  │
  ▼
[Select Physical Room]  [BR-FO-04]
  └─ ONLY rooms with status = Vacant_Clean allowed
  │
  ▼
[Configure Spending Authorization]  [UC12.4]
  ├─ isChargeToRoomAllowed = true
  ├─ subCreditLimit (must be <= Booking.creditLimit)  [BR-FO-06]
  └─ PIN (4 digits) → BCrypt hash → personalPinHash  [BR-SYS-01]
  │
  ▼
[POST /receptionist/checkin/complete]
  ├─ UPDATE RoomBookingDetail.detail_status = Checked_In
  ├─ UPDATE Booking.status = Checked_In
  └─ UPDATE Room.room_status = Occupied
  │
  ▼
[INSERT AuditLog: CHECK_IN]  [BR-SYS-04]
  │
  ▼
[Check-in Complete → Issue room key]
```

---

<a id="wf-04"></a>
## WF-04 — Check-Out & Invoice Settlement

**Actor:** Receptionist  
**Business Rules:** BR-FIN-01, BR-FO-04, BR-HK-01, BR-SYS-04  
**Use Cases:** UC12.6, UC26.5, UC27.4  
**Status:** ✅ Implemented

### Check-Out Flow

```
Receptionist
  │
  ├─ GET /receptionist/in-house → Select guest
  │
  ▼
[Load All Folio Items]
  ├─ GET /api/folios/room/{roomBookingDetailId}
  └─ Aggregates: Room charges + F&B + Tour + Minibar + Extras
  │
  ▼
[Generate Consolidated Invoice Preview]
  ├─ Sum: All FolioItems (not separately settled)
  ├─ Subtract: deposit already paid
  ├─ Apply: promo/membership discount
  ├─ Add: VAT
  └─ = OUTSTANDING BALANCE
  │
  ▼
[Balance > 0?]  [BR-FIN-01]
  │
  ├─ YES → Block checkout → Collect payment
  │     ├─ Methods: Cash | Card | Bank Transfer | VNPay  [BR-FIN-06]
  │     ├─ INSERT PaymentTransaction per payment split
  │     └─ Repeat until Balance = 0
  │
  └─ NO → Proceed to checkout
  │
  ▼
[POST /api/folios/room/{id}/checkout]
  ├─ UPDATE ConsolidatedInvoice.status = Paid
  ├─ UPDATE RoomBookingDetail.detail_status = Checked_Out
  ├─ UPDATE Booking.status = Checked_Out
  └─ UPDATE Room.room_status = Vacant_Dirty  [BR-FO-04]
  │
  ▼
[DB Trigger: TRG_Auto_Housekeeping_Task]  [BR-HK-01]
  └─ INSERT Hotel_Operations (type=CHECKOUT_CLEAN, priority=High)
  │
  ▼
[Generate & Send Invoice]
  ├─ InvoicePdfService.generate() → PDF
  └─ EmailServiceImpl.sendInvoiceEmail() → Customer inbox
  │
  ▼
[INSERT AuditLog: CHECK_OUT]  [BR-SYS-04]
```

---

<a id="wf-05"></a>
## WF-05 — Room Service (E-Menu / Post-to-Room)

**Actor:** Customer (Checked-In)  
**Business Rules:** BR-FB-01, BR-FB-02, BR-FB-05  
**Use Cases:** UC16, UC16.1, UC16.2, UC16.3  
**Status:** ✅ Implemented

### Order Flow

```
Customer (Checked-In)
  │
  ├─ QR code scan in room → /order-food
  ├─ Not logged in? → Auth modal → Login → Resume
  │
  ▼
[Display E-Menu]
  ├─ Only Menu_Items where is_available = true  [BR-FB-02]
  └─ Prices include 10% room service surcharge  [BR-FB-05]
  │
  ▼
[Customer builds cart → Review total]
  │
  ▼
[Choose Payment Method]
  │
  ├─ [A] Pay Now via VNPay
  │     POST /api/v1/payments/food-order/{id}/vnpay
  │     On success:
  │       INSERT FoodOrder (status=Confirmed, type=ROOM_SERVICE)
  │       INSERT FoodOrderDetails
  │       Push order to KDS
  │
  └─ [B] Charge to Room (Post-to-Room)  [BR-FB-01]
        │
        ▼
      [Validate all conditions simultaneously]
        ├─ Room detail_status = Checked_In?     → NO → ERR: POS-003
        ├─ isChargeToRoomAllowed = true?         → NO → ERR: POS-003
        │
        ▼
      [Customer enters 4-digit PIN]
        └─ BCrypt.verify(pinRaw, personalPinHash) → mismatch → ERR
        │
        ▼
      [TRG_Folio_Credit_Limit_Check at DB level]
        └─ currentDebt + orderTotal > subCreditLimit?
              YES → Rollback → ERR: POS-003
        │
        ▼
      [INSERT FoodOrder (payment_type=CHARGE_TO_ROOM)]
      [INSERT FolioItem (source_department=FB)]
      [Push order to KDS]
```

---

<a id="wf-06"></a>
## WF-06 — POS Dine-In & F&B Payment

**Actor:** F&B Staff (Cashier)  
**Business Rules:** BR-FB-01, BR-FB-04  
**Use Cases:** UC17, UC18  
**Status:** ✅ Implemented

### Dine-In Order Flow

```
F&B Staff
  │
  ├─ Login → /fbStaff/table-management
  ├─ Select or scan table
  │
  ▼
[POST /fbStaff/create-food-order → /api/pos/orders]
  ├─ type = DINE_IN
  ├─ INSERT FoodOrder (status=PENDING, table_id=X)
  └─ INSERT FoodOrderDetails (kotStatus=PENDING per item)
  │
  ▼
[KDS receives order]
  │
  ▼
[Kitchen processes: PENDING → COOKING → READY → SERVED]
  │
  ▼
[F&B Staff processes payment]
  │
  ├─ [Direct Payment: Cash/Card]
  │     POST /api/pos/orders/{id}/pay
  │     INSERT PaymentTransaction (type=POS_DIRECT)
  │     UPDATE FoodOrder.isPaidInPos = true
  │
  └─ [Post-to-Room]  [BR-FB-01]
        F&B Staff enters room number
        Verify: room Checked_In + isChargeToRoomAllowed
        Customer enters PIN on POS device
        BCrypt.verify() → mismatch → REJECT
        TRG_Folio_Credit_Limit_Check → over limit → REJECT
        INSERT FolioItem (source=FB)
        UPDATE FoodOrder: payment_type=CHARGE_TO_ROOM
  │
  ▼
[NOTE: Orders at COOKING+ state cannot be modified]  [BR-FB-04]
  └─ Any cancellation requires separate VOID ticket + AuditLog entry
```

---

<a id="wf-07"></a>
## WF-07 — KDS Kitchen Order Lifecycle

**Actor:** Kitchen Staff  
**Business Rules:** BR-FB-02, BR-FB-04  
**Use Cases:** UC19  
**Status:** ✅ Implemented (polling, not WebSocket)

### KDS Status Flow

```
New Order Created (POS / Room Service)
  │
  ▼
[KDS Dashboard: /kitchenStaff/kitchen]
  ├─ Displays: FoodOrderDetails where kotStatus = PENDING
  └─ Room Service orders flagged with priority label
  │
  ▼
[Kitchen Staff picks up order]
  │
  ▼
PUT /api/pos/orders/{id}/status  →  kotStatus: PENDING → COOKING
  │
  ▼
[Food prepared]
  │
  ▼
PUT → kotStatus: COOKING → READY
  │  (system notifies serving staff via polling)
  │
  ▼
[Dish delivered to customer]
  │
  ▼
PUT → kotStatus: READY → SERVED
```

### Out-of-Stock Management (BR-FB-02)

```
Kitchen Staff detects ingredient shortage
  │
  ▼
[POST /api/menu-items/{id}/toggle]
  └─ UPDATE Menu_Items.is_available = false
  │
  ▼
[System propagates immediately]
  ├─ POS terminals: item greyed out / ordering disabled
  ├─ Room Service E-Menu: item hidden
  └─ KDS: item flagged unavailable

[Kitchen restocks] → Toggle back → is_available = true → Item reappears
```

---

<a id="wf-08"></a>
## WF-08 — Table Reservation

**Actor:** Customer  
**Business Rules:** BR-FB-03  
**Use Cases:** UC14  
**Status:** ✅ Implemented

### Reservation Flow

```
Customer
  │
  ├─ Navigate to /book-table
  ├─ Select: reserveDate, reserveTime, guestsCount, tableId
  │
  ▼
[GET /api/v1/tables/availability]
  └─ Check Restaurant_Tables for conflicts at same time slot
  │
  ▼
[Validate]  [BR-FB-03]
  ├─ reserveTime >= now + 1 hour?         → NO  → ERR
  └─ Table not booked for overlapping slot? → NO  → ERR
  │
  ▼
[POST /api/v1/tables/reservations]
  ├─ INSERT TableReservation (status=Confirmed)
  └─ UPDATE RestaurantTable.status = Reserved
  │
  ▼
[Send confirmation email: email/booking-table.html]
```

### Auto-Release (BR-FB-03)

```
Cron job: reservation_cleanup
  │
  ▼
SELECT TableReservations WHERE status=Confirmed AND reserveTime + 30min < now()
  │
  ▼
UPDATE TableReservation.status = Expired
UPDATE RestaurantTable.status = Available
  │
  ▼
Table freed for new bookings
```

---

<a id="wf-09"></a>
## WF-09 — Tour Booking

**Actor:** Customer, Receptionist  
**Business Rules:** BR-TR-01, BR-TR-05  
**Use Cases:** UC20, UC21, UC22.6  
**Status:** ✅ Implemented

### Tour Search & Booking Flow

```
Customer/Guest
  │
  ├─ Browse /tours or /tours/search
  ├─ GET /api/weather → Display weather forecast per tour date
  │
  ▼
[Select Tour + Schedule]
  └─ View TourSchedule: departureDate, time, available seats
  │
  ▼
[Enter participant count]
  │
  ▼
[TRG_Tour_Capacity_Validator]  [BR-TR-01]
  └─ booked_seats + participantCount > max_capacity?
        YES → ROLLBACK → ERR: TOUR-001
  │
  ▼
[POST /api/tour-bookings]
  ├─ INSERT Booking (type=TOUR)
  ├─ INSERT TourBooking
  ├─ INSERT TourAttendees per participant
  └─ UPDATE TourSchedule.booked_seats += participantCount
  │
  ▼
[Payment Method]
  │
  ├─ [VNPay Direct] → Redirect VNPay → Callback → INSERT PaymentTransaction
  └─ [Post-to-Room] → PIN verify → Credit check → INSERT FolioItem (source=TOUR)
  │
  ▼
[Email: tour confirmation sent]
```

### Auto-Cancel T-24h (BR-TR-05)

```
Scheduler at T-24h before each departure
  │
  ▼
total_booked < minimum_pax?
  │
  ├─ YES:
  │   UPDATE TourSchedule.status = Cancelled
  │   UPDATE TourBookings.status = Cancelled
  │   Process refunds (VNPay Refund API / remove FolioItems)
  │   Send cancellation + apology email to all participants
  │
  └─ NO: Tour runs as scheduled → Assign guides → Proceed
```

---

<a id="wf-10"></a>
## WF-10 — AI Face Scan Attendance

**Actor:** Tour Guide  
**Business Rules:** BR-TR-02  
**Use Cases:** UC04.2, UC22.5, UC22.6  
**Status:** ⚠️ Partial (demo hardcoded references)

### Face Scan Flow

```
Tour Guide at departure checkpoint
  │
  ├─ Open /tourguide/FaceID on mobile
  ├─ Camera captures passenger photo
  │
  ▼
[POST /api/faceid/scan] { imageFile, scheduleId }
  │
  ▼
[Spring Boot → forwards to Python kawai-ai-service]
  │
  ▼
[Python AI (dlib/OpenCV)]
  ├─ Extract 128-dim face embedding from captured image
  ├─ Compare vs face_vector_data stored in TourAttendees
  └─ Calculate Cosine Similarity score
  │
  ▼
[Cosine Similarity >= 0.85?]  [BR-TR-02]
  │
  ├─ YES → Identity confirmed
  │     UPDATE TourAttendees.attendance_status = Boarded
  │     UPDATE TourAttendees.face_matched_at = now()
  │     Return: passenger name + confirmation
  │
  └─ NO → Threshold not met (dark/angle/blur)
        Flag for manual verification
```

### Manual Fallback (BR-TR-02)

```
[POST /api/v1/tour-attendance/{attendeeId}/manual]
  ├─ Tour Guide manually ticks attendance
  └─ UPDATE TourAttendees.attendance_status = Boarded (manual)
```

---

<a id="wf-11"></a>
## WF-11 — Room State Machine (Housekeeping & Maintenance)

**Actor:** System, Housekeeper, Maintenance Staff, Receptionist  
**Business Rules:** BR-FO-04, BR-FO-05, BR-HK-01, BR-HK-02  
**Use Cases:** UC13  
**Status:** ⚠️ Service exists, no dedicated mobile UI

### State Diagram

```
                 ┌──────────────────┐
           ┌────►│   Vacant_Clean   │◄──────────────────┐
           │     └────────┬─────────┘                    │
           │              │ Receptionist assigns room     │
           │              │ at Check-in (only from here) │
           │              ▼                              │
           │     ┌──────────────────┐                   │
           │     │     Occupied     │                   │
           │     └────────┬─────────┘                   │
           │              │ Receptionist confirms         │
           │              │ Check-out                    │
           │              ▼                              │
           │     ┌──────────────────┐   Housekeeper     │
           │     │  Vacant_Dirty   │──── marks Done ────┘
           │     └────────┬─────────┘
           │              │ Housekeeper
           │              │ reports damage
           │              ▼
           │     ┌──────────────────┐   Maintenance
           └─────│  Maintenance    │◄── marks Fixed
                 └──────────────────┘
```

### Post-Checkout Housekeeping Flow (BR-HK-01)

```
Checkout event triggers DB Trigger: TRG_Auto_Housekeeping_Task
  │
  ├─ INSERT Hotel_Operations (type=CHECKOUT_CLEAN, priority=High)
  └─ UPDATE Room.room_status = Vacant_Dirty
  │
  ▼
Housekeeper opens task queue (mobile)
  ├─ Rush Room flagged? → Move to TOP of queue  [BR-FO-05]
  ├─ Clean room
  └─ Mark task = Completed
  │
  ▼
UPDATE Hotel_Operations.status = Completed
UPDATE Room.room_status = Vacant_Clean
  │
  ▼
Room Matrix shows room as Vacant_Clean → Available for check-in
```

### Maintenance Flow (BR-HK-02)

```
Housekeeper reports broken equipment
  │
  ▼
DB Trigger: TRG_Maintenance_Auto_Bridge
  ├─ INSERT Hotel_Operations (type=MAINTENANCE)
  └─ UPDATE Room.room_status = Maintenance
  │
  ▼
Room BLOCKED from check-in assignment on Room Matrix
  │
  ▼
Maintenance Staff receives ticket
  ├─ Fixes equipment
  └─ Marks task = Resolved
  │
  ▼
UPDATE Room.room_status = Vacant_Dirty (needs cleaning)
  OR
UPDATE Room.room_status = Vacant_Clean (if already clean)
```

---

<a id="wf-12"></a>
## WF-12 — Night Audit (02:00 AM Cron)

**Actor:** System (Scheduled Job)  
**Business Rules:** BR-FIN-03, BR-FIN-04, BR-FIN-07  
**Use Cases:** UC27.1, UC30  
**Status:** ⚠️ Bug: status enum mismatch (OCCUPIED vs Checked_In)

### Night Audit Flow

```
02:00 AM — DynamicJobManager triggers
  │
  ▼
NightAuditServiceImpl.runNightAudit()
POST /api/v1/audit/night-audit (internal call)
  │
  ▼
[Step 1] Scan all Checked-In Rooms
  └─ SELECT RoomBookingDetails WHERE detail_status = 'Checked_In'
  │
  ▼
[Step 2] For each active room
  └─ GET Daily_Rates[roomCategoryId][today]
        → No Daily_Rate? → ALERT Manager via email → Skip room
  │
  ▼
[Step 3] Post Room Charge to Folio
  └─ INSERT FolioItem:
        source_department = 'ROOM'
        amount = dailyRate.rate
        room_booking_detail_id = X
        posting_date = today
  │
  ▼
[Step 4] Advance Business Date
  └─ systemDate = systemDate + 1 day
  │
  ▼
[Step 5] Closing Balance Summary  [BR-FIN-04]
  └─ Calculate revenue for closed day:
        Room Revenue | F&B Revenue | Tour Revenue (USALI split)
  │
  ▼
[Step 6] Lock Previous Day Records  [BR-FIN-07]
  └─ Flag transactions of previous day as immutable
        No UPDATE/DELETE allowed on locked financial records
  │
  ▼
[Step 7] Write Audit Log
  └─ INSERT Audit_Log: NIGHT_AUDIT_COMPLETE
        Rooms processed, total amount posted, errors list
```

### Error Handling

```
IF Daily_Rate missing for a room category:
  → Log warning in execution log
  → Send email alert to Manager
  → SKIP that room (audit continues for others)
  → Do NOT crash entire audit process
```

---

<a id="wf-13"></a>
## WF-13 — Folio Aggregation & Checkout Settlement

**Actor:** Receptionist, Customer  
**Business Rules:** BR-FIN-01, BR-FIN-06  
**Use Cases:** UC26, UC27.4  
**Status:** ✅ Implemented

### Folio Accumulation (During Stay)

```
Night Audit (02:00 AM daily)  → FolioItem (source=ROOM)
F&B Post-to-Room              → FolioItem (source=FB)
Tour Post-to-Room             → FolioItem (source=TOUR)
Minibar / damages / extras    → FolioItem (source=MISC)
```

### Settlement Flow

```
Receptionist opens Checkout Workspace
  │
  ├─ GET /api/folios/room/{roomBookingDetailId}
  │
  ▼
[Consolidated Invoice Preview]
  ├─ SUM all FolioItems (not separately settled)
  ├─ MINUS: initial deposit paid
  ├─ APPLY: promo discount / membership tier
  ├─ ADD: VAT
  └─ = OUTSTANDING BALANCE
  │
  ▼
[Balance > 0?]  [BR-FIN-01]
  │
  ├─ YES → Collect payment  [BR-FIN-06]
  │     ├─ Cash + Card + Transfer combinations allowed
  │     ├─ INSERT PaymentTransaction per payment type
  │     └─ Repeat collection until Balance reaches 0
  │
  └─ NO → Proceed
  │
  ▼
[Split Folio — Optional]  [UC26.4]
  ├─ PUT /api/folios/items/{id}/split
  └─ isSettledSeparately = true
       (e.g., company pays room, employee pays F&B)
  │
  ▼
[POST /api/folios/room/{id}/checkout]
  │
  ▼
[Final invoice issued]
  ├─ PDF signed with digital resort seal
  └─ Email sent automatically to customer
```

---

<a id="wf-14"></a>
## WF-14 — Walk-In Check-In

**Actor:** Receptionist  
**Business Rules:** BR-FO-01, BR-FO-03, BR-FO-04  
**Use Cases:** UC12.7  
**Status:** ✅ Implemented

### Walk-In Flow

```
Receptionist
  │
  ├─ GET /receptionist/walk-in
  │
  ▼
[Search / Create Customer]
  ├─ GET /api/walk-in/search-customer { cccd or phone }
  ├─ Found? → Use existing profile
  └─ Not found? → Create new Customer record
  │
  ▼
[Select Room]  [BR-FO-04]
  └─ Only rooms with status = Vacant_Clean available
  │
  ▼
[Calculate Charges]
  ├─ POST /api/walk-in/calculate-surcharge
  └─ Early check-in / Late check-out / child surcharge
  │
  ▼
[Verify Identity]  [BR-FO-03]
  ├─ Primary contact >= 18 years old
  └─ Valid CCCD or Passport
  │
  ▼
[Process Deposit via VNPay]
  ├─ Payment prefix = WALKIN_{bookingId}
  └─ POST /api/walk-in/checkin
  │
  ▼
[On VNPay Success]
  ├─ INSERT Booking (source=Walk_In, status=Confirmed)
  ├─ INSERT RoomBooking + RoomBookingDetail (status=Checked_In)
  ├─ UPDATE Room.status = Occupied
  ├─ Configure PIN + CreditLimit
  └─ INSERT AuditLog: WALK_IN_CHECKIN  [BR-SYS-04]
  │
  ▼
[Room assigned → Guest receives key]
```

---

<a id="wf-15"></a>
## WF-15 — Password Reset

**Actor:** Customer, Employee  
**Business Rules:** BR-SYS-01, BR-SYS-02  
**Use Cases:** UC02  
**Status:** ✅ Implemented

### Reset Flow

```
User
  │
  ├─ Click "Forgot Password" on login page
  │
  ▼
[POST /api/v1/auth/forgot-password] { email }
  │
  ├─ Email not found? → Silent success (no user enumeration leak)
  │
  ▼
[Generate UUID reset token]
  └─ INSERT Password_Reset_Tokens:
        token = UUID, expires_at = now + 15 min  [BR-SYS-02]
        used = false
  │
  ▼
[Send reset link email]
  └─ URL: /auth/reset-password?token={UUID}
  │
  ▼
User clicks link (within 15 minutes)
  │
  ▼
[GET /auth/reset-password?token=...]
  ├─ Token not found?  → Show error page
  ├─ Token expired?    → Show error page
  └─ Token used=true?  → Show error page
  │
  ▼
[Display form: guest/reset-password-form.html]
  │
  ▼
[POST /api/v1/auth/reset-password] { token, newPassword }
  │
  ├─ Validate password strength  [BR-SYS-07]
  │     min 8 chars, 1 upper, 1 lower, 1 digit
  │
  ▼
[BCrypt hash new password]  [BR-SYS-01]
  │
  ▼
UPDATE Accounts.password_hash = newHash
UPDATE Token.used = true  (one-time use only)  [BR-SYS-02]
  │
  ▼
[Redirect → Login page]
```

---

<a id="wf-16"></a>
## WF-16 — Booking Cancellation & Refund

**Actor:** Customer, Receptionist  
**Business Rules:** BR-FIN-02, BR-TR-05, BR-SYS-04  
**Use Cases:** UC31.2  
**Status:** ✅ Implemented

### Room Booking Cancellation

```
Customer
  │
  ├─ Opens /bookings → Select confirmed booking → Click "Cancel"
  │
  ▼
[POST /api/bookings/{id}/cancel]
  │
  ▼
[System validates]
  ├─ Booking status = Confirmed?    → NO → ERR
  └─ Not already Checked-In?       → NO → ERR
  │
  ▼
[Refund policy calculation]  [BR-FIN-02]
  │
  ├─ Cancel > 48h before checkIn?
  │   YES → Full refund (100% of deposit)
  │         INSERT PaymentTransaction (type=REFUND)
  │         VNPay Refund API called
  │         Email: "Full refund processed"
  │
  └─ Cancel <= 48h OR No-show?
        NO refund
        Deposit forfeited → recorded as penalty revenue
        Email: "Booking cancelled, deposit forfeited"
  │
  ▼
UPDATE Booking.status = Cancelled
Release room hold → available for new bookings
INSERT AuditLog: BOOKING_CANCELLED  [BR-SYS-04]
```

### Tour Cancellation (BR-TR-05)

```
Resort-initiated cancellation (weather/incident):
  → 100% refund regardless of timing
  → Send apology email + refund confirmation to all participants

Customer self-cancellation within 24h:
  → 50% deposit forfeited per BR-TR-05
```

---

<a id="wf-17"></a>
## WF-17 — Admin Master Data Management

**Actor:** Admin, Manager  
**Business Rules:** BR-DAT-01, BR-DAT-02, BR-DAT-03, BR-SYS-04  
**Use Cases:** UC06, UC07, UC08, UC09  
**Status:** ✅ Mostly Implemented

### Rooms & Categories CRUD

```
[CRUD /admin/api/v1/rooms]
  │
  ├─ CREATE: assign category, floor, number → status = Vacant_Clean
  ├─ UPDATE: allowed when not Occupied
  └─ DELETE:  [BR-DAT-01]
        Check: active booking for this room?
          YES → BLOCK deletion
          NO  → Soft delete / deactivate
  │
  ▼
INSERT AuditLog for all mutations  [BR-SYS-04]
```

### Dynamic Pricing CRUD (BR-DAT-03)

```
[CRUD /admin/api/v1/pricing]
  │
  ├─ SET rate for specific date per room category
  ├─ INSERT Daily_Rates (category_id, date, rate)
  │
  └─ Overlap validation:
        Same category + overlapping date range already exists?
          YES → BLOCK → ERR: "Overlapping pricing period"
```

### Employee Accounts (BR-DAT-02)

```
[POST /admin/api/v1/employees]
  │
  ├─ @Transactional block:
  │     INSERT Accounts (with role assigned)
  │     INSERT Employees (linked profile)
  │       → Both succeed OR both rollback
  │
  ├─ One role per employee at any time
  └─ Role change: explicit reassignment required
```

### Audit Log Review (Read-Only)

```
[GET /admin/audit-log]
  ├─ Filter: date range, employee, action type
  └─ READ ONLY: Audit_Logs table blocks UPDATE/DELETE  [BR-SYS-04]
```

---

<a id="wf-18"></a>
## WF-18 — Scheduled Jobs Automation

**Actor:** System  
**Business Rules:** BR-FO-02, BR-FB-03, BR-FIN-03  
**Use Cases:** UC30  
**Status:** ✅ Implemented via DynamicJobManager

### Jobs Overview

```
DynamicJobManager (Spring @Scheduled)
  │
  ├─ booking_cleanup          every 5 min   [BR-FO-02]
  │     SELECT Bookings WHERE status=Pending AND holdExpiresAt < now()
  │     UPDATE Booking.status = Cancelled
  │     Log: "Auto-cancelled {n} expired bookings"
  │
  ├─ reservation_cleanup      every 30 min  [BR-FB-03]
  │     SELECT TableReservations WHERE Confirmed AND time + 30min < now()
  │     UPDATE TableReservation.status = Expired
  │     UPDATE RestaurantTable.status = Available
  │
  ├─ table_cleanup            hourly
  │     Reset orphaned table states
  │
  ├─ audit_cleanup            daily
  │     Archive old audit logs per retention policy
  │     (Cannot physically delete: Audit_Logs INSERT-only)  [BR-SYS-04]
  │
  ├─ workflow_processor       every 15 min
  │     Scan workflow items for SLA breaches
  │     Escalate overdue approvals
  │     Notify responsible employees
  │
  └─ night_audit              daily 02:00 AM  [BR-FIN-03]
        NightAuditServiceImpl.runNightAudit()
        Post room charges to folios
        Advance business date
        Lock previous day records
```

### Admin Control Panel

```
Admin manages all jobs at /admin/cronjobs
  ├─ View: last run, next run, execution logs
  ├─ Toggle: enable / disable individual jobs
  └─ Manual trigger: POST /admin/api/v1/cronjobs/{id}/run
```

---

## Phụ lục — Bảng Tổng hợp Workflows

| WF ID | Tên Workflow | Actor chính | BR áp dụng | UC liên quan | Status |
|---|---|---|---|---|---|
| WF-01 | Customer Registration & Login | Guest, Customer | BR-SYS-01, 02, 07 | UC01, UC02 | ✅ |
| WF-02 | Room Search & Online Booking | Customer | BR-FO-01, 02; BR-FIN-05 | UC10, UC11 | ✅ |
| WF-03 | Check-In (Sảnh) | Receptionist | BR-FO-03, 04, 06 | UC12.2–12.4 | ⚠️ |
| WF-04 | Check-Out & Invoice | Receptionist | BR-FIN-01; BR-FO-04; BR-HK-01 | UC12.6, UC26.5 | ✅ |
| WF-05 | Room Service / E-Menu | Customer | BR-FB-01, 02, 05 | UC16 | ✅ |
| WF-06 | POS Dine-In & Payment | F&B Staff | BR-FB-01, 04 | UC17, UC18 | ✅ |
| WF-07 | KDS Kitchen Lifecycle | Kitchen Staff | BR-FB-02, 04 | UC19 | ✅ |
| WF-08 | Table Reservation | Customer | BR-FB-03 | UC14 | ✅ |
| WF-09 | Tour Booking | Customer | BR-TR-01, 05 | UC20, UC21 | ✅ |
| WF-10 | AI Face Scan Attendance | Tour Guide | BR-TR-02 | UC04.2, UC22.5 | ⚠️ |
| WF-11 | Room State Machine | System, HK, Maintenance | BR-FO-04, 05; BR-HK-01, 02 | UC13 | ⚠️ |
| WF-12 | Night Audit 02:00 AM | System | BR-FIN-03, 04, 07 | UC27.1, UC30 | ⚠️ |
| WF-13 | Folio Aggregation & Settlement | Receptionist | BR-FIN-01, 06 | UC26 | ✅ |
| WF-14 | Walk-In Check-In | Receptionist | BR-FO-01, 03, 04 | UC12.7 | ✅ |
| WF-15 | Password Reset | Customer, Employee | BR-SYS-01, 02 | UC02 | ✅ |
| WF-16 | Booking Cancellation & Refund | Customer | BR-FIN-02; BR-SYS-04 | UC31.2 | ✅ |
| WF-17 | Admin Master Data Management | Admin | BR-DAT-01, 02, 03 | UC06–UC09 | ✅ |
| WF-18 | Scheduled Jobs Automation | System | BR-FO-02; BR-FB-03; BR-FIN-03 | UC30 | ✅ |

**Legend:** ✅ Fully implemented E2E | ⚠️ Partial / has known gaps | ❌ Not yet implemented

---

*Document generated by Antigravity — Business Analyst Review — 2026-06-29*  
*Source: business_rule.md · UC_MASTER_TABLE.md · UC_DETAIL_SPEC.md · requirement_traceability_matrix.md*
