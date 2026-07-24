# Hướng dẫn: Check-out, Compliance & Back-Office Workflow

> **File draw.io:** `checkout-workflow.drawio`  
> **Mở bằng:** [draw.io desktop](https://github.com/jgraph/drawio-desktop/releases) hoặc [app.diagrams.net](https://app.diagrams.net)

---

## 🗺️ Tổng quan Swimlanes

| Màu | Lane | Mô tả |
|---|---|---|
| 🔵 Xanh dương | **Guest / Customer** | Khách thực hiện check-out, thanh toán, nhận hoá đơn |
| 🟢 Xanh lá | **Receptionist** | Lễ tân mở form, xác nhận thanh toán, hỏi GDPR |
| 🟡 Vàng | **System** | Xử lý backend: tổng hợp Folio, ghi transaction, async tasks |
| 🟠 Cam | **Housekeeping** | ROOM_CHECK, DAMAGE_CHECK, dọn phòng |
| 🔴 Đỏ/Hồng | **Maintenance** | Nhận task sửa chữa, repair phòng |

---

## 📋 Chi tiết từng bước

### Phase 1 — Khởi tạo Check-out

| Bước | Lane | Mô tả |
|---|---|---|
| ① | Guest | Khách đến quầy yêu cầu check-out |
| ② | Receptionist | Lễ tân mở **Check-out Form** (Folio Detail) |
| ③ | System | **Kiểm tra** tất cả ROOM_CHECK tasks đã `COMPLETED`? (BR-FOLIO-03) |

### Phase 2 — ROOM_CHECK (nếu chưa hoàn thành)

| Bước | Lane | Mô tả |
|---|---|---|
| ④ (NO branch) | Housekeeping | Thực hiện **ROOM_CHECK**: kiểm tra phòng, minibar, tìm hỏng hóc |
| ⑤ | Housekeeping | **Phát hiện hỏng hóc?** |
| ⑥ (YES) | Housekeeping | Tạo **DAMAGE_CHECK**: nhập `damagePrice`, ghi chú, upload ảnh |
| ⑦ (YES) | System | Aggregate bao gồm phí đền bù |

> **YES branch (đã hoàn thành):** Nhảy thẳng sang Phase 3

### Phase 3 — Tổng hợp Folio & Thanh toán

| Bước | Lane | Mô tả | BR |
|---|---|---|---|
| ⑧ | System | **Tổng hợp Folio & Xuất Invoice** | BR-FOLIO-01, BR-FOLIO-02 |
| | | + Room charge (auto-post nếu Night Audit bỏ qua) | BR-FOLIO-02 |
| | | + F&B / Minibar | |
| | | + Phí đền bù (non-VAT) | BR-FOLIO-01 |
| | | + VAT 10% trên taxable items | BR-FOLIO-01 |
| | | − Deposit đặt cọc / Promo discount | |
| ⑨ | Receptionist | Xác nhận & Thu thanh toán (hiển thị Invoice) | BR-SYS-04 |
| ⑩ | Guest | **Thanh toán** (Cash / VNPAY) | |
| ⑪ | System | Ghi `PaymentTransaction` + Set `RoomBookingDetail → Checked_Out` | BR-SYS-04 |
| | | + Gửi E-Invoice Email **@Async** | |
| | | + Cộng điểm **Loyalty** (1pt/10k VND) **@Async** | |

### Phase 4 — Xử lý phòng sau check-out

| Bước | Lane | Mô tả | BR |
|---|---|---|---|
| ⑫ | System | **Kiểm tra DAMAGE_CHECK** tồn tại? | |
| ⑬a (NO) | System | Room → `Vacant_Dirty` | |
| ⑬b (YES) | System | Room → `Maintenance` + Tạo **MAINTENANCE task** | BR-MT-01 |
| ⑭ | Maintenance | Nhận task, thực hiện sửa chữa | |
| ⑮ | Maintenance | Sửa xong → Room → `Vacant_Dirty` | BR-MT-03 |
| ⑯ | Housekeeping | Nhận task **CHECKOUT_CLEAN** (cả hai nhánh hội tụ ở đây) | |
| ⑰ | Housekeeping | Dọn phòng + kiểm tra lần cuối | |
| ⑱ | System | Room → `Vacant_Clean / Ready` | |

### Phase 5 — GDPR & Kết thúc

| Bước | Lane | Mô tả | BR |
|---|---|---|---|
| ⑲ | Receptionist | **Hỏi khách**: Yêu cầu xóa dữ liệu cá nhân? | BR-SYS-05 |
| ⑳a (NO) | Guest | **End** — Kết thúc phiên | |
| ⑳b (YES) | System | Vô danh hóa PII + Lưu hồ sơ tài chính (compliance) | BR-SYS-05 |
| ㉑ | Guest | **End** — Kết thúc sau GDPR | |

---

## 🔗 Business Rules được thể hiện trong diagram

| BR | Mô tả | Node trong diagram |
|---|---|---|
| BR-FOLIO-01 | VAT 10% cho taxable; phí đền bù non-VAT | N08 (Aggregate) |
| BR-FOLIO-02 | Auto-post room charge nếu Night Audit bỏ qua | N08 (Aggregate) |
| BR-FOLIO-03 | Chặn check-out nếu ROOM_CHECK chưa done | N04 (diamond gate) |
| BR-MT-01 | Tạo MAINTENANCE task khi phát hiện hỏng hóc | N13b |
| BR-MT-03 | Sửa xong → Room Vacant_Dirty | N15 |
| BR-SYS-04 | Audit Log: Confirm payment, Modify Invoice | N09, N11 |
| BR-SYS-05 | GDPR: Vô danh hóa PII khi khách yêu cầu | N20b |
| BR-FB-17 | VNPAY payment: service charge 5% → giảm còn 3% | Thực hiện trong PosServiceImpl |

---

## 📝 Ký hiệu

| Ký hiệu | Ý nghĩa |
|---|---|
| ⬤ (đen đặc) | Start / End Event |
| ◻ Bo góc | Process / Activity |
| ◇ Diamond | Decision Gate (YES/NO) |
| ◻ Viền đứt | Async activity (không block flow) |
| → mũi tên đứt | Async trigger / notification |

---

*Generated: 2026-07-23 | Project: HOANIEN Resort Management System*
