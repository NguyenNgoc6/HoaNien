**TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE** 
**Mẫu Đặc tả Kiểm thử Hướng Phát triển** 

**Document ID:** SWP391-G2-TDD-MOD3-UC16 
**Version:** 1.0 
**Date:** 2026-06-14 
**Status:** Approved 
**Author**: Trịnh Minh Đức
**Reviewed by:** Nguyễn Xuân Lưu
**Classification:** Internal Confidential 

**CHANGELOG**

| **Ngày** | **Người thực hiện** | **Nội dung thay đổi** |
|---|---|---|
| 2026-06-14 | Trịnh Minh Đức | Khởi tạo tài liệu TDD cho UC16 (Create Dine-In Order) |

---

**1. Thông tin Module**

| **Field** | **Value** |
|---|---|
| **Feature / Gap ID** | UC16 |
| **Module** | MOD3 POS |
| **Priority** | 🔴 P1 |
| **Data Classification** | Internal |

**2. Logic Issues Resolved**

| **#** | **Spec gốc (sai / thiếu)** | **Thực tế (code cũ)** | **Fix áp dụng trong test** |
|---|---|---|---|
| 1 | "F&B Staff specifies quantities" | Code cũ nhận `List<Long>` và hardcode `detail.setQuantity(1)` | Truyền DTO `OrderItemRequest` chứa quantity và lấy đúng giá trị `req.getQuantity()` để set. |

**3. Test Design Specification (TDS)**

**TDS-01 — Scope / Phạm vi**
Module bao gồm các layer: Service layer (`PosServiceImpl`) sử dụng Mockito để mock các Repositories (`FoodOrderRepository`, `FoodItemRepository`, v.v...).

**TDS-02 — Test Basis / Cơ sở Kiểm thử**
- SRS.md UC-19 (UC16 theo Master Table): Gọi món tại quầy Dine-In.

**TDS-03 — Test Conditions and Coverage Items**
| Condition ID | Test Condition | Coverage Item | Test Cases |
|---|---|---|---|
| TC-COND-001 | Gọi món với đầu vào hợp lệ | `createDineInOrder` | `MOD3-TC-005` |
| TC-COND-002 | Thanh toán hợp lệ | `payOrder` | `MOD3-TC-006` |
| TC-COND-003 | List món ăn trống hoặc null | `createDineInOrder` | `MOD3-TC-007` |
| TC-COND-004 | Số lượng món <= 0 | `createDineInOrder` | `MOD3-TC-008` |
| TC-COND-005 | Món ăn đã hết (isAvailable=false) | `createDineInOrder` | `MOD3-TC-009` |
| TC-COND-006 | Món ăn không tồn tại | `createDineInOrder` | `MOD3-TC-010` |
| TC-COND-007 | Bàn không tồn tại | `createDineInOrder` | `MOD3-TC-011` |

**TDS-04 — Test Techniques / Kỹ thuật Kiểm thử**
- **Equivalence Partitioning**: Kiểm tra mảng rỗng vs mảng có phần tử (TC-007).
- **Boundary Value Analysis**: Kiểm tra số lượng quantity <= 0 (TC-008).
- **Error Guessing**: Giả lập DB trả về Empty (TC-010, TC-011) hoặc trả về false (TC-009).

**4. Test Case Specification**

**MOD3-TC-005 — POS tạo order đúng bàn/món/giá**
- **Severity:** HIGH 
- **Feature Under Test:** `PosServiceImpl.createDineInOrder()` 
- **Test File:** `PosServiceUC16Test.java` 
- **TDD Phase**: 🟢 GREEN 
- **Preconditions:** 
  - Mock RestaurantTable (ID=1)
  - Mock 2 MenuItem (ID=101, 102) có trạng thái isAvailable = true.
- **Test Steps:**
  1. Arrange DTO requests với món 101 số lượng 2, món 102 số lượng 1.
  2. Act: Gọi `posService.createDineInOrder`.
  3. Assert kết quả trả về.
- **Expected Result (PASS)**: Order khác null, `OrderStatus` là "UNPAID", có đúng 2 details.
- **Current Status:** 🟢 Passing

