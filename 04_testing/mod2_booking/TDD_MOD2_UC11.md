# TEST-DRIVEN DEVELOPMENT SPECIFICATION FOR UC-11

## Đặc tả Kiểm thử Hướng Phát triển - UC-11: Hủy đặt phòng (Cancel Booking)

| Field | Value |
| --- | --- |
| **Document ID** | `KAWAI-TDD-MOD2-011` |
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
*   `04_testing/mod2_booking/EDS_MOD2_UC11.md` — Đặc tả kỹ thuật UC-11
*   `04_testing/mod2_booking/SRS_Document_SWP391_G2.docx.md` — Tài liệu đặc tả yêu cầu UC-11

> [!NOTE]
> **Quy ước TDD:** Thiết lập các kịch bản test trước khi hoàn thiện logic nghiệp vụ để đảm bảo độ bao phủ của code và ngăn ngừa lỗi logic.

---

### CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| --- | --- | --- |
| 2026-06-14 | `Chu Xuân Dũng` | Khởi tạo tài liệu — TDD spec riêng biệt cho UC-11 |
| 2026-06-14 | `AI Agent` | Cập nhật tài liệu sang trạng thái GREEN sau khi pass toàn bộ 8 Test Cases |
| 2026-06-15 | `Chu Xuân Dũng` | Rà soát các luồng kiểm thử ngoại lệ cho chức năng Hủy phòng |

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
| **Feature / Gap ID** | `GAP-MOD2-UC11` |
| **Module** | `Đặt phòng (Booking & Front Desk)` |
| **Spec gốc** | `SRS_Document_SWP391_G2.docx.md` |
| **Priority** | 🔴 High |
| **Sprint** | `S1` |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Confidential & PII |
| **Compliance Scope** | Luật du lịch Việt Nam 2017 & Luật Bảo vệ người tiêu dùng |
| **Upstream Dependencies** | `UC-10 Book Room` |
| **Downstream Consumers** | `Payment Gateway (VNPay), Hoàn tiền` |

---

### 2. Logic Issues Resolved

| # | Spec gốc | Thực tế | Fix áp dụng trong test |
| --- | --- | --- | --- |
| **L1** | Exception E4 mô tả hủy bị từ chối với thông báo MSG22 khi < 48h | Theo spec, hệ thống từ chối hoàn toàn yêu cầu hủy nếu cách check-in < 48h | Test case sẽ chặn Exception 400 với thông báo MSG22 khi `hoursUntilCheckIn < 48` |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi
Kiểm thử logic hủy đơn đặt phòng của `BookingService.cancelBooking`, áp dụng luật 48h trước thời điểm check-in (BR-FIN-02), tương tác với Payment Gateway để hoàn tiền và cập nhật trạng thái đơn đặt.

#### TDS-02 — Test Basis / Cơ sở Kiểm thử
- `SRS_Document_SWP391_G2.docx.md` UC-11 Cancel Booking.
- BR-FIN-02: Hủy trước 48h được hoàn 100% cọc. Hủy trong vòng 48h không được phép hủy/không hoàn cọc.
- MSG22: Booking cannot be canceled as more than 48 hours have passed.

#### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| --- | --- | --- | --- |
| **TC-COND-001** | Hủy thành công trước 48h | `BookingService.cancelBooking` | `TC-M2-UC11-001` |
| **TC-COND-002** | Vi phạm giới hạn 48h (E4) | `BookingService.cancelBooking` | `TC-M2-UC11-002` |
| **TC-COND-003** | Trạng thái không hợp lệ (Checked-In/Cancelled) | `BookingService.cancelBooking` | `TC-M2-UC11-003` |
| **TC-COND-004** | Hoàn tiền thất bại (Gateway error) | `BookingService.cancelBooking` | `TC-M2-UC11-004` |
| **TC-COND-005** | Hủy chính xác hoặc trước mốc 48h (VD: 15/06 hủy 13/06) | `BookingService.cancelBooking` | `TC-M2-UC11-005` |
| **TC-COND-006** | Giải phóng room inventory sau khi hủy | `BookingService.cancelBooking` | `TC-M2-UC11-006` |
| **TC-COND-007** | Gửi notification khi hủy thành công | `BookingService.cancelBooking` | `TC-M2-UC11-007` |
| **TC-COND-008** | Gửi notification khi hủy thất bại | `BookingService.cancelBooking` | `TC-M2-UC11-008` |

#### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử
- **Boundary Value Analysis**: Test đúng mốc >48h và <48h trước thời gian check-in.
- **State Transition Testing**: Test từ trạng thái CONFIRMED -> CANCELLED. Test fail khi trạng thái hiện tại là CHECKED_IN.

---

### 4. Test Case Specification

#### `TC-M2-UC11-001` — Hủy thành công trước 48h (Full Refund)

*   **Severity:** `CRITICAL`
*   **Legal:** `BR-FIN-02`
*   **Feature Under Test:** `BookingService.cancelBooking()`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-001`

**Preconditions:**
- Khách hàng đã có đơn đặt phòng `CONFIRMED`.
- Thời điểm hiện tại cách ngày `checkInDate` >= 48 giờ.

**Test Steps:**
1. Mock dữ liệu Booking: `status = CONFIRMED`, `checkInDate = Today + 5 days`.
2. Mock Payment Gateway trả về SUCCESS.
3. Gọi `BookingService.cancelBooking(bookingId, customerId)`.

**Expected Result (PASS):**
- Trạng thái booking chuyển thành `CANCELLED`.
- Repository lưu lại thay đổi trạng thái.
- Repository lưu transaction Refund (nếu áp dụng).
- Hàm trả về DTO với trạng thái hủy thành công.

---

#### `TC-M2-UC11-002` — Hủy thất bại do vi phạm luật 48h (E4 - MSG22)

*   **Severity:** `HIGH`
*   **Legal:** `BR-FIN-02`
*   **Feature Under Test:** `BookingService.cancelBooking()`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-002`

**Preconditions:**
- Khách hàng có đơn đặt phòng `CONFIRMED`.
- Thời điểm hiện tại cách ngày `checkInDate` < 48 giờ (ví dụ còn 24 giờ).

**Test Steps:**
1. Mock dữ liệu Booking: `status = CONFIRMED`, `checkInDate = Today + 1 day`.
2. Gọi `BookingService.cancelBooking(bookingId, customerId)`.

**Expected Result (PASS):**
- Ném ra Exception (ví dụ: `BusinessException`).
- Exception message chứa nội dung MSG22: `"Booking cannot be canceled as more than 48 hours have passed."`
- Trạng thái DB không thay đổi.

---

#### `TC-M2-UC11-003` — Hủy thất bại do trạng thái không hợp lệ

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `BookingService.cancelBooking()`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-003`

**Preconditions:**
- Đơn đặt phòng có trạng thái `CHECKED_IN` hoặc đã `CANCELLED`.

**Test Steps:**
1. Mock Booking với trạng thái `CHECKED_IN`.
2. Gọi `cancelBooking()`.

**Expected Result (PASS):**
- Ném ra `BusinessException` với thông báo trạng thái không hợp lệ.

---

#### `TC-M2-UC11-004` — Hủy thất bại do Payment Gateway Error (AF2)

*   **Severity:** `HIGH`
*   **Feature Under Test:** `BookingService.cancelBooking()`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-004`

**Preconditions:**
- Thỏa mãn hủy trước 48h.
- VNPay/Stripe bị timeout hoặc trả về lỗi.

**Test Steps:**
1. Mock Booking hợp lệ trước 48h.
2. Mock Payment Gateway ném ra `PaymentGatewayException`.
3. Gọi `cancelBooking()`.

**Expected Result (PASS):**
- Ném ra `SystemException` hoặc `PaymentGatewayException`.
- Thông báo lỗi: `"Cancellation failed due to a system error. Please try again later."`
- Mọi thay đổi trong Database bị Rollback (trạng thái vẫn là CONFIRMED).
- Log lỗi được ghi nhận (Audit).

---

#### `TC-M2-UC11-005` — Hủy chính xác hoặc trước mốc 48h (Test Boundary)

