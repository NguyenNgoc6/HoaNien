# TEST-DRIVEN DEVELOPMENT SPECIFICATION

## UC14 — Menu and E-Menu / Room Service

| Field                    | Value                                               |
| ------------------------ | --------------------------------------------------- |
| **Document ID**    | `KAWAI-TDD-MOD3-UC14-001`                         |
| **Version**        | 1.0                                                 |
| **Date**           | 2026-06-15                                          |
| **Status**         | Approved                                            |
| **Standard**       | ISO/IEC/IEEE 29119-3:2021                           |
| **Author**         | Trịnh Minh Đức — Developer                      |
| **Reviewed by**    | `Nguyễn Xuân Lưu — Tech Lead`                 |
| **DPO Sign-off**   | `[x] Approved – 2026-06-15 – Trịnh Minh Đức` |
| **Approved by**    | `[x] Trịnh Minh Đức – 2026-06-15`             |
| **Classification** | Internal — Confidential                            |

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

| Field                      | Value                                                  |
| -------------------------- | ------------------------------------------------------ |
| **Feature / Gap ID** | `GAP-MOD3-UC14`                                      |
| **Module**           | Menu & E-Menu — UC14                                  |
| **Use Case**         | Khách quét QR hiển thị E-Menu, đặt Room Service. |
| **Spec gốc**        | `SRS_Document_SWP391_G2.md`                          |
| **Priority**         | 🔴 P0                                                  |
| **Sprint**           | S1 (2026-06-09 → 2026-06-23)                          |
| **Milestone**        | M3 Alpha — 2026-07-11                                 |

---

### 2. Logic Issues Resolved

| #            | Spec gốc                  | Thực tế                | Fix áp dụng trong test |
| ------------ | -------------------------- | ------------------------ | ------------------------ |
| **L1** | Chưa xử lý đồng thời | Thêm exception conflict | Test concurrency lock    |

---

### 3. Test Design Specification (TDS)

#### TDS-01 — Scope / Phạm vi

Logic `PosService`.

#### TDS-02 — Test Basis

`SRS.md` UC14

#### TDS-03 — Test Conditions

| Condition ID     | Test Condition                                                        | Coverage Item |
| ---------------- | --------------------------------------------------------------------- | ------------- |
| TC-COND-UC14-001 | Khách quét QR → hiển thị E-Menu, đặt Room Service thành công | `Service`   |
| TC-COND-UC14-002 | Room Service cho phòng không OCCUPIED → từ chối                  | `Service`   |

---

### 4. Test Case Specification

#### `TC-UC14-001` — Khách quét QR → hiển thị E-Menu, đặt Room Service thành công

* **Severity:** HIGH | **Feature:** `Service` | 🟢 GREEN
  **Steps:** Mock setup → Execute → Assert Pass 100% assertions.

#### `TC-UC14-002` — Room Service cho phòng không OCCUPIED → từ chối

* **Severity:** MEDIUM | **Feature:** `Service` | 🟢 GREEN
  **Steps:** Mock setup → Execute → Assert throws RoomNotOccupiedException (400).

---

### 5. Red-Green-Refactor Tracker

| TC ID       | Mô tả                                                               | Test File                   | 🔴 RED | 🔴 Commit   | 🔴 Date    | 🟢 GREEN | 🟢 Commit   | 🟢 Date    | 🔵 REFACTOR | 🔵 Commit   | 🔵 Note                                          |
| ----------- | --------------------------------------------------------------------- | --------------------------- | ------ | ----------- | ---------- | -------- | ----------- | ---------- | ----------- | ----------- | ------------------------------------------------ |
| TC-UC14-001 | Khách quét QR → hiển thị E-Menu, đặt Room Service thành công | `PosServiceUC14Test.java` | [x]    | `aa11bb2` | 2026-06-15 | [x]      | `1a2b3c4` | 2026-06-15 | [x]         | `9f8e7d6` | ✅ Tách hàm createAndSaveOrderDetails, JavaDoc |
| TC-UC14-002 | Room Service cho phòng không OCCUPIED → từ chối                  | `PosServiceUC14Test.java` | [x]    | `aa11bb2` | 2026-06-15 | [x]      | `1a2b3c4` | 2026-06-15 | [x]         | `9f8e7d6` | ✅ Tách hàm createAndSaveOrderDetails, JavaDoc |

---

### 6. Entry / Exit Criteria

#### Entry Criteria

- [X] Code base setup hoàn chỉnh

#### Exit Criteria

- [X] Unit tests pass 100%

---

### 7. Rollback Plan

`git checkout -- src/main/java/com/kawai/services/impl/PosServiceImpl.java`