**MOD3-TC-006 — POS thanh toán -> PAID**
- **Severity:** HIGH 
- **Feature Under Test:** `PosServiceImpl.payOrder()` 
- **Test File:** `PosServiceUC16Test.java` 
- **TDD Phase**: 🟢 GREEN 
- **Preconditions:** Mock `FoodOrder` tồn tại với ID=10, trạng thái "UNPAID".
- **Test Steps:**
  1. Gọi `posService.payOrder` với orderId=10, phương thức "CASH".
- **Expected Result (PASS)**: Order trả về khác null, `OrderStatus` được chuyển thành "PAID".
- **Current Status:** 🟢 Passing

**MOD3-TC-007 — Gọi món nhưng mảng requests rỗng**
- **Severity:** HIGH 
- **Feature Under Test:** `createDineInOrder()` 
- **TDD Phase**: 🟢 GREEN
- **Expected Result (PASS)**: Quăng `BusinessException` (POS-001).
- **Current Status:** 🟢 Passing

**MOD3-TC-008 — Gọi món nhưng truyền Quantity <= 0**
- **Severity:** HIGH 
- **Feature Under Test:** `createDineInOrder()` 
- **TDD Phase**: 🟢 GREEN
- **Expected Result (PASS)**: Quăng `BusinessException` (POS-004).
- **Current Status:** 🟢 Passing

**MOD3-TC-009 — Gọi món nhưng món đã hết (isAvailable=false)**
- **Severity:** HIGH 
- **Feature Under Test:** `createDineInOrder()` 
- **TDD Phase**: 🟢 GREEN
- **Expected Result (PASS)**: Quăng `BusinessException` (POS-002).
- **Current Status:** 🟢 Passing

**MOD3-TC-010 — ID món không tồn tại trong Menu**
- **Severity:** HIGH 
- **Feature Under Test:** `createDineInOrder()` 
- **TDD Phase**: 🟢 GREEN
- **Expected Result (PASS)**: Quăng `BusinessException` (ITEM_NOT_FOUND).
- **Current Status:** 🟢 Passing

**MOD3-TC-011 — ID Bàn không tồn tại**
- **Severity:** HIGH 
- **Feature Under Test:** `createDineInOrder()` 
- **TDD Phase**: 🟢 GREEN
- **Expected Result (PASS)**: Quăng `BusinessException` (TABLE_NOT_FOUND).
- **Current Status:** 🟢 Passing

**5. Red-Green-Refactor Tracker**

| **TC ID** | **Test File** | **🔴 RED confirmed** | **🟢 GREEN (commit)** | **🔵 REFACTOR note** |
|---|---|---|---|---|
| MOD3-TC-005 | PosServiceUC16Test.java | [x] | [x] | Thay đổi tham số `List<Long>` thành `List<OrderItemRequest>` để truyền đúng Quantity. |
| MOD3-TC-006 | PosServiceUC16Test.java | [x] | [x] | Không |
| MOD3-TC-007 | PosServiceUC16Test.java | [x] | [x] | Bổ sung bắt lỗi mảng rỗng (POS-001) |
| MOD3-TC-008 | PosServiceUC16Test.java | [x] | [x] | Bổ sung bắt lỗi Quantity <= 0 (POS-004) |
| MOD3-TC-009 | PosServiceUC16Test.java | [x] | [x] | Bổ sung bắt lỗi món hết (POS-002) |
| MOD3-TC-010 | PosServiceUC16Test.java | [x] | [x] | Bổ sung bắt lỗi ID món (ITEM_NOT_FOUND) |
| MOD3-TC-011 | PosServiceUC16Test.java | [x] | [x] | Bổ sung bắt lỗi ID Bàn (TABLE_NOT_FOUND) |

**6. Entry / Exit Criteria**

**Exit Criteria (Điều kiện kết thúc — DoD)**
- [x] npm test (mvn test) — tất cả unit tests xanh (không có skip).
- [x] Test coverage bao phủ luồng gọi món thành công và luồng thanh toán.
