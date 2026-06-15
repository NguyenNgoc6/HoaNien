# TEST-DRIVEN DEVELOPMENT SPECIFICATION FOR UC-12

## Đặc tả Kiểm thử Hướng Phát triển - Xem Sơ đồ Ma trận Phòng & Khách Dự kiến

| Field                    | Value                                                                    |
| ------------------------ | ------------------------------------------------------------------------ |
| **Document ID**    | `KAWAI-TDD-MOD2-012`                                                   |
| **Version**        | 1.1                                                                      |
| **Date**           | 2026-06-15                                                               |
| **Status**         | Approved                                                                 |
| **Standard**       | ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation |
| **Author**         | `Chu Xuân Dũng – Developer`                                         |
| **Reviewed by**    | `Nguyễn Xuân Lưu – Tech Lead`                                      |
| **DPO Sign-off**   | `[ ] Pending`                                                          |
| **Approved by**    | `[ ] Pending`                                                          |
| **Classification** | Internal – Confidential                                                 |

---

### References:

* `04_testing/mod2_booking/EDS_MOD2_UC12.md` — Đặc tả kỹ thuật UC-12
* `04_testing/mod2_booking/SRS_Document_SWP391_G2.docx.md` — Tài liệu đặc tả yêu cầu UC-12 (§2.1.12)
* `RoomServiceImpl.getRoomDashboard()` — Implementation hiện tại
* `ReceptionistController.dashboard()` — Controller hiện tại (Web Thymeleaf)
* `RoomDashboardDTO.java` — DTO hiển thị Front Desk Dashboard

> [!NOTE]
> **Quy ước TDD:** Thiết lập các kịch bản test trước khi hoàn thiện logic nghiệp vụ để đảm bảo độ bao phủ của code và ngăn ngừa lỗi logic.

---

### CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày      | Người thực hiện | Nội dung thay đổi                                                      |
| ---------- | ------------------- | ------------------------------------------------------------------------- |
| 2026-06-15 | `Chu Xuân Dũng` | Mở rộng bộ test condition từ 9 lên 13 — bổ sung Floor Filter, Last Sync Timestamp, Data Sync Error, Dashboard Refresh, Booked Rooms Count |
| 2026-06-15 | `Chu Xuân Dũng` | Khởi tạo tài liệu — TDD spec cho UC-12 View Expected Arrivals Matrix |


---

### MỤC LỤC

