**ENGINEERING DOCUMENTATION STANDARD (EDS)** 
**v2.0** 
**Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa** 

**Field** | **Value**
---|---
**Document ID** | SWP391-G2-MOD3-IMP-UC16
**Version** | 1.0
**Date** | 2026-06-14
**Status** | Approved
**Document Owner** | Nhóm 2 SWP391
**Author** | Trịnh Minh Đức
**Reviewed by** | Nguyễn Xuân Lưu
**Approved by** | Principal Architect
**Last Review** | 2026-06-14
**Based on EDS** | v2.0

**CHANGELOG**

| **Ngày** | **Người thực hiện** | **Nội dung thay đổi** |
|---|---|---|
| 2026-06-14 | Trịnh Minh Đức | Tạo tài liệu lần đầu, đặc tả UC16 (Create Dine-In Order) dựa trên code thực tế. |

---

**1. Tổng quan Module**

Mô tả: Module xử lý nghiệp vụ tạo đơn hàng tại quầy (Dine-In), map với bàn thực tế và chuyển trạng thái (KOT) xuống nhà bếp.

| **Field** | **Value** |
|---|---|
| **Module Name** | MOD3 POS |
| **Bounded Context** | F&B / Restaurant POS |
| **Data Classification** | Internal |
| **Compliance Scope** | N/A |
| **Upstream Dependencies** | Mod 1 (Table Management), Mod 1 (Menu Management) |
| **Downstream Consumers** | Mod 3 (Kitchen KDS), Mod 5 (Billing) |

**2. Ma trận Truy vết (Traceability Matrix)**

| **ID** | **Loại** | **Mô tả yêu cầu** | **Thành phần Code** | **Compliance Target** | **ADR liên quan** |
|---|---|---|---|---|---|
| UC16 | Use Case | Tạo đơn hàng Dine-In (Gọi món tại quầy) | `PosServiceImpl.createDineInOrder()` | N/A | ADR-001 |
| BR-FB-02 | Business Rule | Món hết không được order | `PosServiceImpl.getAvailableMenuItem()` | N/A | |

**3. Architecture Decision Records (ADR)**

**ADR-001 — Sử dụng OrderItemRequest DTO thay vì List ID cho Quantity**

- **Status**: Accepted
- **Date**: 2026-06-14
- **Bối cảnh (Context)**: Thiết kế ban đầu hàm `createDineInOrder` chỉ nhận mảng ID `List<Long> menuItemIds`, dẫn đến việc bị hardcode `Quantity = 1` và tạo ra các dòng dữ liệu trùng lặp trong hóa đơn nếu khách gọi số lượng nhiều. 
- **Quyết định (Decision)**: Tạo DTO `OrderItemRequest` chứa `menuItemId` và `quantity`. 
- **Hệ quả (Consequences)**: Khớp 100% với đặc tả "F&B Staff specifies quantities", tối ưu dung lượng DB, hiển thị gọn gàng trên KDS Bếp.

**4. Static Modeling (Mô hình Tĩnh)**

Các thành phần chính tham gia vào Use Case:
- **Interfaces**: `PosService`
- **Services**: `PosServiceImpl`
- **Entities**: `FoodOrder`, `FoodOrderDetail`, `RestaurantTable`, `MenuItem`
- **DTOs**: `OrderItemRequest`

**5. Interface Specification (Đặc tả Giao diện)**

```java
// PosService.java
/**
 * Creates a new dine-in order for a specific table with a list of menu items.
 * @throws BusinessException if item list is empty, table not found, or item unavailable.
 */
com.kawai.models.FoodOrder createDineInOrder(Long tableId, java.util.List<com.kawai.dto.OrderItemRequest> requests);

com.kawai.models.FoodOrder payOrder(Long orderId, String paymentMethod);
```

**6. Bảng mã lỗi (Error Codes)**

| **Code** | **HTTP Status** | **Message (VI)** | **Trigger Condition** |
|---|---|---|---|
| POS-001 | 400 | Đơn hàng trống — không có món | List `requests` bị null hoặc empty |
| POS-002 | 400 | Món đã hết — không thể order | `item.getIsAvailable()` trả về false |
| POS-004 | 400 | Số lượng món phải lớn hơn 0 | `req.getQuantity() <= 0` hoặc null |
| ITEM_NOT_FOUND | 404 | Món không tồn tại | Không tìm thấy `menuItemId` trong DB |
