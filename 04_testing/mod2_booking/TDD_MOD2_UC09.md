# TEST-DRIVEN DEVELOPMENT SPECIFICATION FOR UC-9
## Đặc tả Kiểm thử Hướng Phát triển - Tìm kiếm phòng trống

| Field | Value |
| --- | --- |
| **Document ID** | `KAWAI-TDD-MOD2-009` |
| **Version** | 1.0 |
| **Date** | 2026-06-14 |
| **Status** | Approved |
| **Standard** | ISO/IEC/IEEE 29119-3:2021 — Software Testing Part 3: Test Documentation |
| **Author** | `Chu Xuân Dũng – Developer` |
| **Reviewed by** | `Nguyễn Xuân Lưu – Tech Lead` |
| **DPO Sign-off** | `[x] Approved` |
| **Approved by** | `Nguyễn Xuân Lưu` |
| **Classification** | Internal – Confidential |

---

### References:
*   `04_testing/mod2_booking/EDS_MOD2_UC09.md` — Đặc tả kỹ thuật UC-9
*   `04_testing/mod2_booking/SRS_Document_SWP391_G2.docx.md` — Tài liệu đặc tả yêu cầu UC-9
*   [RoomServiceUC09Test.java](file:///d:/SWP_Group2/su26-swp391-se2023-g2/03_sourcecode/kawai-backend/src/test/java/com/kawai/services/RoomServiceUC09Test.java) — File JUnit Test thực tế

> [!NOTE]
> **Quy ước TDD:** Thiết lập các kịch bản test trước khi hoàn thiện logic nghiệp vụ để đảm bảo độ bao phủ của code và ngăn ngừa lỗi logic.

---

### CHANGELOG

> [!IMPORTANT]
> **Policy 4.4 — Immutable History:** Không bao giờ xóa thông tin cũ.

| Ngày | Người thực hiện | Nội dung thay đổi |
| --- | --- | --- |
| 2026-06-14 | `Chu Xuân Dũng` | Khởi tạo tài liệu — TDD spec riêng biệt cho UC-9 |
| 2026-06-15 | `Chu Xuân Dũng` | Rà soát lại các luồng logic ngoại lệ cho chức năng Tìm kiếm phòng |
| 2026-06-15 | `AI Agent`      | Chạy testcase và update trạng thái GREEN (Pass) |

---

### MỤC LỤC

1. [Thông tin Module](#1-thong-tin-module)
2. [Logic Issues Resolved](#2-logic-issues-resolved)
3. [Test Design Specification (TDS)](#3-test-design-specification-tds)
4. [Test Case Specification](#4-test-case-specification)
5. [Red-Green-Refactor Tracker](#5-red-green-refactor-tracker)
6. [Entry / Exit Criteria](#6-entry--exit-criteria)
7. [Rollback Plan](#7-rollback-plan)

---

### 1. Thông tin Module

| Field | Value |
| --- | --- |
| **Feature / Gap ID** | `GAP-MOD2-UC09` |
| **Module** | `Đặt phòng (Booking & Front Desk)` |
| **Spec gốc** | `SRS_Document_SWP391_G2.docx.md` |
| **Priority** | 🔴 P0 |
| **Sprint** | `S1` |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Internal |
| **Compliance Scope** | Luật du lịch Việt Nam 2017 |
| **Upstream Dependencies** | `N/A` |
| **Downstream Consumers** | `Payment Gateway, POS, Finance` |

---

### 2. Logic Issues Resolved

| # | Spec gốc | Thực tế | Fix áp dụng trong test |
| --- | --- | --- | --- |
| **L1** | Thiếu bộ lọc cho các phòng đang sửa chữa hoặc lỗi dịch vụ | Trạng thái phòng `Maintenance` và `OutOfService` không được hiển thị | Lọc bỏ các phòng có trạng thái `Maintenance` hoặc `OutOfService` trong logic tìm kiếm |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi
Kiểm thử logic tìm kiếm phòng trống thời gian thực (UC09) thuộc component `RoomService` và class `RoomServiceImpl`.

#### TDS-02 — Test Basis / Cơ sở Kiểm thử
- `SRS_Document_SWP391_G2.docx.md` UC-9 Search Available Rooms.
- BR-FO-01: Chỉ hiển thị phòng không có booking trùng ngày.
- BR-FO-04: Trạng thái phòng ảnh hưởng đến kết quả tìm kiếm.
- BR-FIN-05: Giá phòng tính bằng BigDecimal, scale 0, HALF_UP.

#### TDS-03 — Test Conditions and Coverage Items

| Condition ID | Test Condition | Coverage Item | Test Cases |
| --- | --- | --- | --- |
| **TC-COND-001** | Tìm phòng trống trong khoảng thời gian không có booking trùng ngày | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-001` |
| **TC-COND-002** | Lọc các phòng đã bị đặt trước (overlapping bookings) | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002b` |
| **TC-COND-003** | Trả về danh sách trống khi không có phòng nào trong DB | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002` |
| **TC-COND-004** | Lọc bỏ phòng đang Bảo trì hoặc Ngừng hoạt động (Maintenance/OutOfService) | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002c` |
| **TC-COND-005** | Lọc theo tên hạng phòng (categoryName) | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002d` |
| **TC-COND-006** | Lọc theo sức chứa tối thiểu (minCapacity) | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002e` |
| **TC-COND-007** | Lọc theo giá tiền tối đa (maxPricePerNight) | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002f` |
| **TC-COND-008** | Validate ngày check-in > check-out | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002g` |
| **TC-COND-009** | Tìm kiếm kết hợp (Category + Price + Capacity) | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002h` |
| **TC-COND-010** | Tìm kiếm với maxPrice quá thấp (không có phòng) | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002i` |
| **TC-COND-011** | Tìm kiếm với đầu vào là giá trị âm | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002j` |
| **TC-COND-012** | Phân trang kết quả tìm kiếm | `RoomServiceImpl.searchAvailableRooms` | `TC-M2-002k` |

#### TDS-04 — Test Techniques / Kỹ thuật Kiểm thử
- **Boundary Value Analysis (BVA)**: Kiểm tra biên của thời gian để xác định tính overlapping của ngày check-in/check-out.
- **Equivalence Partitioning (EP)**: Phân nhóm các trạng thái phòng (Vacant_Clean, Vacant_Dirty, Occupied, Maintenance, OutOfService).

#### TDS-05 — Test Data Requirements
- **FX-001**: R101 - Deluxe - 2,000,000 VND - capacity 2 - Vacant_Clean.
- **FX-002**: R201 - Suite - 3,500,000 VND - capacity 4 - Vacant_Clean.

---

### 4. Test Case Specification

#### `TC-M2-001` — Tìm phòng trống đúng theo ngày nhận/trả

*   **Severity:** `HIGH`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-001`

**Preconditions:**
- Hệ thống có 2 phòng Deluxe và Suite hoạt động bình thường, không trùng lịch đặt phòng.

**Test Steps:**
1. **Arrange**: Cấu hình Mock `roomRepository.findAll()` trả về R101 và R201. Mock `roomBookingRepository.countOverlappingBookings()` trả về `0L` (không trùng lịch).
2. **Act**: Gọi `roomService.searchAvailableRooms()` với khoảng ngày tìm kiếm hợp lệ.
3. **Assert**: Kiểm tra danh sách kết quả có size = 2, giá phòng được làm tròn scale 0, HALF_UP.

**Expected Result (PASS):**
- Danh sách trả về không null và chứa đúng R101 và R201.

**Expected Result (FAIL):**
- Trả về null hoặc danh sách rỗng, hoặc giá trị làm tròn sai.

**Current Status:** 🟢 Passing
**Implementation Note:** So sánh ngày đặt phòng và làm tròn BigDecimal.

---

#### `TC-M2-002` — Không có phòng trống → trả danh sách rỗng

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-003`

**Preconditions:**
- Hệ thống không có phòng nào trong DB.

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về danh sách trống.
2. **Act**: Gọi `roomService.searchAvailableRooms()`.
3. **Assert**: Kiểm tra kết quả trả về không null và rỗng.

**Expected Result (PASS):**
- Trả về danh sách rỗng.

**Expected Result (FAIL):**
- Trả về null hoặc quăng lỗi NullPointerException.

**Current Status:** 🟢 Passing

---

#### `TC-M2-002b` — Tất cả phòng đều bận → trả danh sách rỗng

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-002`

**Preconditions:**
- Hệ thống có phòng R101 nhưng phòng này đã bị đặt trước trùng khoảng ngày tìm kiếm.

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về R101. Mock `roomBookingRepository.countOverlappingBookings()` trả về `1L` (bị trùng lịch).
2. **Act**: Gọi `roomService.searchAvailableRooms()`.
3. **Assert**: Kiểm tra kết quả trả về là danh sách rỗng.

**Expected Result (PASS):**
- Trả về danh sách rỗng.

**Expected Result (FAIL):**
- Trả về danh sách chứa phòng đã bị đặt.

**Current Status:** 🟢 Passing

---

#### `TC-M2-002c` — Lọc bỏ phòng đang Bảo trì hoặc Ngừng hoạt động

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-004`

**Preconditions:**
- Hệ thống có 3 phòng, trong đó phòng R101 đang bảo trì ('Maintenance'), R102 ngừng hoạt động ('OutOfService'), và R103 trống sạch sẽ ('Vacant_Clean').

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về R101, R102, R103. Mock `roomBookingRepository.countOverlappingBookings()` cho R103 trả về 0.
2. **Act**: Gọi `roomService.searchAvailableRooms()`.
3. **Assert**: Kiểm tra kết quả trả về chỉ chứa duy nhất phòng R103.

**Expected Result (PASS):**
- Chỉ trả về phòng R103. Phòng R101 và R102 bị lọc bỏ.

**Expected Result (FAIL):**
- Dẫn đến việc trả về phòng đang bảo trì hoặc ngừng hoạt động.

**Current Status:** 🟢 Passing

---

#### `TC-M2-002d` — Lọc theo tên hạng phòng

*   **Severity:** `LOW`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-005`

**Preconditions:**
- Có 2 phòng Deluxe (R101) và Suite (R201) trong DB.

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về R101 và R201. Yêu cầu lọc hạng phòng `Deluxe`.
2. **Act**: Gọi `roomService.searchAvailableRooms()` với Request lọc `Deluxe`.
3. **Assert**: Kiểm tra danh sách chỉ chứa phòng R101 (Deluxe).

**Expected Result (PASS):**
- Trả về danh sách chỉ chứa R101.

**Expected Result (FAIL):**
- Trả về cả R201 hoặc trả danh sách rỗng.

**Current Status:** 🟢 Passing

---

#### `TC-M2-002e` — Lọc theo sức chứa tối thiểu

*   **Severity:** `LOW`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-006`

**Preconditions:**
- Có phòng Deluxe R101 (sức chứa 2) và Suite R201 (sức chứa 4).

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về R101 và R201. Yêu cầu sức chứa tối thiểu = 3.
2. **Act**: Gọi `roomService.searchAvailableRooms()` với `minCapacity = 3`.
3. **Assert**: Kết quả chỉ chứa Suite R201.

**Expected Result (PASS):**
- Chỉ trả về phòng R201.

**Expected Result (FAIL):**
- Trả về cả R101 có sức chứa nhỏ hơn yêu cầu.

**Current Status:** 🟢 Passing

---

#### `TC-M2-002f` — Lọc theo mức giá tối đa

*   **Severity:** `LOW`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-007`

**Preconditions:**
- Có phòng Deluxe R101 (giá 2,000,000 VND) và Suite R201 (giá 3,500,000 VND).

**Test Steps:**
1. **Arrange**: Mock `roomRepository.findAll()` trả về R101 và R201. Yêu cầu mức giá tối đa là 2,500,000 VND.
2. **Act**: Gọi `roomService.searchAvailableRooms()` với `maxPricePerNight = 2,500,000`.
3. **Assert**: Kết quả trả về chỉ chứa R101.

**Expected Result (PASS):**
- Danh sách kết quả chỉ chứa R101.

**Expected Result (FAIL):**
- Trả về cả R201 (vượt mức giá trần).

**Current Status:** 🟢 Passing

---

#### `TC-M2-002g` — Validate ngày check-in > check-out

*   **Severity:** `HIGH`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-008`

**Preconditions:**
- Dữ liệu tìm kiếm không hợp lệ: ngày nhận phòng (check-in) sau ngày trả phòng (check-out).

**Test Steps:**
1. **Arrange**: Tạo request tìm phòng có checkIn = 2026-08-10, checkOut = 2026-08-05.
2. **Act**: Gọi `roomService.searchAvailableRooms(request)`.
3. **Assert**: Kiểm tra xem phương thức có ném lỗi `IllegalArgumentException` hay không.

**Expected Result (PASS):**
- Ném lỗi `IllegalArgumentException` thông báo ngày check-in phải trước check-out.

**Expected Result (FAIL):**
- Phương thức chạy bình thường mà không báo lỗi logic ngày tháng.

**Current Status:** 🟢 Passing

---

#### `TC-M2-002h` — Tìm kiếm kết hợp (Category + Price + Capacity)

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-009`

**Preconditions:**
- DB có các phòng: R101 (Deluxe, giá 2,000,000, sức chứa 2), R102 (Deluxe, giá 3,000,000, sức chứa 2), và R201 (Suite, giá 2,200,000, sức chứa 4).

**Test Steps:**
1. **Arrange**: Mock DB trả về R101, R102, R103. Yêu cầu lọc: `categoryName = Deluxe`, `minCapacity = 2`, `maxPricePerNight = 2,500,000`.
2. **Act**: Gọi `roomService.searchAvailableRooms(request)`.
3. **Assert**: Kiểm tra kết quả trả về chỉ chứa duy nhất phòng R101.

**Expected Result (PASS):**
- Trả về danh sách chỉ chứa R101.

**Expected Result (FAIL):**
- Trả về phòng sai category, sai sức chứa hoặc giá vượt quá giới hạn.

**Current Status:** 🟢 Passing

---

#### `TC-M2-002i` — Tìm kiếm với maxPrice quá thấp (không có phòng)

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-010`

**Preconditions:**
- Có phòng R101 giá 2,000,000 VND. Yêu cầu giá tối đa cực thấp: 500,000 VND.

**Test Steps:**
1. **Arrange**: Mock trả về R101. Set `maxPricePerNight = 500,000`.
2. **Act**: Gọi `roomService.searchAvailableRooms(request)`.
3. **Assert**: Kiểm tra xem kết quả trả về là một danh sách rỗng khác null.

**Expected Result (PASS):**
- Trả về danh sách rỗng, không lỗi.

**Expected Result (FAIL):**
- Trả về danh sách chứa R101 hoặc quăng exception.

**Current Status:** 🟢 Passing

---

#### `TC-M2-002j` — Tìm kiếm với đầu vào là giá trị âm

*   **Severity:** `HIGH`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-011`

**Preconditions:**
- Dữ liệu lọc không hợp lệ: `minCapacity = -1` hoặc `maxPricePerNight = -10000`.

**Test Steps:**
1. **Arrange**: Tạo request tìm phòng có giá trị lọc âm.
2. **Act**: Gọi `roomService.searchAvailableRooms(request)`.
3. **Assert**: Kiểm tra xem phương thức có ném lỗi `IllegalArgumentException` hay không.

**Expected Result (PASS):**
- Ném lỗi `IllegalArgumentException` thông báo giá trị nhập vào không được phép âm.

**Expected Result (FAIL):**
- Chấp nhận giá trị âm và trả về kết quả hoặc lỗi logic khác.

**Current Status:** 🟢 Passing

---

#### `TC-M2-002k` — Phân trang kết quả tìm kiếm

*   **Severity:** `MEDIUM`
*   **Feature Under Test:** `RoomServiceImpl.searchAvailableRooms()`
*   **Test File:** `src/test/java/com/kawai/services/RoomServiceUC09Test.java`
*   **TDD Phase:** 🟢 GREEN
*   **Condition Ref:** `TC-COND-012`

**Preconditions:**
- DB có 4 phòng trống (R101, R102, R103, R104). Yêu cầu trang số 1 (chỉ mục 1) với kích thước trang là 2.

**Test Steps:**
1. **Arrange**: Mock DB trả về 4 phòng. Cấu hình request có `page = 1` and `size = 2`.
2. **Act**: Gọi `roomService.searchAvailableRooms(request)`.
3. **Assert**: Kiểm tra xem kết quả trả về đúng 2 phòng R103 và R104.

**Expected Result (PASS):**
- Kết quả có size = 2 và chứa đúng 2 phòng ở trang tiếp theo (R103 và R104).

**Expected Result (FAIL):**
- Trả về không đúng trang kết quả, kích thước trang sai hoặc lỗi biên phân trang.

**Current Status:** 🟢 Passing

---



### 5. Red-Green-Refactor Tracker

| TC ID | Test File | 🔴 RED confirmed | 🟢 GREEN (commit) | 🔵 REFACTOR note |
| --- | --- | :---: | --- | --- |
| `TC-M2-001` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Base logic check and price rounding |
| `TC-M2-002` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Return empty array list instead of null |
| `TC-M2-002b` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Filter out overlapping booked rooms |
| `TC-M2-002c` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Filter out Maintenance & OutOfService rooms |
| `TC-M2-002d` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Filter rooms by categoryName criteria |
| `TC-M2-002e` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Filter rooms by minCapacity criteria |
| `TC-M2-002f` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Filter rooms by maxPricePerNight criteria |
| `TC-M2-002g` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Check-in after check-out date validation |
| `TC-M2-002h` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Combined search query filter AND logic |
| `TC-M2-002i` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Return empty list when maxPrice is too low |
| `TC-M2-002j` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Inputs (price/capacity) negative check |
| `TC-M2-002k` | `RoomServiceUC09Test.java` | `[x]` | `[x]` | Test pagination (page/size limit) |

---

### 6. Entry / Exit Criteria

#### Entry Criteria
- Yêu cầu nghiệp vụ UC-9 đã rõ ràng.
- File JUnit `RoomServiceUC09Test.java` đã sẵn sàng.

#### Exit Criteria
- 100% test cases của UC-9 chạy PASS.

---

### 7. Rollback Plan

**Revert implementation files:**
`git checkout -- 03_sourcecode/kawai-backend/src/main/java/com/kawai/services/impl/RoomServiceImpl.java`
