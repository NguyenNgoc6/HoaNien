# ENGINEERING DOCUMENTATION STANDARD (EDS) & TESTING SPECIFICATION v2.0
## HỆ THỐNG QUẢN LÝ NGHỈ DƯỠNG KAWAI RETREAT RESORT & HUB

| Field          | Value                                                                                  |
| -------------- | -------------------------------------------------------------------------------------- |
| Document ID    | KAWAI-ALL-EDS-001                                                           |
| Version        | 1.0                                                                                    |
| Date           | 2026-06-09                                                                             |
| Status         | Draft / In Review / Approved                                             |
| Document Owner | Trưởng Nhóm Phát Triển                                                   |
| Author         | Antigravity                                                                           |
| Reviewed by    | Tech Lead                                                                            |
| Based on EDS   | v2.0                                                                                   |

## MỤC LỤC
1. Tổng quan Module
2. Ma trận Truy vết (Traceability Matrix)
3. Architecture Decision Records (ADR)
4. Non-Functional Requirements & SLA
5. Static Modeling (Mô hình Tĩnh)
6. Dynamic Modeling (Mô hình Động)
7. Quy trình Triển khai & Kiểm thử Toàn diện (Đặc tả Kiểm thử Chi tiết)
8. Phương pháp Xác minh

---

## 1. Tổng quan Hệ thống
| Field                 | Value                                                  |
| --------------------- | ------------------------------------------------------ |
| Module Name           | KAWAI RETREAT RESORT SYSTEM (5 Modules)                |
| Bounded Context       | Quản lý lưu trú, F&B, Tour, Khách hàng, Thanh toán      |
| Data Classification   | Sensitive-PII (CCCD, Hộ chiếu) / Internal (Doanh thu)  |
| Compliance Scope      | Nghị định 13/2023/NĐ-CP, USALI (Báo cáo Kế toán)      |

---

## 2. Ma trận Truy vết (Traceability Matrix)
| Requirement ID | Loại | Mô tả yêu cầu | Thành phần Code | Compliance Target |
| -------------- | ---- | ------------- | --------------- | ----------------- |
| UC07           | BR   | Ẩn danh hóa dữ liệu người dùng (Quyền được quên) | `UserService.anonymizeUser()` | Nghị định 13/2023/NĐ-CP |
| UC10, UC20     | BR   | Chống Double-booking cho Phòng và Tour | `BookingRepository` (Pessimistic Locking) | Data Integrity |
| UC24           | BR   | Night Audit tự động gom bill và chốt ngày | `NightAuditScheduler` | Chuẩn Kế toán USALI |

---

## 3. Architecture Decision Records (ADR)

### ADR-001 — Cơ chế Khóa (Locking Mechanism) cho Booking
**Bối cảnh:** Ngăn chặn nhiều khách hàng hoặc nhân viên đặt cùng một phòng/ghế tour trong cùng 1 mili-giây.
**Quyết định:** Sử dụng **Pessimistic Locking (Khóa bi quan)** qua JPA `@Lock(LockModeType.PESSIMISTIC_WRITE)` ở tầng Repository khi query phòng trống.
**Hệ quả:**
- Đảm bảo tính toàn vẹn 100% cho Inventory.
- Trade-off: Giảm Throughput khi request cùng tranh chấp 1 phòng, nhưng hệ thống Resort không có lượng traffic siêu khủng như TMĐT nên mức giảm này có thể chấp nhận.

---

## 4. Non-Functional Requirements & SLA

### 4.1. Sự Toàn vẹn Dữ liệu (Data Integrity)
| Category | Requirement | Target | Verification Method |
| --- | --- | --- | --- |
| ACID | Folio & Checkout | RPO = 0 | Transaction Management `@Transactional` |
| Concurrency | Chống Overbooking | 0% lỗi | JMeter Multi-thread Test |

---

## 5. Domain Event Catalog (Events Giao tiếp Giữa 5 Modules)
Hệ thống sử dụng ApplicationEventPublisher (Spring) để giao tiếp nội bộ giữa các module.

### Events Published (Phát ra)
| Event Name | Publisher | Subscriber(s) | Action |
| --- | --- | --- | --- |
| `PosOrderCompleted` | Module 3 (POS) | Module 5 (Folio) | Ghi nợ `Post to Room` vào hóa đơn phòng. |
| `RoomCheckedOut` | Module 2 (Tiền sảnh) | Module 5 (Hóa đơn), Module 3 (Housekeeping)| Kích hoạt dọn phòng & Đóng Folio. |
| `NightAuditTriggered`| Hệ thống Cron | Tất cả Modules | Chốt sổ giao dịch ngày. |

