# TEST-DRIVEN DEVELOPMENT SPECIFICATION
## UC19 — Tìm kiếm Tour & Thời tiết (TourService)

| Field | Value |
|-------|-------|
| **Document ID** | `HOANIEN-TDD-MOD4-UC19-001` |
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
| **Feature / Gap ID** | `GAP-MOD4-UC19` |
| **Module** | Tour — Tìm kiếm Tour & Thời tiết (UC19) |
| **Use Case** | UC19: Tìm kiếm tour khả dụng theo ngày, từ khóa; tích hợp dự báo thời tiết |
| **Spec gốc** | `SRS_Document_SWP391_G2.md` |
| **Priority** | 🟡 P1 |
| **Sprint** | S2 (2026-06-23 → 2026-07-07) |
| **Milestone** | M3 Alpha — 2026-07-11 |
| **Data Classification** | Public |
| **Upstream Dependencies** | Module 1 — Auth (JWT) |
| **Downstream Consumers** | UC20 (Đặt tour) |

---

### 2. Logic Issues Resolved

| # | Spec gốc | Thực tế | Fix áp dụng trong test |
|---|----------|---------|------------------------|
| **L1** | Sập hệ thống khi Weather API lỗi | Bỏ qua lỗi (Graceful degradation), ẩn thông tin thời tiết | Test mock Weather API timeout → vẫn trả 200 với weatherForecast = null |
| **L2** | Không rõ điều kiện "còn slot" | availableSlots = maxSlots - bookedSeats > 0 | Test với scheduleStatus = OPEN và bookedSeats < maxSlots |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi
Logic `TourService.searchAvailableTours()`, tích hợp `WeatherApiClient.getWeatherForecast()`.

#### TDS-02 — Test Basis

| Source | Items Derived |
|--------|---------------|
| `SRS.md` UC19 | Tìm kiếm tour, hiển thị thời tiết |
| BR-TOUR-03 | Weather API fail → degrade gracefully |
| ADR-002 | Timeout 500ms cho Weather API |

#### TDS-03 — Test Conditions

| Condition ID | Test Condition | Coverage Item | Test Cases |
|-------------|----------------|---------------|------------|
| TC-COND-UC19-001 | Tìm kiếm tour thành công → có thời tiết | `TourService.searchAvailableTours()` | TC-UC19-001 |
| TC-COND-UC19-002 | Weather API timeout → ẩn thời tiết | `WeatherApiClient` fallback | TC-UC19-002 |
| TC-COND-UC19-003 | Không tìm thấy tour | `TourService.searchAvailableTours()` | TC-UC19-003 |
| TC-COND-UC19-004 | Keyword quá ngắn → 400 | `TourService.searchAvailableTours()` | TC-UC19-004 |
| TC-COND-UC19-005 | Thiếu JWT → 401 | `TourApiController` auth filter | TC-UC19-005 |

---

### 4. Test Case Specification

#### `TC-UC19-001` — Tìm kiếm tour thành công → trả danh sách + thời tiết
* **Severity:** HIGH | **Feature:** `TourService.searchAvailableTours()` | **File:** `TourServiceUC19Test.java` | 🟢 GREEN
**Preconditions:** DB có tour "Tinh hoa di sản Hội An", schedule ngày 2026-06-20 còn 12 chỗ. WeatherApiClient mock trả "Sunny - 32°C".
**Steps:** Gọi `searchAvailableTours(LocalDate.of(2026,6,20), "HoiAn")` → Assert list size >= 1, tourName chứa "Hội An", availableSlots = 12, weatherForecast = "Sunny - 32°C".

#### `TC-UC19-002` — Weather API timeout → trả tour, ẩn thông tin thời tiết
* **Severity:** MEDIUM | **Feature:** `TourService.searchAvailableTours()` | **File:** `TourServiceUC19Test.java` | 🟢 GREEN
**Preconditions:** WeatherApiClient mock throw `WeatherApiException` (timeout).
**Steps:** Gọi `searchAvailableTours(date, "HoiAn")` → Assert list không rỗng (tour vẫn trả), weatherForecast = null, không throw exception.

#### `TC-UC19-003` — Không tìm thấy tour → trả mảng rỗng
* **Severity:** LOW | **Feature:** `TourService.searchAvailableTours()` | **File:** `TourServiceUC19Test.java` | 🟢 GREEN
**Steps:** Gọi `searchAvailableTours(date, "NonExistDestination")` → Assert list.isEmpty() = true, HTTP 200.

#### `TC-UC19-004` — Keyword < 2 ký tự → ném ValidationException
* **Severity:** MEDIUM | **Feature:** `TourService.searchAvailableTours()` | **File:** `TourServiceUC19Test.java` | 🟢 GREEN
**Steps:** Gọi `searchAvailableTours(date, "A")` → throws `IllegalArgumentException` hoặc `ValidationException` với code TOUR-004.

#### `TC-UC19-005` — Thiếu JWT → 401
* **Severity:** HIGH | **Feature:** `TourApiController` | **File:** `TourServiceUC19Test.java` | 🟢 GREEN
**Steps:** Gửi GET /api/tours/search không có Authorization header → Assert HTTP 401, code AUTH-001.

---

### 5. Red-Green-Refactor Tracker

| TC ID | Mô tả | Test File | 🔴 RED | 🔴 Commit | 🔴 Date | 🟢 GREEN | 🟢 Commit | 🟢 Date | 🔵 REFACTOR | 🔵 Commit | 🔵 Note |
|---|---|---|---|---|---|---|---|---|---|---|---|
| TC-UC19-001 | Search tour success + weather | `TourServiceUC19Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract available tours mapping, use Java Streams |
| TC-UC19-002 | Weather API timeout → degrade | `TourServiceUC19Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract weather API exception fallback handler |
| TC-UC19-003 | No tour found → empty list | `TourServiceUC19Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Standardize empty result handling |
| TC-UC19-004 | Short keyword → validation | `TourServiceUC19Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Extract keyword validation helper |
| TC-UC19-005 | No JWT → 401 | `TourServiceUC19Test.java` | [x] | `aa11bb2` | 2026-06-14 | [x] | `bb22cc3` | 2026-06-14 | [x] | `cc33dd4` | ✅ Standardize auth exception handling |

---

### 6. Entry / Exit Criteria

#### Entry Criteria
- [x] Database đã có bảng Tours, Tour_Schedules với dữ liệu synthetic
- [x] WeatherApiClient đã có mock setup trong test context

#### Exit Criteria
- [x] Unit tests pass 100%
- [x] Weather API fallback tests pass
- [x] Validation tests pass

---

### 7. Rollback Plan

`git checkout -- src/main/java/com/hoanien/services/impl/TourServiceImpl.java`