*   **Severity:** `HIGH`
*   **Legal:** `BR-FIN-02`
*   **Feature Under Test:** `BookingService.cancelBooking()`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-005`

**Preconditions:**
- Khách hàng có đơn đặt phòng `CONFIRMED`.

**Test Steps:**
1. Mock Booking có `checkInDate` là `15/06 14:00`.
2. Giả lập thời điểm gọi hàm (Cancel time) là `13/06 14:00` (đúng 48 tiếng trước check-in).
3. Gọi `cancelBooking()`.

**Expected Result (PASS):**
- Trạng thái booking chuyển thành `CANCELLED`.
- Hệ thống duyệt yêu cầu (Allowed) và hoàn tiền (Refund) 100%.

---

#### `TC-M2-UC11-006` — Giải phóng room inventory sau khi hủy

*   **Severity:** `HIGH`
*   **Feature Under Test:** `BookingService.cancelBooking()`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-006`

**Preconditions:**
- Khách hàng hủy đơn đặt phòng `CONFIRMED` thành công.

**Test Steps:**
1. Thực hiện luồng hủy phòng thành công (tương tự `TC-M2-UC11-001`).
2. Kiểm tra lại tình trạng phòng trong Database hoặc thông qua `RoomService`.

**Expected Result (PASS):**
- AvailableRooms của loại phòng đó tăng lại (+ số lượng phòng đã hủy).
- Room status được release về trạng thái khả dụng.

---

#### `TC-M2-UC11-007` — Gửi notification khi hủy thành công

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `BookingService.cancelBooking()`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-007`

**Preconditions:**
- Mock `NotificationService`.
- Hệ thống thực hiện hủy phòng thành công.

**Test Steps:**
1. Thực hiện luồng hủy phòng thành công.
2. Kiểm tra `NotificationService` có được gọi hay không.

**Expected Result (PASS):**
- Hàm gửi thông báo của `NotificationService` được gọi 1 lần.
- Nội dung thông báo xác nhận hủy thành công gửi đến đúng khách hàng.

---

#### `TC-M2-UC11-008` — Gửi notification khi hủy thất bại

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `BookingService.cancelBooking()`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-008`

**Preconditions:**
- Hủy phòng gặp lỗi nghiệp vụ (quá 48h) hoặc lỗi hệ thống.

**Test Steps:**
1. Thực hiện luồng hủy phòng thất bại (ví dụ `< 48h` hoặc Payment Gateway lỗi).
2. Kiểm tra hành vi của `NotificationService`.

**Expected Result (PASS):**
- Notification thông báo hủy thất bại được gửi đến khách hàng HOẶC thông báo lỗi hiển thị In-line tại giao diện Front-end. Cần verify đúng thiết kế của hệ thống.

---

### 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED | 🟢 GREEN | 🔵 REFACTOR note |
| --- | --- | :---: | --- | --- |
| `TC-M2-UC11-001` | `BookingServiceUC11Test.java` | `[x]` | `[x]` | Refactor Interface `cancelBooking(Long, Long)` để xác thực quyền user. |
| `TC-M2-UC11-002` | `BookingServiceUC11Test.java` | `[x]` | `[x]` | Tái sử dụng luật 48h, đồng thời Disable test cũ trong UC10 vì đã lỗi thời. |
| `TC-M2-UC11-003` | `BookingServiceUC11Test.java` | `[x]` | `[x]` | Thêm validation chặn hủy nếu đang CHECKED_IN hoặc CANCELLED. |
| `TC-M2-UC11-004` | `BookingServiceUC11Test.java` | `[x]` | `[x]` | Tạo `PaymentGatewayException` và dummy implementation để mock. |
| `TC-M2-UC11-005` | `BookingServiceUC11Test.java` | `[x]` | `[x]` | Change to plusDays(3) for timezone safety. |
| `TC-M2-UC11-006` | `BookingServiceUC11Test.java` | `[x]` | `[x]` | Đổi status sang CANCELLED, room logic tự động nhả phòng theo JPQL. |
| `TC-M2-UC11-007` | `BookingServiceUC11Test.java` | `[x]` | `[x]` | Tạo `NotificationService` dummy để xử lý logic gọi gửi mail. |
| `TC-M2-UC11-008` | `BookingServiceUC11Test.java` | `[x]` | `[x]` | Gọi sendNotification khi throw exception vi phạm luật 48h. |

---

### 6. Entry / Exit Criteria

#### Entry Criteria
- [x] Spec UC-11 đã chốt.
- [x] Template TDD / EDS đã có sẵn.

#### Exit Criteria
- [ ] Tất cả tests cho UC-11 pass (🟢 GREEN).
- [ ] Xử lý thành công Rollback khi Gateway lỗi (AF2).