---

## 6. KỊCH BẢN KIỂM THỬ CHI TIẾT (Đặc tả Test toàn bộ nghiệp vụ - Theo Mẫu EDS Section 13)

### KỊCH BẢN 1: INTEGRATION E2E - LUỒNG CHECK-IN, POST TO ROOM & CHECK-OUT TRỌN VẸN
**TC-E2E-FULLFLOW-001**
```gherkin
Feature: Luồng vận hành lõi tích hợp 3 Modules (Front Desk, POS, Folio)
  Background:
    Given Hệ thống có phòng R202 trống.
    And test data classification: SYNTHETIC

  Scenario: Khách Check-in, ăn nhà hàng ghi nợ và Check-out thanh toán 1 lần
    # Module 2
    Given Lễ tân thực hiện Check-in cho khách vào R202
    Then Trạng thái phòng R202 chuyển thành OCCUPIED
    And Một Folio trống được khởi tạo cho R202

    # Module 3
    When Khách xuống nhà hàng gọi món hết 1,000,000 VND
    And Nhân viên POS chọn thanh toán "Post to Room" cho phòng R202
    Then Hệ thống ghi nhận 1,000,000 VND vào Folio của R202

    # Module 5
    When Khách làm thủ tục Check-out
    Then Hệ thống chặn Check-out và báo nợ 1,000,000 VND
    When Lễ tân thực hiện thanh toán tất toán Folio (Tiền mặt/Thẻ)
    Then Folio chuyển trạng thái SETTLED
    And Trạng thái phòng R202 chuyển thành DIRTY (Chờ dọn dẹp)
```

### KỊCH BẢN 2: BẢO MẬT PII & ẨN DANH DỮ LIỆU
**TC-SECURITY-PII-001**
```gherkin
  Scenario: Chạy tiến trình ẩn danh dữ liệu khách hàng
    Given Tài khoản khách hàng có ID "USR-001" với CCCD "012345678900"
    When DPO hoặc Khách hàng yêu cầu xóa tài khoản
    Then Hệ thống cập nhật bản ghi "USR-001"
    And Cột CCCD, Số điện thoại, Tên bị băm (hashed) thành chuỗi vô nghĩa
    And Booking lịch sử của "USR-001" vẫn được giữ nguyên để kiểm toán tài chính
```

### KỊCH BẢN 3: NIGHT AUDIT KIỂM TOÁN ĐÊM CHỐT DOANH THU
**TC-INT-AUDIT-001**
```gherkin
  Scenario: Hệ thống chạy Night Audit vào 2h sáng
    Given 5 phòng đang OCCUPIED
    When Job NightAuditScheduler kích hoạt
    Then Giá phòng của 5 phòng được tự động cộng thêm 1 ngày vào Folio tương ứng
    And Báo cáo doanh thu ngày hiện tại được chốt (Freezed)
    And System Business Date tiến lên 1 ngày
```

---

## 6. API Specification — UC19: Tìm kiếm Gói Tour (Module 4)

### 6.1. Endpoint

| Method | Path | Auth Level | Required Roles | Rate Limit | Idempotent? |
|--------|------|------------|----------------|------------|-------------|
| GET | `/tours/search` | Public (Guest / Customer) | Guest, Customer | 100/min | Yes |

### 6.2. Request Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `fromDate` | `LocalDate` (ISO 8601) | No | Today | Ngày bắt đầu tìm kiếm (yyyy-MM-dd) |
| `toDate` | `LocalDate` (ISO 8601) | No | `fromDate + 7 days` | Ngày kết thúc tìm kiếm (yyyy-MM-dd) |

### 6.3. Response — 200 OK (Happy Path)

```json
{
  "tours": [
    {
      "tourId": 1,
      "tourName": "Vịnh Hạ Long - 1 Ngày",
      "tourType": "DAY_TRIP",
      "basePrice": 1500000,
      "maxCapacity": 30,
      "availableSlots": 15,
      "departureDate": "2026-06-20",
      "scheduleStatus": "Open",
      "weatherAvailable": true,
      "weatherDescription": "Sunny",
      "temperature": 32.5
    }
  ],
  "fromDate": "2026-06-15",
  "toDate": "2026-06-25"
}
```

