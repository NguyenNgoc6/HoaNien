# TEST-DRIVEN DEVELOPMENT SPECIFICATION
## UC20 — Đặt Tour & Lập lịch & Hủy Tour (TourBookingService)

| Field | Value |
|-------|-------|
| **Document ID** | `HOANIEN-TDD-MOD4-UC20-001` |
| **Version** | 1.0 |
| **Date** | 2026-06-14 |
| **Status** | Approved |
| **Standard** | ISO/IEC/IEEE 29119-3:2021 |
| **Author** | Nguyễn Thị Thúy Ngọc — Developer |
| **Reviewed by** | [x] Nguyễn Xuân Lưu — Tech Lead |
| **DPO Sign-off** | `[x] Approved – 2026-06-14 – Nguyễn Xuân Lưu` |
| **Approved by** | `[x] Nguyễn Xuân Lưu – 2026-06-14` |
| **Classification** | Internal — Confidential |

---

### MỤC LỤC
1. [Thông tin Module](#1)
2. [Logic Issues Resolved](#2)
3. [TDS](#3)
4. [Test Case Specification](#4)
5. [Red-Green-Refactor Tracker](#5)
6. [Entry / Exit Criteria](#6)
7. [Rollback Plan](#7)

---

### 1. Thông tin Module

| Field | Value |
|-------|-------|
| **Feature / Gap ID** | `GAP-MOD4-UC20` |
| **Module** | Tour — Đặt Tour & Lập lịch & Hủy Tour (UC20) |
| **Use Case** | UC20.1: Đặt tour; UC20.2: Lập lịch (gán xe, hướng dẫn viên); UC20.3: Hủy tour |
| **Spec gốc** | `SRS_Document_SWP391_G2.md` |
| **Priority** | 🔴 P0 |
| **Sprint** | S2 (2026-06-23 → 2026-07-07) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | PII (thông tin khách, phòng) |
| **Upstream Dependencies** | UC01 (Auth), UC19 (Tour Search), Module 2 (Folio) |
| **Downstream Consumers** | Module 5 (Finance), UC21 (Điểm danh) |

---

### 2. Logic Issues Resolved

| # | Spec gốc | Thực tế | Fix áp dụng trong test |
|---|----------|---------|------------------------|
| **L1** | Chưa quy định xử lý đồng thời khi đặt tour | Dùng SELECT FOR UPDATE để tránh overbooking | Test concurrent booking |
| **L2** | Chính sách hủy tour chưa rõ | > 24h: hoàn 100%; < 24h: hoàn 50% | Test 2 case hủy |
| **L3** | Post to Room chưa rõ flow | Gọi `FolioService.postTourCharge()` sau khi tạo booking | Test verify FolioService |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi
Logic `TourBookingService.createTourBooking()`, `scheduleTour()`, `cancelTour()`.

#### TDS-02 — Test Basis

| Source | Items Derived |
|--------|---------------|
| `SRS.md` UC20 | Đặt tour, kiểm tra slot, Post to Room, hủy tour |
| BR-TOUR-01 | Chặn đặt khi hết slot |
| BR-TOUR-02 | Post to Room ghi nợ Folio |

#### TDS-03 — Test Conditions

| Condition ID | Test Condition | Coverage Item | Test Cases |
|-------------|----------------|---------------|------------|
| TC-COND-UC20-001 | Đặt tour thành công, còn slot | `createTourBooking()` | TC-UC20-001 |
| TC-COND-UC20-002 | Tour hết slot → TOUR-001 | `createTourBooking()` | TC-UC20-002 |
| TC-COND-UC20-003 | Post to Room → FolioService gọi đúng | `createTourBooking()` + `FolioService` | TC-UC20-003 |
| TC-COND-UC20-004 | Lập lịch tour → gán xe, tài xế, guide | `scheduleTour()` | TC-UC20-004 |
| TC-COND-UC20-005 | Hủy tour > 24h → hoàn 100% | `cancelTour()` | TC-UC20-005 |
| TC-COND-UC20-006 | Hủy tour < 24h → hoàn 50% | `cancelTour()` | TC-UC20-006 |
| TC-COND-UC20-007 | Hủy tour đã qua → TOUR-006 | `cancelTour()` | TC-UC20-007 |

---

### 4. Test Case Specification

#### `TC-UC20-001` — Đặt tour thành công
* **Severity:** CRITICAL | **Feature:** `createTourBooking()` | **File:** `TourBookingServiceUC20Test.java` | 🟢 GREEN
**Preconditions:** TourSchedule id=1 có bookedSeats=5, maxSlots=20.
**Steps:** Gọi `createTourBooking({scheduleId:1, participantCount:3})` → Assert bookingStatus=CONFIRMED, bookedSeats=8.

#### `TC-UC20-002` — Tour hết slot → TOUR-001
* **Severity:** CRITICAL | **Feature:** `createTourBooking()` | **File:** `TourBookingServiceUC20Test.java` | 🟢 GREEN
**Steps:** bookedSeats=18, maxSlots=20, gọi với participantCount=5 → throws `TourFullException`.

#### `TC-UC20-003` — Post to Room → ghi nợ Folio
* **Severity:** HIGH | **Feature:** `createTourBooking()` + `FolioService` | **File:** `TourBookingTddServiceUC20Test.java` | 🟢 GREEN
**Steps:** Gọi với paymentMethod=POST_TO_ROOM → Verify `FolioService.postTourCharge()` được gọi.

#### `TC-UC20-004` — Lập lịch tour
* **Severity:** MEDIUM | **Feature:** `scheduleTour()` | **File:** `TourBookingTddServiceUC20Test.java` | 🟢 GREEN
**Steps:** Gọi `scheduleTour(1, 5, 2, 3)` → Assert scheduleStatus=SCHEDULED.

#### `TC-UC20-005` — Hủy > 24h → hoàn 100%
* **Severity:** HIGH | **Feature:** `cancelTour()` | **File:** `TourBookingTddServiceUC20Test.java` | 🟢 GREEN
**Steps:** departureDate=now+48h → Assert refundAmount=full.

#### `TC-UC20-006` — Hủy < 24h → hoàn 50%
* **Severity:** HIGH | **Feature:** `cancelTour()` | **File:** `TourBookingTddServiceUC20Test.java` | 🟢 GREEN
**Steps:** departureDate=now+12h → Assert refundAmount=50%.

#### `TC-UC20-007` — Hủy tour đã qua → TOUR-006
* **Severity:** MEDIUM | **Feature:** `cancelTour()` | **File:** `TourBookingTddServiceUC20Test.java` | 🟢 GREEN
**Steps:** departureDate=now-2 ngày → throws `InvalidOperationException`.

---

### 5. Red-Green-Refactor Tracker

| TC ID | Mô tả | Test File | 🔴 RED | 🟢 GREEN | 🔵 REFACTOR |
|---|---|---|---|---|---|
| TC-UC20-001 | Đặt tour thành công | `TourBookingServiceUC20Test.java` | [x] | [x] | ✅ Extract attendee creation |
| TC-UC20-002 | Tour hết slot → TOUR-001 | `TourBookingServiceUC20Test.java` | [x] | [x] | ✅ Optimize concurrent check |
| TC-UC20-003 | Post to Room → Folio | `TourBookingTddServiceUC20Test.java` | [x] | [x] | ✅ Standardize billing strategy |
| TC-UC20-004 | Lập lịch tour | `TourBookingTddServiceUC20Test.java` | [x] | [x] | ✅ Extract scheduling algorithm |
| TC-UC20-005 | Hủy > 24h → hoàn 100% | `TourBookingTddServiceUC20Test.java` | [x] | [x] | ✅ Extract cancellation calculator |
| TC-UC20-006 | Hủy < 24h → hoàn 50% | `TourBookingTddServiceUC20Test.java` | [x] | [x] | ✅ Reuse calculator |
| TC-UC20-007 | Hủy tour đã qua → TOUR-006 | `TourBookingTddServiceUC20Test.java` | [x] | [x] | ✅ Extract past date validation |

---

### 6. Entry / Exit Criteria

#### Entry Criteria
- [x] Database đã có bảng Tour_Bookings, Tour_Attendees với dữ liệu synthetic
- [x] FolioService mock đã sẵn sàng trong test context

#### Exit Criteria
- [x] Unit tests pass 100%
- [x] Concurrency test pass (không overbooking)
- [x] Cancellation policy tests pass

---

### 7. Rollback Plan

`git checkout -- src/main/java/com/hoanien/services/impl/TourBookingServiceImpl.java`