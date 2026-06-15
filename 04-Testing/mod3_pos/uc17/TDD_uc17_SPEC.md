# TEST-DRIVEN DEVELOPMENT SPECIFICATION
## UC17 — KDS & Quản lý Hết món (KdsService)

| Field | Value |
|-------|-------|
| **Document ID** | `HOANIEN-TDD-MOD3-UC17-001` |
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
| **Feature / Gap ID** | `GAP-MOD3-UC17` |
| **Module** | POS — KDS & Quản lý Hết món (UC17) |
| **Use Case** | UC17.1: KDS nhận KOT, bếp cập nhật PREPARING→READY; UC17.2: Bếp báo hết món → khóa trên E-Menu |
| **Spec gốc** | `SRS_Document_SWP391_G2.md` |
| **Priority** | 🟡 P1 |
| **Sprint** | S2 (2026-06-23 → 2026-07-07) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Internal |
| **Upstream Dependencies** | UC16 (POS Dine-In/Room Service tạo Order) |
| **Downstream Consumers** | UC14 (E-Menu — phản ánh tính sẵn có của món) |

---

### 2. Logic Issues Resolved

| # | Spec gốc | Thực tế | Fix áp dụng trong test |
|---|----------|---------|------------------------|
| **L1** | Không kiểm tra status transition | Chỉ được chuyển PENDING→PREPARING→READY (không nhảy cóc) | Test READY→PENDING → POS-012 |
| **L2** | Khóa món chỉ hiển thị trên KDS | Phải cập nhật MenuItem.isAvailable=false → E-Menu tự filter | Test markItemUnavailable → isAvailable=false, getEMenu không còn trả món đó |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi
Logic `KdsService.getActiveOrders()`, `updateOrderItemStatus()`, `markItemUnavailable()`, `markItemAvailable()`.

#### TDS-02 — Test Basis

| Source | Items Derived |
|--------|---------------|
| `SRS.md` UC17.1, UC17.2 | KDS workflow, item availability |
| ADR-004 | SSE real-time delivery |

#### TDS-03 — Test Conditions

| Condition ID | Test Condition | Coverage Item | Test Cases |
|-------------|----------------|---------------|------------|
| TC-COND-UC17-001 | KDS lấy active orders | `getActiveOrders()` | TC-UC17-001 |
| TC-COND-UC17-002 | Bếp cập nhật PREPARING → READY | `updateOrderItemStatus()` | TC-UC17-002 |
| TC-COND-UC17-003 | Chuyển trạng thái không hợp lệ → POS-012 | `updateOrderItemStatus()` | TC-UC17-003 |
| TC-COND-UC17-004 | Báo hết món → isAvailable=false | `markItemUnavailable()` | TC-UC17-004 |
| TC-COND-UC17-005 | Mở lại món → isAvailable=true | `markItemAvailable()` | TC-UC17-005 |

---

### 4. Test Case Specification

#### `TC-UC17-001` — KDS nhận order mới → trả đúng danh sách active orders
* **Severity:** HIGH | **Feature:** `KdsService.getActiveOrders()` | **File:** `KdsServiceUC17Test.java` | 🟢 GREEN
**Preconditions:** DB có 3 Orders: 2 PENDING, 1 PAID.
**Steps:** Gọi `getActiveOrders()` → Assert list.size()=2, tất cả orders có status IN (PENDING, PREPARING), mỗi order có items list đầy đủ.

#### `TC-UC17-002` — Bếp cập nhật từng món PREPARING → READY
* **Severity:** HIGH | **Feature:** `KdsService.updateOrderItemStatus()` | **File:** `KdsServiceUC17Test.java` | 🟢 GREEN
**Preconditions:** OrderItem id=15 có itemStatus=PREPARING.
**Steps:** Gọi `updateOrderItemStatus(15, "READY")` → Assert OrderItem.itemStatus=READY.

#### `TC-UC17-003` — Chuyển trạng thái không hợp lệ → InvalidStatusTransitionException (POS-012)
* **Severity:** MEDIUM | **Feature:** `KdsService.updateOrderItemStatus()` | **File:** `KdsServiceUC17Test.java` | 🟢 GREEN
**Preconditions:** OrderItem id=15 có itemStatus=READY.
**Steps:** Gọi `updateOrderItemStatus(15, "PENDING")` → throws `InvalidStatusTransitionException` với code POS-012.

#### `TC-UC17-004` — Bếp báo hết món → MenuItem.isAvailable=false
* **Severity:** HIGH | **Feature:** `KdsService.markItemUnavailable()` | **File:** `KdsServiceUC17Test.java` | 🟢 GREEN
**Preconditions:** MenuItem id=3 isAvailable=true.
**Steps:** Gọi `markItemUnavailable(3)` → Assert MenuItem.isAvailable=false, unavailableReason được lưu, markedUnavailableAt != null. Gọi thêm `getEMenu(101)` → Assert món id=3 không có trong kết quả.

#### `TC-UC17-005` — Bếp mở lại món → MenuItem.isAvailable=true
* **Severity:** MEDIUM | **Feature:** `KdsService.markItemAvailable()` | **File:** `KdsServiceUC17Test.java` | 🟢 GREEN
**Preconditions:** MenuItem id=3 isAvailable=false.
**Steps:** Gọi `markItemAvailable(3)` → Assert MenuItem.isAvailable=true, unavailableReason=null.

---

### 5. Red-Green-Refactor Tracker

| TC ID | Mô tả | Test File | 🔴 RED | 🔴 Commit | 🔴 Date | 🟢 GREEN | 🟢 Commit | 🟢 Date | 🔵 REFACTOR | 🔵 Commit | 🔵 Note |
|---|---|---|---|---|---|---|---|---|---|---|---|
| TC-UC17-001 | KDS active orders | `KdsServiceUC17Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract active status filter |
| TC-UC17-002 | PREPARING → READY | `KdsServiceUC17Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract status transition validator |
| TC-UC17-003 | Invalid transition → POS-012 | `KdsServiceUC17Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Centralize valid transitions map |
| TC-UC17-004 | Mark item unavailable | `KdsServiceUC17Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract MenuItem availability toggler |
| TC-UC17-005 | Mark item available | `KdsServiceUC17Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Reuse availability toggler |

---

### 6. Entry / Exit Criteria

#### Entry Criteria
- [x] DB có Order_Items (PENDING/PREPARING), Menu_Items
- [x] OrderItemRepository, MenuItemRepository mock sẵn sàng

#### Exit Criteria
- [x] Unit tests pass 100%
- [x] Status transition tests pass
- [x] MenuItem availability propagation tests pass

---

### 7. Rollback Plan

`git checkout -- src/main/java/com/hoanien/services/impl/KdsServiceImpl.java`