### 6.4. Response — 200 OK (Weather API Unavailable)

```json
{
  "tours": [
    {
      "tourId": 1,
      "tourName": "Vịnh Hạ Long - 1 Ngày",
      "tourType": "DAY_TRIP",
      "basePrice": 1500000,
      "maxCapacity": 30,
      "availableSlots": 15,
      "departureDate": "2026-06-20",
      "scheduleStatus": "Open",
      "weatherAvailable": false,
      "weatherDescription": null,
      "temperature": null
    }
  ],
  "fromDate": "2026-06-15",
  "toDate": "2026-06-25"
}
```

### 6.5. Business Rules
| Rule ID | Description | Implementation |
|---------|-------------|---------------|
| BR-TR-01 | Chỉ hiển thị lịch trình có trạng thái "Open" | `TourScheduleRepository.findByDepartureDateBetweenAndScheduleStatus(fromDate, toDate, "Open")` |
| GRACEFUL-DEG-01 | Nếu OpenWeather API lỗi, tour vẫn hiển thị, ẩn thông tin thời tiết | `TourServiceImpl.enrichWithWeatherData()` catch RuntimeException, set `weatherAvailable = false` |

### 6.6. Error Codes
| Code | HTTP Status | Message | Trigger Condition |
|------|-------------|---------|------------------|
| `TOUR-001` | 400 | `fromDate must not be null` | Thiếu tham số fromDate (validation ở Service layer) |
| `TOUR-002` | 400 | `fromDate must not be after toDate` | fromDate > toDate |

---

## 7. Phương pháp Xác minh (API Verification Samples)

### 7.1. UC09 — Tìm kiếm phòng trống (RoomService.searchAvailableRooms)

**[POST] Tìm phòng trống**
```bash
# [POST] Tìm phòng trống theo khoảng ngày
curl -X POST https://api.kawairesort.com/api/v1/rooms/search \
  -H "Authorization: Bearer [CUSTOMER_TOKEN]" \
  -H "Content-Type: application/json" \
  -d '{
    "checkInDate": "2026-08-01",
    "checkOutDate": "2026-08-05",
    "categoryName": "Deluxe",
    "minCapacity": 2
  }'

# Expected Response (200):
{
  "status": "SUCCESS",
  "rooms": [
    {
      "roomId": 1,
      "roomNumber": "R101",
      "categoryName": "Deluxe",
      "pricePerNight": 2000000,
      "capacity": 2,
      "checkInDate": "2026-08-01",
      "checkOutDate": "2026-08-05"
    }
  ]
}
```

**Business Rules:**
* BR-FO-01: Chỉ hiển thị phòng không có booking trùng ngày
* BR-FO-04: Trạng thái phòng Vacant_Clean hoặc Available
* BR-FIN-05: Giá phòng BigDecimal scale 0, HALF_UP

**Implementation:**
* Interface: `RoomService.searchAvailableRooms(RoomSearchRequestDTO)`
* Repository: `RoomRepository.findAll()`, `RoomBookingRepository.countOverlappingBookings()`

---

### 7.2. UC10 — Đặt phòng & Thanh toán cọc (BookingService)

**[POST] Đặt phòng mới**
```bash
# [POST] Đặt phòng
curl -X POST https://api.kawairesort.com/api/v1/bookings \
  -H "Authorization: Bearer [CUSTOMER_TOKEN]" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "roomNumber": "R101",
    "checkInDate": "2026-08-15",
    "checkOutDate": "2026-08-20",
    "depositAmount": 2000000,
    "promotionCode": "SUMMER10"
  }'

# Expected Response (201):
{
  "bookingId": 12345,
  "bookingStatus": "CONFIRMED",
  "depositAmount": 2000000,
  "discountedPrice": 9000000,
  "checkInDate": "2026-08-15",
  "checkOutDate": "2026-08-20",
  "cancellationDeadline": "2026-08-13"
}
```

**[DELETE] Hủy booking**
```bash
# [DELETE] Hủy booking trước 48h
curl -X DELETE https://api.kawairesort.com/api/v1/bookings/12345 \
  -H "Authorization: Bearer [CUSTOMER_TOKEN]"

# Expected Response (200):
{
  "refundAmount": 2000000,
  "bookingStatus": "Cancelled_Refunded"
}
```

