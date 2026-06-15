# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0

## Quy chuẩn Tài liệu Kỹ thuật và Đặc tả Hiện thực hóa

| Field                    | Value                                                                                       |
| ------------------------ | ------------------------------------------------------------------------------------------- |
| **Document ID**    | `BOOKING-PAY-IMP-001`                                                            |
| **Version**        | 1.0                                                                                         |
| **Date**           | 2026-06-15                                                                                  |
| **Status**         | Draft                                                   |
| **Document Owner** | Backend Team                                                    |
| **Author**         | Backend Developer                                                                           |
| **Reviewed by**    | Tech Lead                                                                             |
| **DPO Sign-off**   | `[ ] Pending` |
| **Approved by**    | Principal Architect                                                                   |
| **Last Review**    | 2026-06-15                                    |
| **Based on EDS**   | v2.0                                                                                        |

---

### CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ. Mọi thay đổi phải ghi vào bảng này.

| Ngày      | Người thực hiện | Nội dung thay đổi       |
| ---------- | ------------------- | -------------------------- |
| 2026-06-15 | Backend Developer  | Tạo tài liệu lần đầu - Đặc tả chức năng Payment bằng VNPAY cho UC-10 Book Room |
| 2026-06-15 | `Chu Xuân Dũng`    | Cập nhật ma trận phân quyền: bổ sung quyền gọi Webhook IPN cho VNPAY SYSTEM |

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

---

### 1. Tổng quan Module

Mô tả ngắn gọn: Module thực thi việc tạo giao dịch thanh toán tiền cọc (deposit) thông qua Cổng thanh toán VNPAY dành cho chức năng **UC-10 Book Room**. Module chịu trách nhiệm sinh URL thanh toán theo đặc tả chuẩn của VNPAY, nhận phản hồi từ webhook (IPN), cập nhật trạng thái Booking và tự động hủy sau 10 phút nếu thanh toán thất bại/chưa thanh toán. Ngoài ra, module bổ sung các use case mở rộng như Payment Retry, Reconciliation và Refund để đảm bảo tính toàn vẹn và linh hoạt trong thanh toán.

| Field                           | Value                                                  |
| ------------------------------- | ------------------------------------------------------ |
| **Module Name**           | `Booking Payment VNPAY`                                      |
| **Bounded Context**       | `Booking & Payment`                                           |
| **Data Classification**   | Confidential / PII |
| **Compliance Scope**      | PCI-DSS (Delegate via Gateway) / N/A                               |
| **Upstream Dependencies** | `Booking Module`                               |
| **Downstream Consumers**  | `Notification Module`, `VNPAY Gateway`                               |

---

### 2. Ma trận Truy vết (Traceability Matrix)

| Requirement ID | Loại (BR/ADR/US) | Mô tả yêu cầu | Thành phần Code                    | Compliance Target | ADR liên quan |
| -------------- | ----------------- | ----------------- | ------------------------------------ | ----------------- | -------------- |
| UC-10          | User Story     | Chuyển hướng VNPay đặt cọc | `VnpayService.createPaymentUrl()` | — | ADR-001 |
| UC-PAY-04      | User Story     | Payment Retry / Regenerate QR | `VnpayService.createPaymentUrl()` | — | — |
| UC-PAY-05      | Alternative Flow | Payment Expiration (Auto Cancel sau 10 phút) | `BookingTimeoutJob` | Giải phóng tài nguyên | ADR-002 |
| UC-PAY-06      | Business Rule  | Payment Reconciliation | `PaymentReconciliationJob` | Data Integrity | — |
| UC-PAY-07      | User Story     | Refund / Void (hủy đơn sau khi pay) | `VnpayService.refund()` | Customer Rights | — |
| VNPAY-IPN      | Business Rule  | IPN Webhook & Checksum HM512 | `VnpayService.verifyIpn()`         | Data Integrity | ADR-001 |
| UC-10 E1       | Exception      | Payment Failure -> Cancel   | `VnpayService.verifyIpn()`         | — | — |
| BR-PAY-01      | Business Rule  | Mỗi PaymentTransaction chỉ được update SUCCESS đúng 1 lần | `PaymentService.updateStatus()` | Idempotency | — |

---

### 3. Architecture Decision Records (ADR)

