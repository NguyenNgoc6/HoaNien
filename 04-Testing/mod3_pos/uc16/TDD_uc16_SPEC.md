# TEST-DRIVEN DEVELOPMENT SPECIFICATION
## UC16 — POS Dine-In: Tạo Order & Thanh toán (PosOrderService)

| Field | Value |
|-------|-------|
| **Document ID** | `HOANIEN-TDD-MOD3-UC16-001` |
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
| **Feature / Gap ID** | `GAP-MOD3-UC16` |
| **Module** | POS — Dine-In Order & Payment (UC16) |
| **Use Case** | UC16: Nhân viên tạo Dine-In order, ghi đúng bàn/món/giá; xử lý thanh toán CASH/CARD/POST_TO_ROOM |
| **Spec gốc** | `SRS_Document_SWP391_G2.md` |
| **Priority** | 🔴 P0 (CRITICAL) |
| **Sprint** | S2 (2026-06-23 → 2026-07-07) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Internal / Financial |
| **Upstream Dependencies** | UC15 (TableReservation), Module 1 — Auth |
| **Downstream Consumers** | UC17 (KDS), UC18 (Post to Room Folio) |

---

### 2. Logic Issues Resolved

| # | Spec gốc | Thực tế | Fix áp dụng trong test |
|---|----------|---------|------------------------|
| **L1** | Không quy định rõ cách tính tổng tiền | totalPrice = SUM(qty * unitPrice) từng item | Test verify totalPrice = 2*120000 + 1*45000 = 285000 |
| **L2** | Không xử lý thanh toán order đã PAID | processPayment trên PAID order → POS-010 | Test mock order.status=PAID → POS-010 |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi
Logic `PosOrderService.createDineInOrder()`, `processPayment()`, `getOrdersByTable()`.

#### TDS-02 — Test Basis

| Source | Items Derived |
|--------|---------------|
| `SRS.md` UC16 | Dine-In order, payment processing |
| BR-POS-01 | Tính đúng tổng tiền |
| ADR-001 | Event-driven Post to Room |

#### TDS-03 — Test Conditions

| Condition ID | Test Condition | Coverage Item | Test Cases |
|-------------|----------------|---------------|------------|
| TC-COND-UC16-001 | Tạo Dine-In order → totalPrice đúng | `createDineInOrder()` | TC-UC16-001 |
| TC-COND-UC16-002 | Thanh toán CASH → PAID + receipt | `processPayment()` | TC-UC16-002 |
| TC-COND-UC16-003 | Thanh toán CARD → PAID | `processPayment()` | TC-UC16-003 |
| TC-COND-UC16-004 | Order không tồn tại → POS-009 | `processPayment()` | TC-UC16-004 |
| TC-COND-UC16-005 | Order đã PAID → POS-010 | `processPayment()` | TC-UC16-005 |

---

### 4. Test Case Specification

#### `TC-UC16-001` — POS tạo Dine-In order → ghi đúng bàn, đúng món, đúng tổng tiền
* **Severity:** CRITICAL | **Feature:** `PosOrderService.createDineInOrder()` | **File:** `PosOrderServiceUC16Test.java` | 🟢 GREEN
**Preconditions:** Bàn T05 id=5. MenuItem id=1 price=120000 (available), id=2 price=45000 (available).
**Steps:** Gọi `createDineInOrder({tableId:5, items:[{id:1,qty:2},{id:2,qty:1}]})` → Assert orderId != null, orderType=DINE_IN, tableId=5, totalPrice=285000 (= 2×120000 + 1×45000), OrderItems.count=2.

#### `TC-UC16-002` — POS thanh toán tiền mặt → order chuyển PAID
* **Severity:** HIGH | **Feature:** `PosOrderService.processPayment()` | **File:** `PosOrderServiceUC16Test.java` | 🟢 GREEN
**Preconditions:** Order id=60 status=PENDING, totalPrice=285000.
**Steps:** Gọi `processPayment(60, "CASH")` → Assert order.status=PAID, paymentMethod=CASH, paidAt != null, receiptNumber != null.

#### `TC-UC16-003` — POS thanh toán thẻ → order chuyển PAID
* **Severity:** HIGH | **Feature:** `PosOrderService.processPayment()` | **File:** `PosOrderServiceUC16Test.java` | 🟢 GREEN
**Preconditions:** Order id=61 status=PENDING.
**Steps:** Gọi `processPayment(61, "CARD")` → Assert order.status=PAID, paymentMethod=CARD.

#### `TC-UC16-004` — Order không tồn tại → OrderNotFoundException (POS-009)
* **Severity:** LOW | **Feature:** `PosOrderService.processPayment()` | **File:** `PosOrderServiceUC16Test.java` | 🟢 GREEN
**Steps:** Gọi `processPayment(9999, "CASH")` → throws `OrderNotFoundException` với code POS-009.

#### `TC-UC16-005` — Thanh toán order đã PAID → InvalidOperationException (POS-010)
* **Severity:** MEDIUM | **Feature:** `PosOrderService.processPayment()` | **File:** `PosOrderServiceUC16Test.java` | 🟢 GREEN
**Preconditions:** Order id=60 đã có status=PAID.
**Steps:** Gọi `processPayment(60, "CASH")` lần 2 → throws `InvalidOperationException` với code POS-010.

---

### 5. Red-Green-Refactor Tracker

| TC ID | Mô tả | Test File | 🔴 RED | 🔴 Commit | 🔴 Date | 🟢 GREEN | 🟢 Commit | 🟢 Date | 🔵 REFACTOR | 🔵 Commit | 🔵 Note |
|---|---|---|---|---|---|---|---|---|---|---|---|
| TC-UC16-001 | Dine-In order totalPrice đúng | `PosOrderServiceUC16Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract calculateTotal(), addJavaDoc |
| TC-UC16-002 | CASH payment → PAID | `PosOrderServiceUC16Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract generateReceiptNumber() |
| TC-UC16-003 | CARD payment → PAID | `PosOrderServiceUC16Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Reuse payment strategy |
| TC-UC16-004 | Order not found → POS-009 | `PosOrderServiceUC16Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Standardize not-found exception |
| TC-UC16-005 | Already paid → POS-010 | `PosOrderServiceUC16Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract idempotent guard |

---

### 6. Entry / Exit Criteria

#### Entry Criteria
- [x] DB có dữ liệu synthetic: Restaurant_Tables, Orders, Order_Items, MenuItems

#### Exit Criteria
- [x] Unit tests pass 100%
- [x] Price calculation tests pass

---

### 7. Rollback Plan

`git checkout -- src/main/java/com/hoanien/services/impl/PosOrderServiceImpl.java`