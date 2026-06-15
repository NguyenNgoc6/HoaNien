# TEST-DRIVEN DEVELOPMENT SPECIFICATION
## UC21 — Điểm danh FaceID & Thủ công (TourAttendanceService)

| Field | Value |
|-------|-------|
| **Document ID** | `HOANIEN-TDD-MOD4-UC21-001` |
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
| **Feature / Gap ID** | `GAP-MOD4-UC21` |
| **Module** | Tour — Điểm danh FaceID & Thủ công |
| **Use Case** | UC21: Điểm danh AI FaceID; fallback thủ công |
| **Spec gốc** | `SRS_Document_SWP391_G2.md` |
| **Priority** | 🟡 P1 |
| **Milestone** | M3 Alpha — 2026-07-11 |

---

### 2. Logic Issues Resolved

| # | Spec gốc | Thực tế | Fix trong test |
|---|----------|---------|----------------|
| L1 | Không có ngưỡng confidence | >= 0.6 mới PASS | Test confidence=0.55 → FACE-002 |
| L2 | AI lỗi không có fallback | Trả ai_unavailable | Test AI timeout → không throw |
| L3 | Chưa quy định lưu gì | Face vector + face_matched_at | Test verify dữ liệu được lưu |

---

### 3. TDS

#### TDS-01 — Scope
Logic `processFaceScan()`, `updateStatus()`, tích hợp `FaceIdApiClient`.

#### TDS-02 — Test Conditions

| Condition ID | Test Condition | Test Cases |
|-------------|----------------|------------|
| TC-COND-UC21-001 | AI match >= 0.6 | TC-UC21-001 |
| TC-COND-UC21-002 | AI unavailable fallback | TC-UC21-002 |
| TC-COND-UC21-003 | Confidence < 0.6 → FACE-002 | TC-UC21-003 |
| TC-COND-UC21-004 | No face → FACE-001 | TC-UC21-004 |
| TC-COND-UC21-005 | Manual attendance → PRESENT | TC-UC21-005 |

---

### 4. Test Case Specification

#### TC-UC21-001 — AI match → PRESENT
*HIGH | `processFaceScan()` | `TourAttendanceServiceUC21Test.java` | 🟢 GREEN
**Steps:** Gọi `processFaceScan(5, "base64Image")` → Assert matched=true, confidence=0.89, attendanceStatus=PRESENT.

#### TC-UC21-002 — AI unavailable → ai_unavailable
*HIGH | `processFaceScan()` | 🟢 GREEN
**Steps:** Mock timeout → Assert status=ai_unavailable, không throw.

#### TC-UC21-003 — Confidence < 0.6 → FACE-002
*MEDIUM | 🟢 GREEN
**Steps:** Mock confidence=0.45 → Assert errorCode=FACE-002.

#### TC-UC21-004 — No face → FACE-001
*MEDIUM | 🟢 GREEN
**Steps:** Mock faceCount=0 → throws `FaceNotFoundException`.

#### TC-UC21-005 — Manual → PRESENT
*MEDIUM | `updateStatus()` | 🟢 GREEN
**Steps:** Gọi `updateStatus(5, "PRESENT")` → Assert manual_checked_by được ghi.

---

### 5. Red-Green-Refactor Tracker

| TC ID | Description | 🔴 | 🟢 | 🔵 |
|---|---|---|---|---|
| TC-UC21-001 | AI match | [x] | [x] | ✅ Tách getAttendeeById |
| TC-UC21-002 | AI unavailable | [x] | [x] | ✅ Refactor Controller |
| TC-UC21-003 | Low confidence | [x] | [x] | ✅ Extract threshold constant |
| TC-UC21-004 | No face | [x] | [x] | ✅ Standardize exception |
| TC-UC21-005 | Manual | [x] | [x] | ✅ Clean variables |

---

### 6. Entry / Exit Criteria
- [x] TourAttendees đã được tạo
- [x] FaceIdApiClient mock sẵn sàng

---

### 7. Rollback Plan
```bash
# System Config: tour.attendance.faceid.enabled = false
git checkout -- src/main/java/com/hoanien/services/impl/TourAttendanceServiceImpl.java