**Business Rules:**
* BR-DATE-01: checkOutDate > checkInDate
* BR-FO-01: Pessimistic Locking chống overbooking
* BR-FIN-02: Hủy trước 48h → hoàn 100%; trong 48h → tịch thu cọc
* BR-STATUS-01: Booking tạo mới → "CONFIRMED"
* BR-STATUS-02: Hủy trước 48h → "Cancelled_Refunded"; sau → "Cancelled_Forfeited"
* BR-ERR-01: Exception message chứa error code `[ERR_PROMO_XXX]`

**Implementation:**
* Interface: `BookingService.createBooking()`, `BookingService.cancelBooking()`
* Repository: `RoomBookingRepository.countOverlappingBookings()`, `PromotionRepository.findByPromoCode()`

---

### 7.3. Chống Over-Booking (DB Inspection)
Mô phỏng 2 threads gọi API `/api/bookings` cùng lúc:
```sql
-- Kiểm tra DB sau khi chạy JMeter Stress Test
SELECT room_id, COUNT(*) as booking_count 
FROM bookings 
WHERE check_in = '2026-06-15' AND status = 'CONFIRMED'
GROUP BY room_id HAVING booking_count > 1;
-- Expected: Empty result (Không có phòng nào bị book 2 lần cùng ngày)
```

### 7.2. Ghi nợ phòng (Post to Room) - API Call
```bash
# [POST] POS gửi yêu cầu ghi nợ vào phòng R202
curl -X POST https://api.kawairesort.com/api/v1/pos/post-to-room \
  -H "Authorization: Bearer [POS_STAFF_TOKEN]" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "R202",
    "amount": 1000000,
    "description": "Bữa tối nhà hàng Seafood",
    "posOrderId": "POS-999"
  }'

# Expected Response (201):
{
  "status": "SUCCESS",
  "folioId": "FOL-202-A",
  "addedAmount": 1000000,
  "newTotalBalance": 1000000
}
```

### 7.3. Tạo tài khoản nhân viên mới (Quản lý RBAC) - API Call
```bash
# [POST] Admin tạo tài khoản nhân viên (UC05.1 - TC-M1-015)
curl -X POST https://api.kawairesort.com/api/v1/admin/employees \
  -H "Authorization: Bearer [ADMIN_TOKEN]" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "staff_new",
    "password": "password123",
    "roleId": 2,
    "fullName": "Nguyen Van Staff",
    "gender": "MALE",
    "cccd": "012345678912",
    "phone": "0987654321",
    "email": "staff@example.com",
    "salary": 10000000
  }'

# Expected Response (201):
{
  "status": "SUCCESS",
  "employeeId": 1,
  "accountId": 1,
  "role": "ROLE_FB_STAFF"
}
```

---

## 8. API Specification — UC12: Check-in / Đổi phòng / Hạn mức (Module 2 — Front Desk)

### 8.1. Endpoints

| Method | Path | Auth Level | Required Roles | Description |
|--------|------|------------|----------------|-------------|
| POST | `/api/v1/frontdesk/check-in` | Authenticated | Receptionist | Check-in khách vào phòng (UC12.1) |
| PUT  | `/api/v1/frontdesk/booking-detail/{detailId}/credit-limit` | Authenticated | Receptionist | Cập nhật Credit Limit (UC12.2) |
| POST | `/api/v1/frontdesk/transfer-room` | Authenticated | Receptionist | Đổi phòng cho khách (UC12.3) |
| POST | `/api/v1/frontdesk/upgrade-dependent/{dependentId}` | Authenticated | Receptionist | Nâng cấp Dependent → Customer (UC12.4) |

### 8.2. POST Check-in — Request

```bash
curl -X POST https://api.kawairesort.com/api/v1/frontdesk/check-in \
  -H "Authorization: Bearer [RECEPTIONIST_TOKEN]" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingDetailId": 5000,
    "roomId": 100
  }'
```

### 8.3. POST Check-in — Response 200 OK (Happy Path)

```json
{
  "detailId": 5000,
  "detailStatus": "CHECKED_IN",
  "roomNumber": "R101",
  "roomStatus": "Occupied",
  "customerName": "Nguyen Van A",
  "checkInDate": "2026-06-15",
  "checkOutDate": "2026-06-20"
}
```

### 8.4. POST Check-in — Response 400 (Validation Error)

```json
{
  "status": "ERROR",
  "code": "ROOM-001",
  "message": "Phòng đang DIRTY, chưa được dọn dẹp. Không thể check-in. (BR-FO-04)"
}
```

### 8.5. PUT Credit Limit — Request

