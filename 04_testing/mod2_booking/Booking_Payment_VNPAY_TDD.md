# TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE
## Mẫu Đặc tả Kiểm thử Hướng Phát triển

| Field | Value |
| --- | --- |
| **Document ID** | `BOOKING-TDD-001` |
| **Version** | 1.0 |
| **Date** | 2026-06-15 |
| **Status** | Approved |
| **Standard** | ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation |
| **Author** | Backend Developer |
| **Reviewed by** | `[ ] Pending` |
| **Classification** | Internal – Confidential |

---

### References:
*   `01_Requirements/SRS.md` — Functional requirements (UC-10 Book Room)
*   `03_implement/Booking_Payment_VNPAY_EDS.md` — Technical Specification
*   `VNPAY Payment Gateway_Techspec Post method 2.1.0-VN.md` — VNPAY Official Documentation

---

### CHANGELOG

| Ngày | Người thực hiện | Nội dung thay đổi |
| --- | --- | --- |
| 2026-06-15 | Chu Xuân Dũng | Khởi tạo tài liệu — TDD spec cho Booking Payment VNPAY |
| 2026-06-15 | Chu Xuân Dũng | Bổ sung các test condition và test cases cho luồng thanh toán VNPAY |

---

### MỤC LỤC

1. [Thông tin Module](#1-thong-tin-module)
2. [Logic Issues Resolved](#2-logic-issues-resolved)
3. [Test Design Specification (TDS)](#3-test-design-specification-tds)
4. [Test Case Specification](#4-test-case-specification)
5. [Red-Green-Refactor Tracker](#5-red-green-refactor-tracker)
6. [Entry / Exit Criteria](#6-entry--exit-criteria)

---

### 1. Thông tin Module

| Field | Value |
| --- | --- |
| **Feature / Gap ID** | `UC-10` |
| **Module** | `Booking & Payment` |
| **Spec gốc** | `BOOKING-PAY-IMP-001` |
| **Priority** | 🔴 P0 |
| **Milestone** | Beta Release |

---

### 2. Logic Issues Resolved

*Bắt buộc điền trước khi viết test. Liệt kê mọi sai lệch giữa spec thiết kế và schema/policy/codebase thực tế.*

| # | Spec gốc (sai / thiếu) | Thực tế (schema / policy) | Fix áp dụng trong test |
| --- | --- | --- | --- |
| **L1** | Race condition IPN vs Return Url | IPN có thể đến đồng thời cùng Request ở FE | Đảm bảo logic Idempotent trong IPN. Chỉ xử lý khi status là PENDING. Check DB row locking (ví dụ SELECT FOR UPDATE). |
| **L2** | vnp_TxnRef dùng Booking ID | Nếu thanh toán fail và muốn đổi thẻ thanh toán lại, VNPAY chặn trùng TxnRef cùng ngày | Tạo Transaction record riêng, sinh ID tự động hoặc suffix timestamp để làm `vnp_TxnRef`. |
| **L3** | vnp_Amount x100 | Số tiền gửi VNPAY cần loại bỏ thập phân và nhân 100 | Test case verify logic biến đổi số lượng trước khi hash. |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi
Kiểm thử toàn diện module thanh toán VNPAY, bao gồm:
- Hàm tạo Payment URL, tạo tham số hợp lệ và Checksum đúng HMACSHA512.
- Hàm xử lý Webhook IPN, verify chữ ký, cập nhật Booking/Transaction an toàn.
- Logic timeout tự động hủy Booking (Alternative Flow 2).

#### TDS-02 — Test Basis / Cơ sở Kiểm thử
| Source | Items Derived |
| --- | --- |
| `SRS.md` `UC-10` | Booking status transitions (Pending -> Confirmed / Cancelled). |
| `VNPAY Spec` | Định dạng params: `vnp_Version=2.1.0`, `vnp_Command=pay`, v.v... |
| `VNPAY Spec IPN` | Response Codes: "00", "01", "02", "97", "99" |

#### TDS-03 — Test Conditions and Coverage Items
| Condition ID | Test Condition | Coverage Item | Test Cases |
| --- | --- | --- | --- |
| **TC-COND-001** | Tạo Payment URL đúng format | `VnpayService.createPaymentUrl` | `PAY-TC-001` |
| **TC-COND-002** | IPN Idempotency (Trùng lặp) | `VnpayService.verifyIpn` | `PAY-TC-01` |
| **TC-COND-003** | Expired Payment Cannot Be Confirmed | `VnpayService.verifyIpn` | `PAY-TC-02` |
| **TC-COND-004** | Retry Payment (New Transaction) | `VnpayService.createPaymentUrl` | `PAY-TC-03` |
| **TC-COND-005** | Amount Mismatch Reject IPN | `VnpayService.verifyIpn` | `PAY-TC-04` |
| **TC-COND-006** | Signature Invalid IPN Rejected | `VnpayService.verifyIpn` | `PAY-TC-05` |
| **TC-COND-007** | Booking Lock Release on Expiry | `BookingTimeoutJob` | `PAY-TC-06` |
| **TC-COND-008** | Successful Payment Updates paidAt | `VnpayService.verifyIpn` | `PAY-TC-07` |

#### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử
| Technique (ISO 29119-4) | Applied To | Rationale |
| --- | --- | --- |
| **Equivalence Partitioning** | `vnp_ResponseCode` | RspCode "00" (Thành công) vs RspCode khác (Thất bại) |
| **Boundary Value Analysis** | Expiration Timer | Test mốc đúng 10 phút, < 10 phút, > 10 phút |
| **Error Guessing** | Chữ ký checksum giả | Đảm bảo IPN không thể bị tấn công fake request |

#### TDS-05 — Test Data Requirements
| Fixture ID | Type | Value / Logic | Mục đích |
| --- | --- | --- | --- |
| **FX-001** | Mock config | `VNPAY_HASH_SECRET=my_secret_key` | Tính toán chuỗi HMAC |
| **FX-002** | DB seed | Booking PENDING, depositAmount 150000 | IPN target |

---

### 4. Test Case Specification

### 4. Test Case Specification

#### `PAY-TC-01` — Duplicate IPN Request

*   **Severity:** `CRITICAL`
*   **Feature Under Test:** `VnpayService.verifyIpn()`
*   **TDD Phase:** 🔴 RED
*   **Condition Ref:** `TC-COND-002`

**Preconditions:**
- Given payment SUCCESS already.

**Test Steps:**
1. When same IPN arrives again.
2. Verify the system response and DB state.

**Expected Result (PASS):**
- Then system must NOT update again (Idempotency).
- Controller trả lời `{"RspCode": "02", "Message": "Order already confirmed"}`.

---

#### `PAY-TC-02` — Expired Payment Cannot Be Confirmed

*   **Severity:** `HIGH`
*   **Feature Under Test:** `VnpayService.verifyIpn()`
*   **TDD Phase:** 🔴 RED
*   **Condition Ref:** `TC-COND-003`

**Preconditions:**
- Given payment EXPIRED (Booking is CANCELLED).

**Test Steps:**
1. When IPN SUCCESS arrives late.
2. Verify system response.

**Expected Result (PASS):**
- Then system must ignore or log only.
- Không chuyển booking về lại CONFIRMED (chống sai trạng thái booking).

---

#### `PAY-TC-03` — Retry Payment Generates New Transaction

*   **Severity:** `CRITICAL`
*   **Feature Under Test:** `VnpayService.createPaymentUrl()`
*   **TDD Phase:** 🔴 RED
*   **Condition Ref:** `TC-COND-004`

**Preconditions:**
- Given booking PENDING.

**Test Steps:**
1. When user retry payment (chọn "Pay again").

**Expected Result (PASS):**
- Then new `vnp_TxnRef` must be generated (không reuse vnp_TxnRef cũ).
- Một record `PaymentTransaction` mới được tạo ra ở trạng thái INIT.

---

#### `PAY-TC-04` — Amount Mismatch Reject IPN

*   **Severity:** `CRITICAL`
*   **Feature Under Test:** `VnpayService.verifyIpn()`
*   **TDD Phase:** 🔴 RED
*   **Condition Ref:** `TC-COND-005`

**Preconditions:**
- Given booking amount = 500000.

**Test Steps:**
1. When IPN amount != 50000000 (VNPAY gửi sai số tiền so với DB).

**Expected Result (PASS):**
- Then reject payment.
- Controller trả về `{"RspCode": "04", "Message": "Invalid amount"}` (chống fake IPN).

---

#### `PAY-TC-05` — Signature Invalid IPN Rejected

*   **Severity:** `CRITICAL`
*   **Feature Under Test:** `VnpayService.verifyIpn()`
*   **TDD Phase:** 🔴 RED
*   **Condition Ref:** `TC-COND-006`

**Preconditions:**
- Given invalid checksum trong query parameters.

**Test Steps:**
1. When IPN arrives.

**Expected Result (PASS):**
- Then status unchanged.
- Controller trả về `{"RspCode": "97", "Message": "Invalid Checksum"}`.

---

#### `PAY-TC-06` — Booking Lock Release on Expiry

*   **Severity:** `HIGH`
*   **Feature Under Test:** `BookingTimeoutJob`
*   **TDD Phase:** 🔴 RED
*   **Condition Ref:** `TC-COND-007`

**Preconditions:**
- Given booking PENDING.

**Test Steps:**
1. When expired (quá 10 phút).
2. Job timeout chạy.

**Expected Result (PASS):**
- Then booking becomes CANCELLED and room unlocked (inventory được rollback).

---

#### `PAY-TC-07` — Successful Payment Updates paidAt

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `VnpayService.verifyIpn()`
*   **TDD Phase:** 🔴 RED
*   **Condition Ref:** `TC-COND-008`

**Preconditions:**
- Given valid IPN SUCCESS.

**Test Steps:**
1. IPN đến và checksum hợp lệ.

**Expected Result (PASS):**
- Then `paidAt` must be set (lưu lại thời điểm thanh toán thành công).

---

### 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| --- | --- | :---: | --- | --- |
| `PAY-TC-01` | `payment.controller.spec.ts` | `[ ]` | `[ ]` |  |
| `PAY-TC-02` | `payment.controller.spec.ts`| `[ ]` | `[ ]` |  |
| `PAY-TC-03` | `vnpay.service.spec.ts` | `[ ]` | `[ ]` |  |
| `PAY-TC-04` | `payment.controller.spec.ts`| `[ ]` | `[ ]` |  |
| `PAY-TC-05` | `payment.controller.spec.ts`| `[ ]` | `[ ]` |  |
| `PAY-TC-06` | `booking-timeout.job.spec.ts`| `[ ]` | `[ ]` |  |
| `PAY-TC-07` | `payment.controller.spec.ts`| `[ ]` | `[ ]` |  |

---

### 6. Entry / Exit Criteria

#### Entry Criteria (Điều kiện bắt đầu)
- [x] Spec kỹ thuật VNPAY đã được đọc kỹ.
- [x] Template EDS đã hoàn thiện cho module.
- [x] Credentials sandbox (TmnCode, HashSecret) có sẵn trên VNPay Dev portal.

#### Exit Criteria (Điều kiện kết thúc — DoD)
- [ ] Tất cả các test cases trên chuyển sang trạng thái 🟢.
- [ ] Chạy tay (Manual Testing) 1 luồng end-to-end với Postman / VNPAY test card thành công.
- [ ] KHÔNG để lộ `VNPAY_HASH_SECRET` trong log hệ thống dưới mọi hình thức.