#### ADR-001 — Sử dụng VNPAY làm cổng thanh toán chính (Version 2.1.0)

| Field                | Value                                           |
| -------------------- | ----------------------------------------------- |
| **Status**     | Accepted   |
| **Deciders**   | Tech Lead, System Architect |
| **Date**       | 2026-06-15                                      |

**Bối cảnh (Context)**
Cần hỗ trợ thanh toán online trong chức năng Book Room, giúp hệ thống tự động xác nhận đơn hàng không cần thao tác thủ công, yêu cầu hỗ trợ thẻ nội địa và QR Code.

**Quyết định (Decision)**
Lựa chọn tích hợp VNPAY theo tài liệu `VNPAY Payment Gateway_Techspec Post method 2.1.0-VN.md`, sử dụng checksum bảo mật `HMACSHA512` thay vì các thuật toán cũ.

**Hệ quả (Consequences)**
* Tích cực: Thanh toán tiện lợi, chuẩn mã hóa mới nhất. Hệ thống không lưu trữ thông tin thẻ ngân hàng của user (delegation to VNPAY).
* Tiêu cực / Trade-offs: Cần expose 1 webhook public (`/vnpay-ipn`) để VNPAY gửi request về. Đòi hỏi cơ chế IP Whitelist hoặc xác thực signature chặt chẽ để tránh bị giả mạo thanh toán.

#### ADR-002 — Soft-lock phòng khi chờ thanh toán

| Field                | Value                                           |
| -------------------- | ----------------------------------------------- |
| **Status**     | Accepted   |
| **Deciders**   | Tech Lead |
| **Date**       | 2026-06-15                                      |

**Bối cảnh (Context)**
Cần ngăn chặn việc overbooking khi có 2 khách hàng đồng thời chọn chung 1 phòng trong lúc chưa hoàn tất thanh toán ở VNPay.

**Quyết định (Decision)**
Khi khách hàng bấm Book, hệ thống tạo Booking (trạng thái `PENDING`) và tạo các `Room_Booking_Details` tương ứng, đồng thời trigger bộ đếm thời gian (10 phút). Nếu quá 10 phút chưa thanh toán thành công, hệ thống chuyển sang `CANCELLED` và giải phóng inventory phòng.

---

### 4. Non-Functional Requirements & SLA

#### 4.1. Performance & Availability

| Category               | Requirement         | Target SLA | Measurement Method |
| ---------------------- | ------------------- | ---------- | ------------------ |
| **Latency**      | API trả URL thanh toán (p99) | < 300ms | Load test       |
| **Availability** | Webhook (IPN)       | 99.9%      | Uptime monitor     |

#### 4.2. Data Integrity & Retention

| Category              | Requirement            | Target  | Verification Method |
| --------------------- | ---------------------- | ------- | ------------------- |
| **Consistency** | IPN checksum validation| 100% | Unit tests & Integration tests |
| **Durability**  | Ghi nhận TransactionLog| RPO = 0 | Transaction log     |

#### 4.3. Security

| Category                        | Requirement   | Target          | Verification Method   |
| ------------------------------- | ------------- | --------------- | --------------------- |
| **Checksum Integrity**    | HMACSHA512    | Validated       | Security Code Audit |
| **Idempotency** | Xử lý trùng lặp IPN request | Strictly Enforced | Stress test webhook     |

---

### 5. Static Modeling (Mô hình Tĩnh)

#### 5.1. Data Structure (Prisma Schema / Logical Model)

```prisma
// Lược đồ tham khảo
model Booking {
  id              String   @id @default(uuid())
  customerId      String
  totalPrice      Decimal
  depositAmount   Decimal
  status          String   // PENDING, CONFIRMED, CANCELLED
  paymentMethod   String?  // 'VNPAY'
  createdAt       DateTime @default(now())
  expiresAt       DateTime // createdAt + 10 minutes
  paidAt          DateTime? // Được set khi IPN SUCCESS
  transactions    PaymentTransaction[]
}

model PaymentTransaction {
  id              String   @id @default(uuid())
  bookingId       String
  vnpTxnRef       String   @unique // Mã giao dịch của merchant (vnp_TxnRef)
  vnpTransactionNo String?          // Mã của VNPAY trả về
  amount          Decimal
  status          String   // INIT, SUCCESS, FAILED
  ipnData         Json?    // Dữ liệu gốc từ VNPAY IPN
  createdAt       DateTime @default(now())
  booking         Booking  @relation(fields: [bookingId], references: [id])
}
```

