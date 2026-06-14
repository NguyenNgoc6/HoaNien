# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0
## Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa

| Field | Value |
| --- | --- |
| **Document ID** | `KAWAI-MOD5-IMP-001` |
| **Version** | 1.0 |
| **Date** | 2026-06-12 |
| **Status** | Approved |
| **Document Owner** | Nguyễn Xuân Lưu |
| **Author** | Nguyễn Xuân Lưu - Tech Lead |

---

### CHANGELOG
| Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|
| 2026-06-14 | Ngo Thi Ngoc Lan | Đặc tả API endpoint cho UC26.1 và UC26.2 trong Module 5 |

---

### MỤC LỤC
1. [Tổng quan Module](#1-tong-quan-module)
2. [Ma trận Truy vết (Traceability Matrix)](#2-ma-tran-truy-vet-traceability-matrix)
3. [Architecture Decision Records (ADR)](#3-architecture-decision-records-adr)
4. [Non-Functional Requirements & SLA](#4-non-functional-requirements--sla)
5. [Static Modeling (Mô hình Tĩnh)](#5-static-modeling-mo-hinh-tinh)
6. [Dynamic Modeling (Mô hình Động)](#6-dynamic-modeling-mo-hinh-dong)
7. [Domain Event Catalog](#7-domain-event-catalog)
8. [Interface Specification (Đặc tả Giao diện)](#8-interface-specification-dac-ta-giao-dien)
9. [API Specification](#9-api-specification)
10. [Bảng mã lỗi (Error Codes)](#10-bang-ma-loi-error-codes)
11. [Quy trình Triển khai (Step-by-Step)](#11-quy-trinh-trien-khai-step-by-step)
12. [Rollback & Incident Runbook](#12-rollback--incident-runbook)
13. [Kịch bản Kiểm thử Chi tiết](#13-kich-ban-kiem-thu-chi-tiet)
14. [Phương pháp Xác minh](#14-phuong-phap-xac-minh)
15. [Mẫu thử thực tế (API Verification Samples)](#15-mau-thu-thuc-te-api-verification-samples)
16. [Bảng tổng hợp phân quyền (Authorization Matrix)](#16-bang-tong-hop-phan-quyen-authorization-matrix)
17. [Phụ lục](#17-phu-luc)

---

### 1. Tổng quan Module
Kiểm toán đêm (Night Audit), chốt doanh thu, và hóa đơn tổng hợp (Folio).

---

### 2. Ma trận Truy vết (Traceability Matrix)
| Requirement ID | Loại | Mô tả yêu cầu | Thành phần Code |
| --- | --- | --- | --- |
| BR-FIN-01 | Business Rule | Night audit chốt sổ 2h sáng | `NightAuditScheduler.run()` |

---

### 3. Architecture Decision Records (ADR)
#### ADR-001 — Lập lịch Night Audit
**Bối cảnh:** Cần cộng tiền phòng vào 2h sáng mỗi đêm.
**Quyết định:** Sử dụng Spring `@Scheduled(cron = "0 0 2 * * ?")`.
**Hệ quả:** Đơn giản, tuy nhiên cần thiết lập Redis lock để tránh chạy 2 lần nếu có 2 server node.

---

### 4. Non-Functional Requirements & SLA
| Category | Requirement | Target SLA |
| --- | --- | --- |
| **Latency** | Night Audit completion | < 5 phút cho 1000 phòng |

---

### 5. Static Modeling (Mô hình Tĩnh)
#### 5.1. Class Diagram
```plantuml
@startuml
class NightAuditScheduler {
  +runAudit()
}
@enduml
```

---

### 6. Dynamic Modeling (Mô hình Động)
#### 6.1. Sequence Diagram
```plantuml
@startuml
NightAuditScheduler -> FolioService: applyDailyCharges()
@enduml
```

---

### 7. Domain Event Catalog
| Event Name | Publisher | Subscriber | Action |
| --- | --- | --- | --- |
| `NightAuditCompleted` | `NightAuditScheduler` | `ReportService` | Tạo báo cáo doanh thu |

---

### 8. Interface Specification (Đặc tả Giao diện)
`IFolioService` with `checkout()`

---

### 9. API Specification

### 🔹 API 001: Kích hoạt Night Audit thủ công
- **HTTP Method:** `POST`
- **URL Path:** `/api/v1/audit/run`
- **Authorization:** ADMIN only
- **Request Body:** _(empty)_
- **Response 200:**
```json
{ "status": "success", "message": "Night audit đã chạy xong" }
```

---

### 🔹 API 002: Lấy dữ liệu biểu đồ tài chính (UC26.1)
- **HTTP Method:** `GET`
- **URL Path:** `/api/v1/report/dashboard`
- **Authorization:** MANAGER only (`hasRole('MANAGER')`)
- **Request Body:** _(empty — GET request)_
- **Query Params:** _(none — vế tất cả folio hiện tại)_
- **Response 200 OK:**
```json
{
  "ROOM": 4000000,
  "FB": 500000,
  "TOUR": 800000
}
```
- **Response 403 Forbidden:** Khi caller không có role MANAGER.
- **Response 500 Internal Server Error:** Lỗi hệ thống (SYS-001).
- **Business Rules:** BR-FIN-04 (phân tách USALI), BR-FIN-05 (BigDecimal HALF_UP), BR-SYS-06 (RBAC).
- **Service Method:** `ReportServiceImpl.getDashboardFinancialData()`

---

### 🔹 API 003: Lấy Occupancy Rate (UC26.2)
- **HTTP Method:** `GET`
- **URL Path:** `/api/v1/report/occupancy`
- **Authorization:** MANAGER only (`hasRole('MANAGER')`)
- **Request Body:** _(empty — GET request)_
- **Query Params:** _(none)_
- **Response 200 OK:**
```json
{
  "occupancyRate": 75.0,
  "unit": "%"
}
```
> **Edge case:** Khi Resort chưa có phòng nào, API trả `"occupancyRate": 0.0` — không ném lỗi.
- **Response 403 Forbidden:** Khi caller không có role MANAGER.
- **Response 500 Internal Server Error:** Lỗi hệ thống (SYS-001).
- **Business Rules:** BR-SYS-06 (RBAC).
- **Service Method:** `ReportServiceImpl.getOccupancyRate()`

---

### 10. Bảng mã lỗi (Error Codes)
| Code | HTTP Status | Message (VI) | Trigger Condition |
| --- | --- | --- | --- |
| `FIN-001` | 400 | Đã audit trong ngày | BusinessDate đã update |
| `RPT-001` | 403 | Không đủ quyền xem Dashboard | Caller không có role MANAGER |
| `RPT-002` | 403 | Không đủ quyền xem Occupancy Rate | Caller không có role MANAGER |
| `SYS-001` | 500 | Lỗi hệ thống nội bộ | Exception không xử lý được |

---

### 11. Quy trình Triển khai (Step-by-Step)
# Triển khai bảng FolioItem

---

### 12. Rollback & Incident Runbook
Rollback Folio balances.

---

### 13. Kịch bản Kiểm thử Chi tiết
**TC-UNIT-001 — Night Audit Success**
- **Scenario:** 5 phòng đang Occupied.
- **Expected:** Giá tiền được cộng thêm vào Folio tương ứng.

---

### 14. Phương pháp Xác minh
Kiểm tra bảng Business_Date.

---

### 15. Mẫu thử thực tế (API Verification Samples)
```bash
# API 001 — Night Audit thủ công
curl -X POST /api/v1/audit/run \
     -H "Authorization: Bearer <ADMIN_TOKEN>"

# API 002 — Dashboard tài chính (UC26.1)
curl -X GET /api/v1/report/dashboard \
     -H "Authorization: Bearer <MANAGER_TOKEN>"
# Expected: {"ROOM": 4000000, "FB": 500000, "TOUR": 800000}

# API 003 — Occupancy Rate (UC26.2)
curl -X GET /api/v1/report/occupancy \
     -H "Authorization: Bearer <MANAGER_TOKEN>"
# Expected: {"occupancyRate": 75.0, "unit": "%"}
```

---

### 16. Bảng tổng hợp phân quyền (Authorization Matrix)
| Endpoint | GUEST | CUSTOMER | RECEPTIONIST | MANAGER | ADMIN |
| --- | :---: | :---: | :---: | :---: | :---: |
| POST `/audit/run` | ❌ | ❌ | ❌ | ❌ | ✔️ |
| GET `/report/dashboard` | ❌ | ❌ | ❌ | ✔️ | ✔️ |
| GET `/report/occupancy` | ❌ | ❌ | ❌ | ✔️ | ✔️ |

---

### 17. Phụ lục
**Night Audit:** Chốt sổ qua đêm.

**getDashboardFinancialData():** Gom doanh thu từ bảng `Folio_Items`, nhóm theo `source_department`, cộng dồn bằng `BigDecimal` (BR-FIN-04, BR-FIN-05).

**getOccupancyRate():** Tử vấn `Room` table qua `findOccupied()` và `countTotalRooms()`, tính tỷ lệ %, guard chia cho 0.
