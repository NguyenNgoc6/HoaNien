# TEST-DRIVEN DEVELOPMENT SPECIFICATION FOR UC-10

## Đặc tả Kiểm thử Hướng Phát triển - UC-10: Đặt phòng & Thanh toán cọc trực tuyến

| Field | Value |
| --- | --- |
| **Document ID** | `KAWAI-TDD-MOD2-010` |
| **Version** | 1.0 |
| **Date** | 2026-06-14 |
| **Status** | Approved |
| **Standard** | ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation |
| **Author** | `Chu Xuân Dũng – Developer` |
| **Reviewed by** | `Nguyễn Xuân Lưu – Tech Lead` |
| **DPO Sign-off** | `[x] Approved – 2026-06-14 – Nguyễn Xuân Lưu` |
| **Approved by** | `Nguyễn Xuân Lưu` |
| **Classification** | Internal – Confidential |

---

### References:
*   `04_testing/mod2_booking/EDS_MOD2_UC10.md` — Đặc tả kỹ thuật UC-10
*   `04_testing/mod2_booking/SRS_Document_SWP391_G2.docx.md` — Tài liệu đặc tả yêu cầu UC-10
*   [BookingServiceUC10Test.java](file:///d:/SWP_Group2/su26-swp391-se2023-g2/03_sourcecode/kawai-backend/src/test/java/com/kawai/services/BookingServiceUC10Test.java) — File JUnit Test thực tế

> [!NOTE]
> **Quy ước TDD:** Thiết lập các kịch bản test trước khi hoàn thiện logic nghiệp vụ để đảm bảo độ bao phủ của code và ngăn ngừa lỗi logic.

---

### CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| --- | --- | --- |
| 2026-06-14 | `Chu Xuân Dũng` | Khởi tạo tài liệu — TDD spec riêng biệt cho UC-10 |
| 2026-06-15 | `Chu Xuân Dũng` | Rà soát lại các luồng logic ngoại lệ cho Use Case 10 |
| 2026-06-15 | `AI Agent` | Chạy testcase và update trạng thái GREEN (Pass) |

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

| Field | Value |
| --- | --- |
| **Feature / Gap ID** | `GAP-MOD2-UC10` |
| **Module** | `Đặt phòng (Booking & Front Desk)` |
| **Spec gốc** | `SRS_Document_SWP391_G2.docx.md` |
| **Priority** | 🔴 Critical |
| **Sprint** | `S1` |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Confidential & PII |
| **Compliance Scope** | Luật du lịch Việt Nam 2017 & Luật An ninh mạng Việt Nam |
| **Upstream Dependencies** | `UC-9 Tìm phòng trống` |
| **Downstream Consumers** | `Payment Gateway (VNPay), USALI Financial Decomposer` |

---

### 2. Logic Issues Resolved

| # | Spec gốc | Thực tế | Fix áp dụng trong test |
| --- | --- | --- | --- |
| **L1** | Cho phép truyền giá trị cọc bất kỳ từ client | Có nguy cơ bypass hoặc sửa đổi giá trị tiền cọc từ Front-end | Tiền cọc phải được tính toán lại từ phía Back-end và lưu trữ độc lập để đối chiếu với VNPay |
| **L2** | Mã lỗi voucher dạng chung chung | Cần phân biệt rõ ràng lý do lỗi voucher để Client hiển thị thông báo lỗi chi tiết cho khách hàng | Exception message phải chứa mã lỗi định dạng `[ERR_PROMO_XXX]` |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi
Kiểm thử logic đặt phòng thời gian thực, chống trùng lịch, áp dụng voucher giảm giá, xử lý callback từ cổng thanh toán VNPay và tự động hủy đơn do quá hạn cọc thuộc component `BookingService`.

#### TDS-02 — Test Basis / Cơ sở Kiểm thử
- `SRS_Document_SWP391_G2.docx.md` UC-10 Book Room.
- BR-FO-01: Chỉ đặt phòng khi phòng trống (countOverlappingBookings == 0).
- BR-FO-02: Tự động hủy nếu không thanh toán trong 10 phút.
- BR-DATE-01: checkOutDate > checkInDate bắt buộc.
- BR-FIN-01: Áp dụng voucher phải thỏa mãn `isActive=true` và `validTo >= today`.
- BR-ERR-01: Exception message lỗi voucher phải chứa error code dạng `[ERR_PROMO_XXX]`.

#### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| --- | --- | --- | --- |
| **TC-COND-001** | Đặt phòng thành công | `BookingService.createBooking` | `TC-M2-003` |
| **TC-COND-002** | Trùng lịch đặt phòng đồng thời | `BookingService.createBooking` | `TC-M2-004` |
| **TC-COND-003** | Validate ngày check-in/check-out | `BookingService.createBooking` | `TC-M2-005`, `TC-M2-005b` |
| **TC-COND-004** | Đặt nhiều phòng cùng lúc | `BookingService.createBooking` | `TC-M2-006` |
| **TC-COND-005** | Hết phòng trống khả dụng | `BookingService.createBooking` | `TC-M2-007` |
| **TC-COND-006** | Áp dụng mã giảm giá hợp lệ | `BookingService.createBooking` | `TC-M2-008`, `TC-M2-008b` |
| **TC-COND-007** | Nhập mã voucher lỗi | `BookingService.createBooking` | `TC-M2-009a`, `TC-M2-009b`, `TC-M2-009c` |
| **TC-COND-008** | Tính toán tiền đặt cọc | `BookingService.createBooking` | `TC-M2-010` |
| **TC-COND-009** | Giao dịch cọc chưa callback | `BookingApiController.createBooking` | `TC-M2-011` |
| **TC-COND-010** | Cổng thanh toán callback thành công | `BookingService` callback IPN | `TC-M2-012` |
| **TC-COND-011** | Cổng thanh toán callback thất bại | `BookingService` callback IPN | `TC-M2-013` |
| **TC-COND-012** | Quá hạn 10 phút không trả tiền cọc | `Pending_Booking_Auto_Cancellation` job | `TC-M2-014` |
| **TC-COND-013** | Xác nhận đặt phòng gửi email | `NotificationService.sendBookingConfirmation` | `TC-M2-015` |

#### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử
- **Boundary Value Analysis (BVA)**: Kiểm tra biên của thời gian đặt phòng và biên thời gian thanh toán (10 phút).
- **Equivalence Partitioning (EP)**: Nhóm các trạng thái đơn hàng (`PENDING`, `CONFIRMED`, `CANCELLED`).
- **State Transition Testing**: Kiểm tra vòng đời trạng thái đơn hàng khi nhận callback IPN.

#### TDS-05 — Test Data Requirements
- **FX-001**: Khách hàng ID = 1, Phòng = "R101", Hạng = "Deluxe", Giá = 2,000,000 VND / đêm.
- **FX-002**: Mã Voucher "SUMMER10" (isActive = true, validTo = tương lai, discount = 10%).
- **FX-003**: Mã Voucher "EXPIRED2020" (isActive = false / validTo = quá khứ).

---

### 4. Test Case Specification

#### `TC-M2-003` — Đặt phòng thành công

*   **Severity:** `CRITICAL`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-001`

**Preconditions:**
- Phòng R101 trống trong khoảng ngày đặt và khách hàng ID hợp lệ.

**Test Steps:**
1. **Arrange**: Mock `roomBookingRepository.countOverlappingBookings()` trả về `0L`.
2. **Act**: Gọi `bookingService.createBooking()` với check-in: 2026-07-15, check-out: 2026-07-20 (5 đêm).
3. **Assert**: Kiểm tra kết quả bookingId có tồn tại, status là `CONFIRMED` (sau khi cọc thành công), và `cancellationDeadline` phải là ngày checkIn - 2 ngày (2026-07-13).

**Expected Result (PASS):**
- Trả về DTO chứa đúng ID đặt phòng và cancellationDeadline hợp lệ.

**Expected Result (FAIL):**
- Không sinh ra deadline hoặc status sai.

**Current Status:** 🟢 Passing

---

#### `TC-M2-004` — Đặt phòng đồng thời (Concurrency / Overbooking)

*   **Severity:** `CRITICAL`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-002`

**Preconditions:**
- Hai luồng khách hàng đặt cùng một phòng R101 vào cùng một thời điểm.

**Test Steps:**
1. **Arrange**: Cấu hình trả về `0` cho luồng thứ nhất và `1` cho luồng thứ hai.
2. **Act**: Chạy 2 luồng đồng thời gọi `bookingService.createBooking()`.
3. **Assert**: Xác thực chỉ 1 luồng đặt phòng thành công, luồng còn lại ném ngoại lệ `RoomNotAvailableException`.

**Expected Result (PASS):**
- Số đơn đặt phòng thành công là 1, số đơn thất bại nhận lỗi là 1.

**Expected Result (FAIL):**
- Cả hai luồng đặt phòng thành công dẫn đến overbooking.

**Current Status:** 🟢 Passing

---

#### `TC-M2-005` — Validate ngày Check-out trùng ngày Check-in

*   **Severity:** `HIGH`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-003`

**Preconditions:**
- Check-in: 2026-07-15, Check-out: 2026-07-15 (0 đêm).

**Test Steps:**
1. **Arrange**: Khởi tạo request đặt phòng có check-out trùng check-in.
2. **Act**: Gọi `bookingService.createBooking(request)`.
3. **Assert**: Kiểm tra ném ngoại lệ `IllegalArgumentException`.

**Expected Result (PASS):**
- Ném lỗi `IllegalArgumentException` thông báo ngày check-out phải sau check-in.

**Current Status:** 🟢 Passing

---

#### `TC-M2-005b` — Validate ngày Check-out trước ngày Check-in

*   **Severity:** `HIGH`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-003`

**Preconditions:**
- Check-in: 2026-07-15, Check-out: 2026-07-14.

**Test Steps:**
1. **Act**: Gọi `bookingService.createBooking(request)`.
2. **Assert**: Kiểm tra ném ngoại lệ `IllegalArgumentException`.

**Expected Result (PASS):**
- Ném lỗi `IllegalArgumentException`.

**Current Status:** 🟢 Passing

---

#### `TC-M2-006` — Đặt nhiều phòng cùng lúc (Multiple Room Booking)

*   **Severity:** `HIGH`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-004`

**Preconditions:**
- Khách hàng yêu cầu đặt một đơn chứa nhiều phòng cùng lúc.

**Test Steps:**
1. **Act**: Gửi request chứa thông tin đặt nhiều phòng.
2. **Assert**: Kiểm tra đơn booking lưu thành công và tạo ra tương ứng các chi tiết đặt phòng `RoomBookingDetail`.

**Expected Result (PASS):**
- Có nhiều record `RoomBookingDetail` được lưu dưới cùng 1 `RoomBooking` ID.

**Current Status:** 🟢 Passing

---

#### `TC-M2-007` — Không đủ phòng khả dụng (Insufficient Room Availability)

*   **Severity:** `CRITICAL`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-005`

**Preconditions:**
- Chỉ còn 1 phòng Deluxe khả dụng nhưng khách hàng gửi yêu cầu đặt 2 phòng Deluxe.

**Test Steps:**
1. **Act**: Gửi yêu cầu đặt phòng với số lượng vượt quá số phòng trống khả dụng.
2. **Assert**: Kiểm tra hệ thống từ chối và ném lỗi `RoomNotAvailableException` (hoặc trả lỗi `ERR_ROOM_NOT_AVAILABLE`).

**Expected Result (PASS):**
- Ngoại lệ ném ra đúng với thông báo không đủ phòng.

**Current Status:** 🟢 Passing

---

#### `TC-M2-008` — Áp dụng mã giảm giá SUMMER10 (10% off, 5 đêm)

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-006`

**Preconditions:**
- Đặt 5 đêm, đơn giá 2,000,000 VND. Áp dụng mã "SUMMER10" giảm 10%.

**Test Steps:**
1. **Arrange**: Mock `promotionRepository` trả về voucher giảm 10%.
2. **Act**: Gọi `createBooking()` với voucher "SUMMER10".
3. **Assert**: Kiểm tra `discountedPrice` bằng 9,000,000 VND (10,000,000 - 1,000,000).

**Expected Result (PASS):**
- Giá trị sau giảm giá khớp chính xác 9,000,000 VND.

**Current Status:** 🟢 Passing

---

#### `TC-M2-008b` — Áp dụng mã giảm giá EARLYBIRD20 (20% off, 3 đêm)

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-006`

**Preconditions:**
- Đặt 3 đêm, đơn giá 2,000,000 VND. Mã "EARLYBIRD20" giảm 20%.

**Test Steps:**
1. **Act**: Gọi `createBooking()` với voucher "EARLYBIRD20".
2. **Assert**: Kiểm tra `discountedPrice` bằng 4,800,000 VND (6,000,000 - 1,200,000).

**Expected Result (PASS):**
- Giá sau giảm giá khớp chính xác 4,800,000 VND.

**Current Status:** 🟢 Passing

---

#### `TC-M2-009a` — Voucher ngưng hoạt động (isActive = false)

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-007`

**Preconditions:**
- Nhập voucher "EXPIRED2020" có trường `isActive` bằng `false`.

**Test Steps:**
1. **Act**: Gọi `createBooking()` với voucher lỗi.
2. **Assert**: Hệ thống ném lỗi `IllegalArgumentException` và thông báo chứa `[ERR_PROMO_INACTIVE]`.

**Expected Result (PASS):**
- Ném lỗi chứa đúng mã `[ERR_PROMO_INACTIVE]`.

**Current Status:** 🟢 Passing

---

#### `TC-M2-009b` — Voucher hết hạn sử dụng (Expired date)

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-007`

**Preconditions:**
- Nhập voucher hết hạn sử dụng (`validTo` trong quá khứ).

**Test Steps:**
1. **Act**: Gọi `createBooking()`.
2. **Assert**: Hệ thống ném lỗi `IllegalArgumentException` chứa mã `[ERR_PROMO_EXPIRED]`.

**Expected Result (PASS):**
- Lỗi ném ra chứa mã `[ERR_PROMO_EXPIRED]`.

**Current Status:** 🟢 Passing

---

#### `TC-M2-009c` — Voucher không tồn tại (Not found)

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-007`

**Preconditions:**
- Nhập voucher "GHOST999" không có trong cơ sở dữ liệu.

**Test Steps:**
1. **Act**: Gọi `createBooking()`.
2. **Assert**: Hệ thống ném lỗi `IllegalArgumentException` chứa mã `[ERR_PROMO_NOT_FOUND]`.

**Expected Result (PASS):**
- Lỗi ném ra chứa mã `[ERR_PROMO_NOT_FOUND]`.

**Current Status:** 🟢 Passing

---

#### `TC-M2-010` — Tính toán tiền đặt cọc (Deposit Calculation)

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `BookingServiceImpl.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-008`

**Preconditions:**
- Tổng giá trị đơn phòng là 4,000,000 VND. Tỷ lệ cọc quy định là 30%.

**Test Steps:**
1. **Act**: Hệ thống xử lý đặt phòng và tính tiền cọc.
2. **Assert**: Kiểm tra giá trị tiền cọc được tính toán và lưu là 1,200,000 VND.

**Expected Result (PASS):**
- Tiền cọc khớp chính xác với tỷ lệ 30% của tổng hóa đơn phòng.

**Current Status:** 🟢 Passing

---

#### `TC-M2-011` — VNPay chưa Callback (Trạng thái tạm thời)

*   **Severity:** `HIGH`
*   **Feature Under Test:** `BookingApiController.createBooking()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-009`

**Preconditions:**
- Đơn hàng vừa khởi tạo xong, khách đang chuyển hướng sang VNPay.

**Test Steps:**
1. **Act**: Xem trạng thái của booking vừa lưu.
2. **Assert**: Kiểm tra bookingStatus bằng `PENDING` (chờ thanh toán cọc).

**Expected Result (PASS):**
- Trạng thái booking là `PENDING`.

**Current Status:** 🟢 Passing

---

#### `TC-M2-012` — VNPay Callback thành công (IPN SUCCESS)

*   **Severity:** `CRITICAL`
*   **Feature Under Test:** `BookingServiceImpl.confirmBookingPayment()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-010`

**Preconditions:**
- Booking đang ở trạng thái `PENDING`. VNPay gọi IPN báo thành công.

**Test Steps:**
1. **Act**: Trigger API callback / IPN thành công với mã giao dịch VNPay.
2. **Assert**: Kiểm tra trạng thái booking chuyển thành `CONFIRMED`, thông tin payment được lưu và ghi lại transaction.

**Expected Result (PASS):**
- Trạng thái cập nhật sang `CONFIRMED` và ghi nhận lịch sử giao dịch.

**Current Status:** 🟢 Passing

---

#### `TC-M2-013` — VNPay Callback thất bại (IPN FAILED)

*   **Severity:** `HIGH`
*   **Feature Under Test:** `BookingServiceImpl.confirmBookingPayment()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-011`

**Preconditions:**
- Booking đang ở trạng thái `PENDING`. Khách hủy giao dịch trên VNPay.

**Test Steps:**
1. **Act**: Trigger callback báo lỗi giao dịch.
2. **Assert**: Kiểm tra trạng thái booking cập nhật sang `CANCELLED`, phòng được giải phóng về trạng thái trống, và hệ thống trả về thông báo lỗi MSG19.

**Expected Result (PASS):**
- Booking bị hủy, phòng được giải phóng an toàn.

**Current Status:** 🟢 Passing

---

#### `TC-M2-014` — Hủy do Quá hạn thanh toán (Payment Timeout 10m)

*   **Severity:** `HIGH`
*   **Feature Under Test:** `Pending_Booking_Auto_Cancellation job`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-012`

**Preconditions:**
- Có booking PENDING được tạo cách đây 16 phút và chưa thanh toán.

**Test Steps:**
1. **Act**: Kích hoạt timeout job chạy quét cơ sở dữ liệu.
2. **Assert**: Xác thực booking tự động chuyển trạng thái `CANCELLED`, giải phóng soft-lock giữ phòng, và ghi nhận lý do "Booking expired due to payment timeout".

**Expected Result (PASS):**
- Đơn đặt phòng quá hạn bị hủy hoàn toàn và giải phóng phòng thành công.

**Current Status:** 🟢 Passing

---

#### `TC-M2-015` — Gọi NotificationService gửi mail xác nhận đặt phòng

*   **Severity:** `LOW`
*   **Feature Under Test:** `BookingServiceImpl.confirmBookingPayment()`
*   **Test File:** `src/test/java/com/kawai/services/BookingServiceUC10Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-013`

**Preconditions:**
- Thanh toán thành công, đặt phòng chuyển sang `CONFIRMED`.

**Test Steps:**
1. **Arrange**: Mock `NotificationService` và cấu hình verify.
2. **Act**: Hoàn tất callback xác thực thanh toán.
3. **Assert**: Sử dụng `Mockito.verify()` để xác thực phương thức `sendBookingConfirmation()` được gọi đúng 1 lần với email của khách hàng.

**Expected Result (PASS):**
- Gửi mail xác nhận thành công tới đúng địa chỉ người dùng.

**Current Status:** 🟢 Passing

---

### 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| --- | --- | :---: | --- | --- |
| `TC-M2-003` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Khởi tạo thành công đặt phòng và set deadline |
| `TC-M2-004` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Xử lý kiểm tra trùng lịch đặt phòng đồng thời |
| `TC-M2-005` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Ràng buộc ngày check-out trùng ngày check-in |
| `TC-M2-005b`| `BookingServiceUC10Test.java` | `[x]` | `[x]` | Ràng buộc ngày check-out trước ngày check-in |
| `TC-M2-006` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Lưu thành công đơn chứa nhiều phòng đặt |
| `TC-M2-007` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Ném ngoại lệ khi hết phòng khả dụng |
| `TC-M2-008` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Áp dụng giảm giá 10% voucher SUMMER10 |
| `TC-M2-008b`| `BookingServiceUC10Test.java` | `[x]` | `[x]` | Áp dụng giảm giá 20% voucher EARLYBIRD20 |
| `TC-M2-009a`| `BookingServiceUC10Test.java` | `[x]` | `[x]` | Ném lỗi voucher không kích hoạt `[ERR_PROMO_INACTIVE]` |
| `TC-M2-009b`| `BookingServiceUC10Test.java` | `[x]` | `[x]` | Ném lỗi voucher hết hạn sử dụng `[ERR_PROMO_EXPIRED]` |
| `TC-M2-009c`| `BookingServiceUC10Test.java` | `[x]` | `[x]` | Ném lỗi voucher không tồn tại `[ERR_PROMO_NOT_FOUND]` |
| `TC-M2-010` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Tính tiền đặt cọc 30% tổng hóa đơn phòng |
| `TC-M2-011` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Đơn hàng chờ cọc ở trạng thái PENDING |
| `TC-M2-012` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Callback thanh toán thành công chuyển CONFIRMED |
| `TC-M2-013` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Callback thanh toán thất bại chuyển CANCELLED |
| `TC-M2-014` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Quá 10 phút không thanh toán tự động hủy |
| `TC-M2-015` | `BookingServiceUC10Test.java` | `[x]` | `[x]` | Gửi mail xác nhận khi phòng đặt CONFIRMED |

---

### 6. Entry / Exit Criteria

#### Entry Criteria (Điều kiện bắt đầu)
- Đặc tả yêu cầu Use Case 10 (2.1.10) từ tài liệu SRS đã được duyệt.
- Tệp kiểm thử JUnit `BookingServiceUC10Test.java` đã được thiết lập.
- Cơ sở dữ liệu và cấu hình môi trường test đã sẵn sàng.

#### Exit Criteria (Điều kiện kết thúc — DoD)
- 100% các kịch bản kiểm thử phía trên chạy thành công (`PASS`).
- Không còn lỗi logic overbooking được ghi nhận.
- Tỷ lệ code coverage cho phần logic đặt phòng đạt trên 90%.

---

### 7. Rollback Plan

**Revert implementation files:**
`git checkout -- 03_sourcecode/kawai-backend/src/main/java/com/kawai/services/impl/BookingServiceImpl.java`