```bash
curl -X PUT https://api.kawairesort.com/api/v1/frontdesk/booking-detail/5000/credit-limit \
  -H "Authorization: Bearer [RECEPTIONIST_TOKEN]" \
  -H "Content-Type: application/json" \
  -d '{
    "newCreditLimit": 8000000
  }'
```

### 8.6. PUT Credit Limit — Response 200 OK

```json
{
  "status": "SUCCESS",
  "bookingDetailId": 5000,
  "newCreditLimit": 8000000
}
```

### 8.7. POST Đổi phòng — Request

```bash
curl -X POST https://api.kawairesort.com/api/v1/frontdesk/transfer-room \
  -H "Authorization: Bearer [RECEPTIONIST_TOKEN]" \
  -H "Content-Type: application/json" \
  -d '{
    "bookingDetailId": 5000,
    "newRoomId": 101,
    "reason": "Yêu cầu từ khách hàng"
  }'
```

### 8.8. POST Đổi phòng — Response 200 OK

```json
{
  "detailId": 5000,
  "detailStatus": "CHECKED_IN",
  "oldRoomNumber": "R101",
  "newRoomNumber": "R102",
  "oldRoomStatus": "Dirty",
  "newRoomStatus": "Occupied"
}
```

### 8.9. POST Nâng cấp Dependent — Response 201 Created

```bash
curl -X POST https://api.kawairesort.com/api/v1/frontdesk/upgrade-dependent/200 \
  -H "Authorization: Bearer [RECEPTIONIST_TOKEN]"
```

### 8.10. POST Nâng cấp Dependent — Response 201 Created

```json
{
  "status": "SUCCESS",
  "customerId": 50,
  "fullName": "Nguyen Thi B",
  "email": "pending_50@kawai-resort.com",
  "accountId": 50,
  "role": "CUSTOMER"
}
```

### 8.11. Business Rules

| Rule ID | Description | Implementation |
|---------|-------------|---------------|
| BR-FO-04 | Chỉ check-in phòng Vacant_Clean → Occupied; check-out → Dirty | `CheckinServiceImpl.validateRoomAvailableForCheckin()` |
| BR-FO-06 | Hạn mức chi tiêu phòng (Credit Limit) | `CheckinServiceImpl.updateCreditLimit()` |
| BR-FO-07 | Giới hạn số người (nâng cấp Dependent → Customer) | `CheckinServiceImpl.upgradeDependentToCustomer()` |
| BR-HK-03 | Chặn Check-in phòng MAINTENANCE | `CheckinServiceImpl.validateRoomAvailableForCheckin()` |
| BR-FO-08 | Khai báo tạm trú: thu thập CCCD/Hộ chiếu | `CheckinServiceImpl.checkIn()` |

### 8.12. Error Codes

| Code | HTTP Status | Message | Trigger Condition |
|------|-------------|---------|------------------|
| `ROOM-001` | 400 | Phòng đang DIRTY — không thể check-in (BR-FO-04) | Phòng chưa được dọn dẹp |
| `ROOM-001` | 400 | Phòng đang MAINTENANCE — không thể check-in (BR-HK-03) | Phòng đang bảo trì |
| `ROOM-001` | 400 | Phòng mới không khả dụng (trạng thái: ...) | Phòng đích không trống khi đổi phòng |
| `FOLIO-001` | 400 | Folio chưa thanh toán — không thể check-out | Chưa tất toán hóa đơn (BR-FIN-01) |

### 8.13. Authorization Matrix (bổ sung cho Module 2)

| Tác vụ / Endpoint | GUEST | CUSTOMER | RECEPTIONIST | F&B STAFF | ADMIN / MANAGER |
| --- | --- | --- | --- | --- | --- |
| Check-in (`POST /frontdesk/check-in`) | ❌ | ❌ | ✅ | ❌ | ✅ |
| Update Credit Limit (`PUT /frontdesk/.../credit-limit`) | ❌ | ❌ | ✅ | ❌ | ✅ |
| Transfer Room (`POST /frontdesk/transfer-room`) | ❌ | ❌ | ✅ | ❌ | ✅ |
| Upgrade Dependent (`POST /frontdesk/upgrade-dependent`) | ❌ | ❌ | ✅ | ❌ | ✅ |

---

## 9. API Specification — UC13: Quản lý sơ đồ phòng vật lý (Module 2 — Front Desk & Housekeeping)

### 9.1. Endpoints

