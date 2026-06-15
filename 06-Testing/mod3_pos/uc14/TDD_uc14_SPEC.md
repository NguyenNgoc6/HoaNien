# TEST-DRIVEN DEVELOPMENT SPECIFICATION
## UC14 — Room Service & E-Menu (RoomServiceOrderService)

| Field | Value |
|-------|-------|
| **Document ID** | `HOANIEN-TDD-MOD3-UC14-001` |
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
| **Feature / Gap ID** | `GAP-MOD3-UC14` |
| **Module** | POS — Room Service & E-Menu (UC14) |
| **Use Case** | UC14: Khách quét QR → xem E-Menu → đặt Room Service; xác thực phòng OCCUPIED |
| **Spec gốc** | `SRS_Document_SWP391_G2.md` |
| **Priority** | 🟡 P1 |
| **Sprint** | S2 (2026-06-23 → 2026-07-07) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Internal |
| **Upstream Dependencies** | Module 2 — Booking (Room status) |
| **Downstream Consumers** | UC17 (KDS), UC18 (Post to Room) |

---

### 2. Logic Issues Resolved

| # | Spec gốc | Thực tế | Fix áp dụng trong test |
|---|----------|---------|------------------------|
| **L1** | Không kiểm tra trạng thái phòng khi đặt Room Service | Phải kiểm tra Room.status = OCCUPIED trước khi tạo order | Test mock Room.status=AVAILABLE → POS-002 |
| **L2** | Không kiểm tra món hết hàng | MenuItem.isAvailable = false → từ chối | Test mock MenuItem.isAvailable=false → POS-005 |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi
Logic `RoomServiceOrderService.getEMenu()`, `createRoomServiceOrder()`.

#### TDS-02 — Test Basis

| Source | Items Derived |
|--------|---------------|
| `SRS.md` UC14 | E-Menu, Room Service ordering |
| BR-POS-04 | Chỉ phòng OCCUPIED được đặt Room Service |

#### TDS-03 — Test Conditions

| Condition ID | Test Condition | Coverage Item | Test Cases |
|-------------|----------------|---------------|------------|
| TC-COND-UC14-001 | Khách quét QR → E-Menu load thành công | `getEMenu()` | TC-UC14-001 |
| TC-COND-UC14-002 | Đặt Room Service phòng OCCUPIED → tạo order | `createRoomServiceOrder()` | TC-UC14-002 |
| TC-COND-UC14-003 | Phòng không OCCUPIED → POS-002 | `createRoomServiceOrder()` | TC-UC14-003 |
| TC-COND-UC14-004 | Món hết hàng → POS-005 | `createRoomServiceOrder()` | TC-UC14-004 |
| TC-COND-UC14-005 | RoomId không tồn tại → POS-001 | `getEMenu()` | TC-UC14-005 |

---

### 4. Test Case Specification

#### `TC-UC14-001` — Quét QR phòng 101 → trả E-Menu đúng danh sách món available
* **Severity:** MEDIUM | **Feature:** `RoomServiceOrderService.getEMenu()` | **File:** `RoomServiceOrderServiceUC14Test.java` | 🟢 GREEN
**Preconditions:** Room id=101 tồn tại. DB có 5 MenuItem, trong đó 3 isAvailable=true.
**Steps:** Gọi `getEMenu(101)` → Assert list.size()=3, tất cả items có isAvailable=true.

#### `TC-UC14-002` — Đặt Room Service phòng OCCUPIED → Order PENDING được tạo
* **Severity:** HIGH | **Feature:** `RoomServiceOrderService.createRoomServiceOrder()` | **File:** `RoomServiceOrderServiceUC14Test.java` | 🟢 GREEN
**Preconditions:** Room id=101 có status=OCCUPIED. MenuItem id=1 isAvailable=true, price=120000.
**Steps:** Gọi `createRoomServiceOrder({roomId:101, items:[{menuItemId:1, qty:2}]})` → Assert orderId != null, orderStatus=PENDING, totalPrice=240000, OrderItems count=1.

#### `TC-UC14-003` — Room Service cho phòng không OCCUPIED → RoomNotOccupedException (POS-002)
* **Severity:** HIGH | **Feature:** `RoomServiceOrderService.createRoomServiceOrder()` | **File:** `RoomServiceOrderServiceUC14Test.java` | 🟢 GREEN
**Preconditions:** Room id=102 có status=AVAILABLE.
**Steps:** Gọi `createRoomServiceOrder({roomId:102, items:[...]})` → throws `RoomNotOccupedException` với code POS-002.

#### `TC-UC14-004` — Đặt món hết hàng (isAvailable=false) → MenuItemNotAvailableException (POS-005)
* **Severity:** MEDIUM | **Feature:** `RoomServiceOrderService.createRoomServiceOrder()` | **File:** `RoomServiceOrderServiceUC14Test.java` | 🟢 GREEN
**Preconditions:** Room id=101 OCCUPIED. MenuItem id=5 isAvailable=false.
**Steps:** Gọi `createRoomServiceOrder({roomId:101, items:[{menuItemId:5, qty:1}]})` → throws `MenuItemNotAvailableException` với code POS-005.

#### `TC-UC14-005` — RoomId không tồn tại → RoomNotFoundException (POS-001)
* **Severity:** LOW | **Feature:** `RoomServiceOrderService.getEMenu()` | **File:** `RoomServiceOrderServiceUC14Test.java` | 🟢 GREEN
**Steps:** Gọi `getEMenu(9999)` → throws `RoomNotFoundException` với code POS-001.

---

### 5. Red-Green-Refactor Tracker

| TC ID | Mô tả | Test File | 🔴 RED | 🔴 Commit | 🔴 Date | 🟢 GREEN | 🟢 Commit | 🟢 Date | 🔵 REFACTOR | 🔵 Commit | 🔵 Note |
|---|---|---|---|---|---|---|---|---|---|---|---|
| TC-UC14-001 | QR scan → E-Menu | `RoomServiceOrderServiceUC14Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract menu availability filter |
| TC-UC14-002 | Room Service OCCUPIED → Order | `RoomServiceOrderServiceUC14Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract order total price calculator |
| TC-UC14-003 | Not OCCUPIED → POS-002 | `RoomServiceOrderServiceUC14Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Standardize room status validation |
| TC-UC14-004 | Hết món → POS-005 | `RoomServiceOrderServiceUC14Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract menu item availability check |
| TC-UC14-005 | Room not found → POS-001 | `RoomServiceOrderServiceUC14Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Standardize not-found exception |

---

### 6. Entry / Exit Criteria

#### Entry Criteria
- [x] Database đã có dữ liệu synthetic: Rooms (OCCUPIED/AVAILABLE), MenuItems
- [x] RoomRepository, MenuItemRepository mock sẵn sàng

#### Exit Criteria
- [x] Unit tests pass 100%
- [x] Room status validation tests pass
- [x] Menu availability tests pass

---

### 7. Rollback Plan

`git checkout -- src/main/java/com/hoanien/services/impl/RoomServiceOrderServiceImpl.java`