---

### 6. Dynamic Modeling (Mô hình Động)

#### 6.1. Sequence Diagram — Happy Path (PlantUML)

```plantuml
@startuml
actor "Customer" as Client
participant "BookingController" as Controller
participant "BookingService" as Service
participant "VNPayService" as VNP
database "Database" as DB
participant "VNPAY System" as Gateway

Client -> Controller: POST /api/bookings
activate Controller
Controller -> Service: createBooking(data)
Service -> DB: Save Booking (PENDING), Lock Room
Service -> VNP: buildPaymentUrl(amount, bookingId, ip)
VNP -> VNP: Sign with HMACSHA512
VNP --> Service: vnp_Url
Service --> Controller: vnp_Url
Controller --> Client: HTTP 200 (redirect VNPay URL)
deactivate Controller

Client -> Gateway: Thực hiện thanh toán tại VNPay
Gateway -> Gateway: Xử lý thẻ/QR Code
Gateway -> Controller: GET /api/payments/vnpay-ipn
activate Controller
Controller -> VNP: verifyIpn(queryParams)
VNP -> VNP: Check HMACSHA512 signature
VNP -> Service: updateBookingStatus(bookingId, amount, txnRef)
Service -> DB: Update Booking -> CONFIRMED
Service --> Controller: Success
Controller --> Gateway: HTTP 200 {"RspCode": "00"}
deactivate Controller
@enduml
```

#### 6.2. State Machine

```plantuml
@startuml
[*] --> PENDING : [Tạo Booking]

state PENDING {
}
note right of PENDING
  Phòng bị soft-lock. Hệ thống đợi IPN hoặc Timeout.
end note

PENDING --> CONFIRMED : [VNPAY IPN Success (RspCode 00)]
PENDING --> CANCELLED : [Timeout > 10 mins (UC-PAY-05)]
PENDING --> PENDING : [Payment Retry (UC-PAY-04) - Tạo QR/Transaction mới]
PENDING --> CANCELLED : [VNPAY IPN Failed (RspCode != 00)]

CONFIRMED --> REFUNDED : [Refund / Void (UC-PAY-07)]
CANCELLED --> [*]
REFUNDED --> [*]
@enduml
```

---

### 7. Domain Event Catalog

#### 7.1. Events Published (Phát ra)

| Event Name         | Trigger               | Publisher     | Subscriber(s)            | Payload Schema     | Async? |
| ------------------ | --------------------- | ------------- | ------------------------ | ------------------ | ------ |
| `BookingPending` | Khi user chọn Book Room| BookingService| TimerService | `bookingId`, `expiresAt` | Yes |
| `PaymentSuccess` | Khi VNPay IPN hợp lệ (RspCode 00) | VNPayService| NotificationService | `bookingId`, `vnpTxnRef` | Yes |
| `BookingExpired` | Khi TimerTask báo timeout | TimerService | BookingService, RoomInventory | `bookingId` | Yes |
| `PaymentRefunded`| Khi user cancel sau khi đã pay | VNPayService | BookingService, NotificationService | `bookingId`, `refundTxnRef` | Yes |

---

### 8. Interface Specification (Đặc tả Giao diện)

#### 8.1. Service Interface

```typescript
// IVNPayService.ts
// @version 1.0

export interface CreatePaymentInput {
  vnp_TxnRef: string; // ID tham chiếu từ DB (bookingId + time/random suffix)
  amount: number;     // Số tiền VND nguyên (chưa nhân 100, hàm tự nhân)
  ipAddr: string;
  orderInfo: string;
}

export interface IpnResponse {
  RspCode: string;
  Message: string;
}

export interface IVNPayService {
  /**
   * Sinh URL redirect khách hàng qua cổng VNPAY
   */
  createPaymentUrl(input: CreatePaymentInput): string;

  /**
   * Xử lý webhook gọi về từ VNPay (IPN)
   */
  processIpn(query: Record<string, string>): Promise<IpnResponse>;

  /**
   * Truy vấn trạng thái giao dịch từ VNPay (Dùng cho Reconciliation - UC-PAY-06)
   */
  queryTransaction(vnpTxnRef: string, transDate: string): Promise<any>;

  /**
   * Hoàn tiền giao dịch VNPay (Refund / Void - UC-PAY-07)
   */
  refundTransaction(vnpTxnRef: string, amount: number, transType: string, user: string): Promise<any>;
}
```

