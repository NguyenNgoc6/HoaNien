# TEST-DRIVEN DEVELOPMENT SPECIFICATION
## UC15 — Đặt bàn nhà hàng (TableReservationService)

| Field | Value |
|-------|-------|
| **Document ID** | `HOANIEN-TDD-MOD3-UC15-001` |
| **Version** | 1.0 |
| **Date** | 2026-06-14 |
| **Status** | Approved |
| **Standard** | ISO/IEC/IEEE 29119-3:2021 |
| **Author** | Trịnh Minh Đức — Developer |
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
| **Feature / Gap ID** | `GAP-MOD3-UC15` |
| **Module** | POS — Đặt bàn nhà hàng (UC15) |
| **Use Case** | UC15: Đặt bàn theo ngày giờ, kiểm tra availability, chống double-booking |
| **Spec gốc** | `SRS_Document_SWP391_G2.md` |
| **Priority** | 🟡 P1 |
| **Sprint** | S2 (2026-06-23 → 2026-07-07) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Internal |
| **Upstream Dependencies** | Module 1 — Auth (JWT) |
| **Downstream Consumers** | UC16 (Dine-In POS) |

---

### 2. Logic Issues Resolved

| # | Spec gốc | Thực tế | Fix áp dụng trong test |
|---|----------|---------|------------------------|
| **L1** | Không xử lý đặt bàn đồng thời | Pessimistic Locking tránh double-booking | Test 2 concurrent requests → 1 thành công, 1 POS-006 |
| **L2** | Không kiểm tra sức chứa | partySize > capacity → POS-007 | Test mock partySize > table.capacity |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi
Logic `TableReservationService.reserveTable()`, `getAvailableTables()`, `cancelReservation()`.

#### TDS-02 — Test Basis

| Source | Items Derived |
|--------|---------------|
| `SRS.md` UC15 | Đặt bàn, kiểm tra available, hủy |
| BR-POS-05 | Concurrency-safe reservation |
| ADR-003 | Pessimistic/Optimistic Locking |

#### TDS-03 — Test Conditions

| Condition ID | Test Condition | Coverage Item | Test Cases |
|-------------|----------------|---------------|------------|
| TC-COND-UC15-001 | Lấy bàn trống → đúng danh sách | `getAvailableTables()` | TC-UC15-001 |
| TC-COND-UC15-002 | Đặt bàn thành công → CONFIRMED | `reserveTable()` | TC-UC15-002 |
| TC-COND-UC15-003 | Bàn đã có booking → POS-006 | `reserveTable()` conflict check | TC-UC15-003 |
| TC-COND-UC15-004 | partySize > capacity → POS-007 | `reserveTable()` | TC-UC15-004 |
| TC-COND-UC15-005 | Hủy đặt bàn → CANCELLED | `cancelReservation()` | TC-UC15-005 |

---

### 4. Test Case Specification

#### `TC-UC15-001` — Lấy bàn trống theo ngày giờ → trả danh sách đúng
* **Severity:** LOW | **Feature:** `TableReservationService.getAvailableTables()` | **File:** `TableReservationServiceUC15Test.java` | 🟢 GREEN
**Preconditions:** DB có 5 bàn, bàn T05 đã có reservation lúc 19:00 ngày 2026-06-20.
**Steps:** Gọi `getAvailableTables(2026-06-20, 19:00, 4)` → Assert list không chứa T05, các bàn còn lại có capacity >= 4.

#### `TC-UC15-002` — Đặt bàn thành công → TableReservation CONFIRMED, bàn RESERVED
* **Severity:** HIGH | **Feature:** `TableReservationService.reserveTable()` | **File:** `TableReservationServiceUC15Test.java` | 🟢 GREEN
**Preconditions:** Bàn T03 không có reservation lúc 19:00, capacity=6.
**Steps:** Gọi `reserveTable({tableId:3, date:2026-06-20, time:19:00, partySize:4})` → Assert reservationId != null, status=CONFIRMED, table.tableStatus=RESERVED.

#### `TC-UC15-003` — 2 khách đặt cùng bàn cùng giờ → 1 thành công, 1 POS-006
* **Severity:** HIGH | **Feature:** `TableReservationService.reserveTable()` | **File:** `TableReservationServiceUC15Test.java` | 🟢 GREEN
**Preconditions:** Bàn T03 đang AVAILABLE lúc 19:00.
**Steps (Concurrency):** Gửi 2 request đồng thời `reserveTable({tableId:3, time:19:00})` → Assert 1 trả reservationId, 1 throws `TableAlreadyReservedException` (POS-006).

#### `TC-UC15-004` — Số khách vượt sức chứa → TableCapacityExceededException (POS-007)
* **Severity:** MEDIUM | **Feature:** `TableReservationService.reserveTable()` | **File:** `TableReservationServiceUC15Test.java` | 🟢 GREEN
**Preconditions:** Bàn T01 capacity=2.
**Steps:** Gọi `reserveTable({tableId:1, partySize:6})` → throws `TableCapacityExceededException` với code POS-007.

#### `TC-UC15-005` — Hủy đặt bàn → CANCELLED, bàn trở lại AVAILABLE
* **Severity:** MEDIUM | **Feature:** `TableReservationService.cancelReservation()` | **File:** `TableReservationServiceUC15Test.java` | 🟢 GREEN
**Preconditions:** TableReservation id=20 đang status=CONFIRMED, bàn RESERVED.
**Steps:** Gọi `cancelReservation(20)` → Assert reservation.status=CANCELLED, table.tableStatus=AVAILABLE.

---

### 5. Red-Green-Refactor Tracker

| TC ID | Mô tả | Test File | 🔴 RED | 🔴 Commit | 🔴 Date | 🟢 GREEN | 🟢 Commit | 🟢 Date | 🔵 REFACTOR | 🔵 Commit | 🔵 Note |
|---|---|---|---|---|---|---|---|---|---|---|---|
| TC-UC15-001 | Get available tables | `TableReservationServiceUC15Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract availability query to repository |
| TC-UC15-002 | Reserve table success | `TableReservationServiceUC15Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract reservation creation logic |
| TC-UC15-003 | Double booking → POS-006 | `TableReservationServiceUC15Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Optimize concurrent slots check query |
| TC-UC15-004 | Party size > capacity → POS-007 | `TableReservationServiceUC15Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract capacity validation |
| TC-UC15-005 | Cancel reservation | `TableReservationServiceUC15Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract table status restore logic |

---

### 6. Entry / Exit Criteria

#### Entry Criteria
- [x] DB có dữ liệu synthetic: Restaurant_Tables, Table_Reservations

#### Exit Criteria
- [x] Unit tests pass 100%
- [x] Concurrency test pass (0 double-booking)

---

### 7. Rollback Plan

`git checkout -- src/main/java/com/hoanien/services/impl/TableReservationServiceImpl.java`