1. [Thông tin Module](#1-thong-tin-module)
2. [Logic Issues Resolved](#2-logic-issues-resolved)
3. [Test Design Specification (TDS)](#3-test-design-specification-tds)
4. [Test Case Specification](#4-test-case-specification)
5. [Red-Green-Refactor Tracker](#5-red-green-refactor-tracker)
6. [Entry / Exit Criteria](#6-entry--exit-criteria)
7. [Rollback Plan](#7-rollback-plan)

---

### 1. Thông tin Module

| Field                           | Value                                                |
| ------------------------------- | ---------------------------------------------------- |
| **Feature / Gap ID**      | `GAP-MOD2-UC12`                                    |
| **Module**                | `Đặt phòng & Tiền sảnh vận hành (Module 2)` |
| **Spec gốc**             | `SRS_Document_SWP391_G2.docx.md` §2.1.12          |
| **Priority**              | 🔴 Critical                                          |
| **Sprint**                | `S1`                                               |
| **Milestone**             | M3 Alpha — 2026-07-11                               |
| **Data Classification**   | Internal                                             |
| **Compliance Scope**      | Luật du lịch Việt Nam 2017                        |
| **Upstream Dependencies** | `UC-10 (Book Room), UC-13 (Check-In)`              |
| **Downstream Consumers**  | `Receptionist Front Desk Dashboard`                |

---

### 2. Logic Issues Resolved

| #            | Spec gốc                                                              | Thực tế                                                                                                        | Fix áp dụng trong test                                                                                |
| ------------ | ---------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------- |
| **L1** | Hiển thị "expected guest arrivals" nhưng chưa rõ nguồn dữ liệu | Booking với trạng thái CONFIRMED và checkInDate = today chính là danh sách khách dự kiến đến         | Test kiểm tra `getExpectedArrivals()` trả về danh sách booking CONFIRMED có checkInDate hôm nay |
| **L2** | Room status "Vacant_Dirty" trong code là "Dirty" hoặc "Vacant_Dirty" | Cần chuẩn hóa status enum:`Vacant_Clean`, `Occupied`, `Vacant_Dirty`, `Maintenance`, `OutOfService` | Test kiểm tra đúng 5 trạng thái hiển thị, dùng đúng enum name từ DB                          |
| **L3** | AF-01 yêu cầu filter theo floor, room type, room status              | `RoomDashboardDTO` hiện tại chưa có field floor. `Room` entity cần bổ sung trường `floor` (String) | Test filter theo floor sử dụng roomNumber prefix (R1xx = tầng 1, R2xx = tầng 2)                   |
| **L4** | AF-02 yêu cầu hiển thị last sync timestamp khi dữ liệu đồng bộ bị trễ | Chưa có cơ chế lưu trạng thái đồng bộ cuối cùng                                               | Test `lastSyncTimestamp` hiển thị đúng định dạng ISO 8601 khi controller trả về model attribute |
| **L5** | E1: Data sync error → "Unable to refresh data"                       | `ReceptionistController` hiện catch Exception silently (empty catch block)                       | Test fallback hiển thị warning message khi repository ném RuntimeException                     |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi

Kiểm thử logic hiển thị sơ đồ ma trận phòng và khách dự kiến đến (UC-12) thuộc component `RoomService.getRoomDashboard()` và `ReceptionistController.dashboard()`.

#### TDS-02 — Test Basis / Cơ sở Kiểm thử

- `SRS_Document_SWP391_G2.docx.md` UC-12 View Expected Arrivals Matrix.
- BR-FO-04: Room status lifecycle (Vacant_Clean → Occupied → Vacant_Dirty → Vacant_Clean).
- BR-FO-05: Rooms flagged as Rush Room prioritized in housekeeping queue.
- BR-FIN-05: Giá phòng làm tròn BigDecimal HALF_UP, scale 0.

#### TDS-03 — Test Conditions and Coverage Items

| Condition ID          | Test Condition                                                      | Coverage Item                           | Test Cases         |
| --------------------- | ------------------------------------------------------------------- | --------------------------------------- | ------------------ |
| **TC-COND-001** | Lấy toàn bộ dashboard room matrix                             | `RoomService.getRoomDashboard()`      | `TC-M2-UC12-001` |
| **TC-COND-002** | Dashboard rỗng khi không có phòng                           | `RoomService.getRoomDashboard()`      | `TC-M2-UC12-002` |
| **TC-COND-003** | Occupied room hiển thị tên khách                           | `RoomService.getRoomDashboard()`      | `TC-M2-UC12-003` |
| **TC-COND-004** | Filter theo room status                                       | `RoomService.getRoomDashboard()`      | `TC-M2-UC12-004` |
| **TC-COND-005** | Filter theo room type                                         | `RoomService.getRoomDashboard()`      | `TC-M2-UC12-005` |
| **TC-COND-006** | Tính KPI dashboard                                            | `RoomService.getRoomDashboard()`      | `TC-M2-UC12-006` |
| **TC-COND-007** | Expected arrivals hôm nay                                     | `RoomService.getExpectedArrivals()`   | `TC-M2-UC12-007` |
| **TC-COND-008** | Expected departures hôm nay                                   | `RoomService.getExpectedDepartures()` | `TC-M2-UC12-008` |
| **TC-COND-009** | Filter theo floor                                             | `RoomService.getRoomDashboard()`      | `TC-M2-UC12-009` |
| **TC-COND-010** | Hiển thị last sync timestamp                                 | `RoomService.getRoomDashboard()`      | `TC-M2-UC12-010` |
| **TC-COND-011** | Data synchronization error                                    | `ReceptionistController.dashboard()`  | `TC-M2-UC12-011` |
| **TC-COND-012** | Refresh dashboard sau khi dữ liệu thay đổi              | `RoomService.getRoomDashboard()`      | `TC-M2-UC12-012` |
| **TC-COND-013** | Booked rooms count hiển thị đúng                          | `RoomService.getRoomDashboard()`      | `TC-M2-UC12-013` |

#### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử

- **Equivalence Partitioning (EP)**: Phân nhóm trạng thái phòng (Vacant_Clean, Occupied, Vacant_Dirty, Maintenance, OutOfService).
- **Boundary Value Analysis (BVA)**: Kiểm tra biên của BigDecimal scale. Kiểm tra biên thời gian sync (quá hạn / chưa quá hạn).
- **State Transition Testing**: Kiểm tra dashboard refresh sau khi trạng thái phòng thay đổi (e.g., Vacant_Clean → Occupied sau check-in).
- **Error Guessing**: Kiểm tra fallback khi repository throw RuntimeException (data sync error).

#### TDS-05 — Test Data Requirements

- **FX-001**: R101 - Deluxe - 2,000,000 VND - capacity 2 - Vacant_Clean, floor = "1" (tầng 1).
- **FX-002**: R201 - Suite - 3,500,000 VND - capacity 4 - Occupied (có khách "Nguyen Van A"), floor = "2" (tầng 2).
- **FX-003**: R301 - Deluxe - 2,000,000 VND - capacity 2 - Vacant_Dirty, floor = "3" (tầng 3).
- **FX-004**: R102 - Deluxe - 2,000,000 VND - capacity 2 - Maintenance, floor = "1" (tầng 1).
- **FX-005**: Booking CONFIRMED với checkInDate = today, customer = "Tran Thi B".
- **FX-006**: Booking CHECKED_IN với checkOutDate = today, customer = "Pham Van D", room = R105.
- **FX-007**: Timestamp mẫu `2026-06-15T08:30:00+07:00` — last successful sync time.

---

### 4. Test Case Specification

#### `TC-M2-UC12-001` — Lấy toàn bộ dashboard room matrix

* **Severity:** `CRITICAL`
* **Feature Under Test:** `RoomServiceImpl.getRoomDashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-001`

**Preconditions:**
- Hệ thống có 4 phòng: R101 (Vacant_Clean), R201 (Occupied), R301 (Vacant_Dirty), R102 (Maintenance).

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về danh sách 4 phòng.
2. **Act**: Gọi `roomService.getRoomDashboard()`.
3. **Assert**: Kiểm tra kết quả trả về có size = 4, chứa đủ các roomId tương ứng, giá phòng format BigDecimal scale 0 HALF_UP.

**Expected Result (PASS):**
- Danh sách trả về không null và chứa đúng 4 phòng. Giá phòng đúng định dạng.

**Expected Result (FAIL):**
- Trả về null, sai số lượng phòng, hoặc giá phòng sai scale.

**Current Status:** 🔴 Not written
**Implementation Note:** Sử dụng `toDashboardDTO()` helper method để map Room → RoomDashboardDTO.

---

#### `TC-M2-UC12-002` — Dashboard rỗng khi không có phòng

* **Severity:** `MEDIUM`
* **Feature Under Test:** `RoomServiceImpl.getRoomDashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-002`

**Preconditions:**
- Hệ thống không có phòng nào trong DB.

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về danh sách trống.
2. **Act**: Gọi `roomService.getRoomDashboard()`.
3. **Assert**: Kiểm tra kết quả trả về không null và rỗng (isEmpty = true).

**Expected Result (PASS):**
- Trả về danh sách rỗng.

**Expected Result (FAIL):**
- Trả về null hoặc quăng lỗi NullPointerException.

**Current Status:** 🔴 Not written

---

#### `TC-M2-UC12-003` — Occupied room hiển thị tên khách

* **Severity:** `HIGH`
* **Feature Under Test:** `RoomServiceImpl.getRoomDashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-003`

**Preconditions:**
- Phòng R201 đang Occupied với khách "Nguyen Van A". Phòng R101 đang Vacant_Clean (không có khách).

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về R101 và R201. R201 có RoomBookingDetail với customerName "Nguyen Van A".
2. **Act**: Gọi `roomService.getRoomDashboard()`.
3. **Assert**: Kiểm tra kết quả: R201 có `currentGuestName = "Nguyen Van A"`, R101 có `currentGuestName = null`.

**Expected Result (PASS):**
- Phòng Occupied hiển thị đúng tên khách; phòng không Occupied có currentGuestName = null.

**Expected Result (FAIL):**
- Phòng Occupied không có tên khách, hoặc phòng Vacant hiển thị nhầm tên khách.

**Current Status:** 🔴 Not written
**Implementation Note:** Cần query RoomBookingDetail (detailStatus = CHECKED_IN) để lấy customerName.

---

#### `TC-M2-UC12-004` — Filter theo room status

* **Severity:** `MEDIUM`
* **Feature Under Test:** `RoomServiceImpl.getRoomDashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-004`

**Preconditions:**
- Hệ thống có 4 phòng: R101 (Vacant_Clean), R201 (Occupied), R301 (Vacant_Dirty), R102 (Maintenance).

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về 4 phòng. Yêu cầu lọc `roomStatus = "Vacant_Clean"`.
2. **Act**: Gọi `roomService.getRoomDashboard()` với filter roomStatus.
3. **Assert**: Kiểm tra kết quả chỉ chứa R101.

**Expected Result (PASS):**
- Trả về danh sách chỉ chứa phòng có trạng thái "Vacant_Clean".

**Expected Result (FAIL):**
- Trả về phòng không đúng trạng thái yêu cầu.

**Current Status:** 🔴 Not written
**Implementation Note:** Thêm optional parameter `roomStatus` vào method hoặc lọc post-query bằng Java Stream.

---

#### `TC-M2-UC12-005` — Filter theo room type

* **Severity:** `LOW`
* **Feature Under Test:** `RoomServiceImpl.getRoomDashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-005`

**Preconditions:**
- Hệ thống có R101 (Deluxe), R201 (Suite), R301 (Deluxe).

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về 3 phòng. Yêu cầu lọc `categoryName = "Deluxe"`.
2. **Act**: Gọi `roomService.getRoomDashboard()` với filter categoryName.
3. **Assert**: Kiểm tra kết quả chỉ chứa R101 và R301.

**Expected Result (PASS):**
- Trả về danh sách chỉ chứa phòng hạng Deluxe.

**Expected Result (FAIL):**
- Trả về cả Suite hoặc thiếu phòng Deluxe.

**Current Status:** 🔴 Not written

---

#### `TC-M2-UC12-006` — Tính KPI dashboard

* **Severity:** `HIGH`
* **Feature Under Test:** `RoomServiceImpl.getRoomDashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-006`

**Preconditions:**
- Hệ thống có 5 phòng: 2 Vacant_Clean, 1 Occupied, 1 Vacant_Dirty, 1 Maintenance.

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về 5 phòng.
2. **Act**: Gọi `roomService.getRoomDashboard()`.
3. **Assert**: Kiểm tra KPI: totalRooms = 5, occupied = 1, dirty = 1, available (Vacant_Clean) = 2.

**Expected Result (PASS):**
- Các chỉ số KPI được tính toán chính xác.

**Expected Result (FAIL):**
- Sai số lượng ở bất kỳ KPI nào.

**Current Status:** 🔴 Not written
**Implementation Note:** Có thể tạo DTO riêng `RoomDashboardKpiDTO` hoặc trả về kèm trong response.

---

#### `TC-M2-UC12-007` — Expected arrivals hôm nay

* **Severity:** `HIGH`
* **Feature Under Test:** `RoomServiceImpl.getExpectedArrivals()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-007`

**Preconditions:**
- Có 2 booking CONFIRMED: BK-1001 (checkInDate = today, customer = "Tran Thi B"), BK-1002 (checkInDate = tomorrow, customer = "Le Van C").

**Test Steps:**
1. **Arrange**: Mock `roomBookingRepository` trả về danh sách booking có checkInDate = today.
2. **Act**: Gọi `roomService.getExpectedArrivals()`.
3. **Assert**: Kiểm tra kết quả chỉ chứa BK-1001 của "Tran Thi B". Không chứa BK-1002.

**Expected Result (PASS):**
- Trả về danh sách khách dự kiến đến đúng ngày hôm nay.

**Expected Result (FAIL):**
- Trả về booking ngày mai hoặc thiếu booking hôm nay.

**Current Status:** 🔴 Not written
**Implementation Note:** Query `RoomBookingRepository` với `bookingStatus = CONFIRMED` và `checkInDate = today`.

---

#### `TC-M2-UC12-008` — Expected departures hôm nay

* **Severity:** `MEDIUM`
* **Feature Under Test:** `RoomServiceImpl.getExpectedDepartures()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-008`

**Preconditions:**
- Có 2 booking CHECKED_IN: BK-1001 (checkOutDate = today), BK-1002 (checkOutDate = tomorrow).

**Test Steps:**
1. **Arrange**: Mock `roomBookingRepository` trả về danh sách booking có checkOutDate = today.
2. **Act**: Gọi `roomService.getExpectedDepartures()`.
3. **Assert**: Kiểm tra kết quả chỉ chứa BK-1001. Không chứa BK-1002.

**Expected Result (PASS):**
- Trả về danh sách khách dự kiến check-out đúng ngày hôm nay.

**Expected Result (FAIL):**
- Trả về booking ngày mai hoặc thiếu booking hôm nay.

**Current Status:** 🔴 Not written

---

#### `TC-M2-UC12-009` — Filter theo floor

* **Severity:** `LOW`
* **Feature Under Test:** `RoomServiceImpl.getRoomDashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-009`

**Preconditions:**
- Hệ thống có 4 phòng: R101 (tầng 1), R102 (tầng 1), R201 (tầng 2), R301 (tầng 3).

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về 4 phòng. Yêu cầu lọc `floor = "1"`.
2. **Act**: Gọi `roomService.getRoomDashboard()` với filter floor.
3. **Assert**: Kiểm tra kết quả chỉ chứa R101 và R102 (cùng tầng 1).

**Expected Result (PASS):**
- Trả về danh sách chỉ chứa phòng ở tầng được chỉ định.

**Expected Result (FAIL):**
- Trả về phòng không đúng tầng hoặc thiếu phòng.

**Current Status:** 🔴 Not written
**Implementation Note:** Cần bổ sung trường `floor` vào `Room` entity và `RoomDashboardDTO`. Logic filter: parse prefix roomNumber (R1xx → tầng 1).

---

#### `TC-M2-UC12-010` — Hiển thị last sync timestamp

* **Severity:** `MEDIUM`
* **Feature Under Test:** `RoomServiceImpl.getRoomDashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-010`

**Preconditions:**
- Hệ thống có dữ liệu phòng. Lần đồng bộ cuối cùng thành công lúc `2026-06-15T08:30:00+07:00`.

**Test Steps:**
1. **Arrange**: Mock repository trả về danh sách phòng. Service lưu `lastSyncTimestamp = Instant.now()`.
2. **Act**: Gọi `roomService.getRoomDashboard()`.
3. **Assert**: Kiểm tra response chứa `lastSyncTimestamp` đúng định dạng ISO 8601, không null.

**Expected Result (PASS):**
- `lastSyncTimestamp` hiển thị đúng thời điểm đồng bộ cuối cùng, định dạng ISO 8601 (AF-02).

**Expected Result (FAIL):**
- Thiếu timestamp, timestamp null, hoặc sai định dạng.

**Current Status:** 🔴 Not written
**Implementation Note:** Controller gán `model.addAttribute("lastSyncTimestamp", ...)` với giá trị `LocalDateTime.now()` khi load thành công.

---

#### `TC-M2-UC12-011` — Data synchronization error

* **Severity:** `HIGH`
* **Feature Under Test:** `ReceptionistController.dashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-011`

**Preconditions:**
- `RoomRepository.findAll()` ném `RuntimeException` (giả lập lỗi kết nối DB).

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` throw `RuntimeException("Connection timeout")`.
2. **Act**: Gọi `receptionistController.dashboard(model)`.
3. **Assert**: Kiểm tra model chứa attribute `syncError = true` hoặc `warningMessage = "Unable to refresh data, please contact IT support."` (E1). Dashboard vẫn render được với dữ liệu fallback (KPI = 0, rooms = []).

**Expected Result (PASS):**
- Hiển thị warning message "Unable to refresh data..." khi lỗi sync. Không crash toàn bộ trang.

**Expected Result (FAIL):**
- Trang crash với lỗi 500, hoặc không hiển thị thông báo lỗi.

**Current Status:** 🔴 Not written
**Implementation Note:** Bọc `try-catch` trong controller, set `model.addAttribute("syncError", true)` khi catch exception. Hiện tại controller đã có try-catch rỗng — cần bổ sung model attribute.

---

#### `TC-M2-UC12-012` — Refresh dashboard sau khi dữ liệu thay đổi

* **Severity:** `MEDIUM`
* **Feature Under Test:** `RoomServiceImpl.getRoomDashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-012`

**Preconditions:**
- Trước khi refresh: phòng R101 đang Vacant_Clean.
- Sau check-in: R101 chuyển sang Occupied (có khách "Le Thi C").

**Test Steps:**
1. **Arrange (lần 1)**: Mock `roomRepository.findAll()` trả về R101 (Vacant_Clean).
2. **Act (lần 1)**: Gọi `roomService.getRoomDashboard()` → kết quả: R101 Vacant_Clean.
3. **Arrange (lần 2)**: Mock `roomRepository.findAll()` trả về R101 (Occupied, guest = "Le Thi C").
4. **Act (lần 2)**: Gọi `roomService.getRoomDashboard()` → kết quả: R101 Occupied, currentGuestName = "Le Thi C".

**Expected Result (PASS):**
- Sau khi refresh, dashboard phản ánh đúng trạng thái phòng mới nhất từ DB.

**Expected Result (FAIL):**
- Dashboard không cập nhật, vẫn hiển thị trạng thái cũ.

**Current Status:** 🔴 Not written
**Implementation Note:** Test này xác nhận `getRoomDashboard()` luôn query DB mới (không cache stale data).

---

#### `TC-M2-UC12-013` — Booked rooms count hiển thị đúng

* **Severity:** `HIGH`
* **Feature Under Test:** `RoomServiceImpl.getRoomDashboard()`
* **Test File:** `src/test/java/com/kawai/services/RoomServiceUC12Test.java`
* **TDD Phase:** 🔴 RED
* **Condition Ref:** `TC-COND-013`

**Preconditions:**
- Có 3 booking CONFIRMED với checkInDate = today. 1 booking CANCELLED.

**Test Steps:**
1. **Arrange**: Mock `bookingRepository.countConfirmed()` trả về `3L`. Mock `roomBookingRepository.findCheckOutsBetween(today, today)` trả về 2 booking.
2. **Act**: Gọi `roomService.getRoomDashboard()`.
3. **Assert**: Kiểm tra `pendingCheckIns` = 3, `checkoutsToday` = 2. Các booking CANCELLED không được tính.

**Expected Result (PASS):**
- Booked rooms count (`pendingCheckIns`) chỉ đếm booking CONFIRMED, không bao gồm CANCELLED.

**Expected Result (FAIL):**
- Đếm sai số lượng, bao gồm cả booking CANCELLED, hoặc thiếu booking CONFIRMED.

**Current Status:** 🔴 Not written
**Implementation Note:** Sử dụng `bookingRepository.countConfirmed()` để đếm booking CONFIRMED. Phân biệt với `totalRooms` (tổng phòng vật lý).

---

### 5. Red-Green-Refactor Tracker

| TC ID              | Test File                    | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note                    |
| ------------------ | ---------------------------- | :--------------: | ----------------- | ----------------------------------- |
| `TC-M2-UC12-001` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Lấy toàn bộ dashboard room matrix  |
| `TC-M2-UC12-002` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Dashboard rỗng khi không có phòng |
| `TC-M2-UC12-003` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Occupied room hiển thị tên khách |
| `TC-M2-UC12-004` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Filter theo room status             |
| `TC-M2-UC12-005` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Filter theo room type               |
| `TC-M2-UC12-006` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Tính KPI dashboard                  |
| `TC-M2-UC12-007` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Expected arrivals hôm nay           |
| `TC-M2-UC12-008` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Expected departures hôm nay         |
| `TC-M2-UC12-009` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Filter theo floor                   |
| `TC-M2-UC12-010` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Hiển thị last sync timestamp       |
| `TC-M2-UC12-011` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Data synchronization error          |
| `TC-M2-UC12-012` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Refresh dashboard                   |
| `TC-M2-UC12-013` | `RoomServiceUC12Test.java` |     `[ ]`     | `[ ]`           | Booked rooms count hiển thị đúng  |

---

### 6. Entry / Exit Criteria

#### Entry Criteria

- [X] Spec UC-12 từ SRS đã rõ ràng.
- [X] `RoomDashboardDTO` đã tồn tại.
- [X] `RoomService.getRoomDashboard()` đã có implementation cơ bản.
- [X] `ReceptionistController.dashboard()` đã có endpoint Web.
- [ ] File JUnit `RoomServiceUC12Test.java` đã sẵn sàng.

#### Exit Criteria

- [ ] 100% test cases của UC-12 chạy PASS (13/13).
- [ ] `getExpectedArrivals()` trả về danh sách khách CONFIRMED hôm nay.
- [ ] `getExpectedDepartures()` trả về danh sách khách check-out hôm nay.
- [ ] KPI (totalRooms, occupied, dirty, available) được tính chính xác.
- [ ] Filter theo roomStatus, categoryName, và floor hoạt động đúng.
- [ ] `lastSyncTimestamp` hiển thị khi load thành công.
- [ ] Warning message hiển thị khi data sync error.
- [ ] Dashboard refresh phản ánh đúng thay đổi trạng thái phòng.
- [ ] `pendingCheckIns` chỉ đếm booking CONFIRMED (không bao gồm CANCELLED).

---

### 7. Rollback Plan

**Revert implementation files:**
```bash
git checkout -- 03_sourcecode/kawai-backend/src/main/java/com/kawai/services/impl/RoomServiceImpl.java
git checkout -- 03_sourcecode/kawai-backend/src/main/java/com/kawai/controllers/web/ReceptionistController.java
git checkout -- 03_sourcecode/kawai-backend/src/main/java/com/kawai/dto/RoomDashboardDTO.java
```