---

### 9. API Specification

#### 9.1. Endpoints Table

| Method | Path                       | Auth Level | Rate Limit | Idempotent? |
| ------ | -------------------------- | ---------- | ---------- | ----------- |
| GET    | `/api/v1/payments/vnpay-return` | None | 100/min    | Yes         |
| GET    | `/api/v1/payments/vnpay-ipn`    | None | 500/min    | Yes         |

**Lưu ý:**
- IPN URL là endpoint public cho VNPay ping vào. Phải thiết kế Idempotent (kiểm tra trạng thái cũ, không process lại IPN đã success).

---

### 10. Bảng mã lỗi (Error Codes)

Dựa theo VNPAY spec và mapping hệ thống:

| Code          | HTTP Status | Message (EN)             | Message (VI)              | Trigger Condition  |
| ------------- | ----------- | ------------------------ | ------------------------- | ------------------ |
| `PAY-001` | 400         | Invalid Checksum         | Chữ ký không hợp lệ | Checksum VNPay không khớp |
| `PAY-002` | 404         | Order Not Found          | Đơn hàng không tồn tại | vnp_TxnRef không có trong DB |
| `PAY-003` | 400         | Invalid Amount           | Số tiền không hợp lệ | vnp_Amount khác với giá trị trong DB |
| `PAY-004` | 400         | Order Already Confirmed  | Đơn hàng đã xử lý | IPN đến lần 2 cho cùng giao dịch |

---

### 11. Quy trình Triển khai (Step-by-Step)

#### 11.1. Prerequisites
- Đã đăng ký tài khoản VNPAY Sandbox (TmnCode, HashSecret).
- Môi trường đã sẵn sàng biến môi trường `VNPAY_TMN_CODE`, `VNPAY_HASH_SECRET`, `VNPAY_URL`.

#### 11.2. Implementation Steps
1. Khai báo các field model trong DB (Booking bổ sung status, tạo PaymentTransaction).
2. Xây dựng utility VNPAY Hashing sử dụng `crypto.createHmac('sha512', secret)`. Đảm bảo chuỗi tham số được sắp xếp theo đúng a-z.
3. Cài đặt IPN webhook, map các `RspCode` VNPAY ("00", "01", "02", "97"...) đúng theo tài liệu.

---

### 12. Rollback & Incident Runbook

#### 12.1. Điều kiện kích hoạt Rollback (Trigger Conditions)
- Người dùng bấm chuyển qua VNPay bị lỗi 4xx liên tục, báo Checksum sai.
- Webhook IPN chết, đơn chuyển qua VNPAY đã trả tiền nhưng Local không cập nhật.

#### 12.2. Rollback Procedure
- Nếu lỗi checksum: Quay lại commit version API cũ, verify lại secret key config.
- Nếu lỗi IPN không update: Chạy job Reconciliation truy vấn kết quả VNPay (API `querydr`) và chạy tay (manual update).

---

### 13. Kịch bản Kiểm thử Chi tiết
(Tham khảo chi tiết tại TDD Spec: `BOOKING-TDD-001`)

---

### 14. Phương pháp Xác minh
- Kiểm tra Log: Verify VNPAY raw URL và URL params đã được URI Encode đúng chuẩn trước khi hash.
- Kiểm thử VNPay Sandbox bằng test card (ATM Nội địa, thẻ test VNPAY cấp).

---

### 15. Bảng tổng hợp phân quyền (Authorization Matrix)

| Endpoint                          | GUEST | CUSTOMER | RECEPTIONIST | ADMIN | VNPAY SYSTEM |
| --------------------------------- | :---: | :------: | :----------: | :---: | :----------: |
| GET `/api/v1/payments/vnpay-return` | ❌ | ✔️ | ❌ | ✔️ | ❌ |
| GET `/api/v1/payments/vnpay-ipn`    | ❌ | ❌ | ❌ | ❌ | ✔️ |

---