| Method | Path | Auth Level | Required Roles | Description |
|--------|------|------------|----------------|-------------|
| GET  | `/api/v1/housekeeping/pending` | Authenticated | Receptionist, HK_Staff | Lấy danh sách yêu cầu dọn phòng/bảo trì đang chờ xử lý (UC13.3) |
| POST | `/api/v1/housekeeping/tasks/{taskId}/complete-clean` | Authenticated | HK_Staff | HK đánh dấu phòng đã dọn sạch, phòng chuyển Vacant_Clean (UC13.2) |
| POST | `/api/v1/housekeeping/maintenance` | Authenticated | HK_Staff | HK báo hỏng thiết bị, phòng chuyển Maintenance (UC13.4) |
| POST | `/api/v1/housekeeping/maintenance/{taskId}/complete` | Authenticated | Maintenance_Staff | Hoàn tất bảo trì, phòng chuyển Vacant_Clean (UC13.5) |

*Lưu ý: Tác vụ tự động sinh yêu cầu dọn phòng (UC13.1) được trigger ngầm qua backend event khi khách Check-out, không gọi qua REST API riêng.*

### 9.2. GET Pending Tasks — Response 200 OK

```json
{
  "status": "SUCCESS",
  "data": [
    {
      "taskId": 100,
      "roomId": 1,
      "roomNumber": "R101",
      "operationalType": "CHECKOUT_CLEAN",
      "priority": "High",
      "status": "Pending",
      "createdAt": "2026-06-15T10:00:00"
    },
    {
      "taskId": 200,
      "roomId": 2,
      "roomNumber": "R102",
      "operationalType": "MAINTENANCE",
      "priority": "Normal",
      "status": "Pending",
      "notes": "Điều hòa không mát",
      "createdAt": "2026-06-15T11:30:00"
    }
  ]
}
```

### 9.3. POST Báo hỏng thiết bị — Request

```bash
curl -X POST https://api.kawairesort.com/api/v1/housekeeping/maintenance \
  -H "Authorization: Bearer [HK_STAFF_TOKEN]" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 102,
    "staffId": 25,
    "notes": "Điều hòa không mát"
  }'
```

### 9.4. POST Báo hỏng thiết bị — Response 201 Created

```json
{
  "taskId": 200,
  "roomNumber": "R102",
  "roomStatus": "Maintenance",
  "operationalType": "MAINTENANCE",
  "priority": "Normal",
  "status": "Pending",
  "notes": "Điều hòa không mát"
}
```

### 9.5. Business Rules

| Rule ID | Description | Implementation |
|---------|-------------|---------------|
| BR-FO-04 | Luân chuyển trạng thái phòng sang Vacant_Clean sau khi dọn / sửa xong | `HousekeepingServiceImpl.updateRoomToClean()`, `completeMaintenance()` |
| BR-HK-02 | Báo cáo chi tiết sự cố khi tạo phiếu bảo trì | `HousekeepingServiceImpl.createMaintenanceRequest()` |
| BR-HK-03 | Phòng có phiếu bảo trì bị khóa thành trạng thái MAINTENANCE | `HousekeepingServiceImpl.createMaintenanceRequest()` |

### 9.6. Error Codes

| Code | HTTP Status | Message | Trigger Condition |
|------|-------------|---------|------------------|
| `HK-001` | 404 | Không tìm thấy Task với ID cung cấp | taskId không tồn tại |
| `HK-002` | 400 | Phòng không ở trạng thái hợp lệ | Ví dụ đánh dấu hoàn thành dọn nhưng phòng không DIRTY |

---

**BẢNG TỔNG HỢP PHÂN QUYỀN (Authorization Matrix)**
| Tác vụ / Endpoint | GUEST | CUSTOMER | RECEPTIONIST | F&B STAFF | HK/MAINT STAFF | ADMIN / MANAGER |
| --- | --- | --- | --- | --- | --- | --- |
| Đặt phòng (`POST /bookings`) | ❌ | ✅ | ✅ | ❌ | ❌ | ✅ |
| Post to Room (`POST /pos/charge`) | ❌ | ❌ | ❌ | ✅ | ❌ | ✅ |
| Night Audit (`POST /audit/run`) | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Check-in (`POST /frontdesk/check-in`) | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ |
| Transfer Room | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ |
| Get Pending Housekeeping Tasks | ❌ | ❌ | ✅ | ❌ | ✅ | ✅ |
| Update Room Clean/Maintenance | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |

