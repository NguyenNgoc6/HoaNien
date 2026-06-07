# Phân tích Yêu cầu Hệ thống (SRS Analysis)
**Dự án:** Kawai Resort & Tour Hub
**Nguồn phân tích:** `SRS_Document_SWP391_G2.docx`

---

## 1. Danh sách các Tác nhân (Actors)
Hệ thống được thiết kế với 9 đối tượng người dùng (Actors) có các quyền hạn và chức năng riêng biệt:

| # | Actor | Mô tả chi tiết |
|---|---|---|
| 1 | **Administrator** | Quản trị viên hệ thống. Quản lý cấu hình, tài khoản người dùng, phân quyền (Role) và giám sát hoạt động hệ thống (Audit logs). |
| 2 | **Manager** | Cấp quản lý. Xem Dashboard tài chính, xuất báo cáo, chạy chốt ngày (Night Audit) và cấu hình giá (Dynamic Pricing). |
| 3 | **Guest** | Khách vãng lai (Chưa đăng nhập). Có thể xem thông tin resort, tìm kiếm phòng trống, xem danh sách tour nhưng không thể thực hiện đặt chỗ. |
| 4 | **Customer** | Khách hàng đã đăng ký. Có thể đặt phòng, đặt tour, gọi món ăn, xem công nợ cá nhân (Folio), đánh giá dịch vụ và quản lý hồ sơ. |
| 5 | **Receptionist** | Lễ tân. Quản lý nhận/trả phòng (Check-in/Check-out), xếp phòng vật lý, cấp hạn mức chi tiêu và xử lý thanh toán tổng hợp. |
| 6 | **F&B Staff** | Nhân viên nhà hàng. Lên đơn món ăn, xử lý thanh toán hoặc đẩy chi phí ăn uống vào hóa đơn phòng của khách. |
| 7 | **HouseKeeping** | Nhân viên Buồng phòng. Nhận lịch dọn phòng, cập nhật trạng thái dọn dẹp và báo cáo thiết bị hỏng hóc. |
| 8 | **Tour Guide** | Hướng dẫn viên. Xem lịch trình phân công, điểm danh hành khách tham gia tour bằng AI Face Scan. |
| 9 | **Maintainer** | Nhân viên Bảo trì. Tiếp nhận báo cáo hỏng hóc, tiến hành sửa chữa và cập nhật trạng thái thiết bị. |

---

## 2. Phân rã các Phân hệ (Modules) & Use Cases
Hệ thống được chia thành 8 phân hệ (Modules) cốt lõi bao gồm 40 Use Cases.

### 2.1. Phân hệ Xác thực & Quản lý người dùng (Authentication & User Profiles)
Phân hệ này xử lý bảo mật và quản lý thông tin định danh của khách hàng.
*   **UC-01 Login System:** Xác thực đăng nhập vào hệ thống.
*   **UC-02 Register Account:** Đăng ký tài khoản khách hàng mới.
*   **UC-03 Verify Authentication via OTP:** Xác thực bảo mật hai lớp bằng OTP.
*   **UC-04 Reset Password:** Đặt lại mật khẩu.
*   **UC-05 Manage Profiles & Upload Identity Documents:** Cập nhật hồ sơ và tải lên giấy tờ tùy thân (CCCD/Passport).
*   **UC-06 Upgrade Dependent to Customer:** Nâng cấp tài khoản cho người đi cùng thành khách hàng độc lập.
*   **UC-07 Request Personal Data Deletion:** Yêu cầu xóa dữ liệu cá nhân theo quyền riêng tư.

### 2.2. Phân hệ Đặt phòng & Tiền sảnh (Room Booking & Front Desk)
Xử lý các quy trình cốt lõi của khách sạn từ lúc khách tìm phòng đến khi nhận phòng.
*   **UC-10 Search Available Rooms:** Tìm kiếm phòng trống theo ngày và loại phòng.
*   **UC-11 Book Room & Pay Online Deposit:** Đặt phòng và thanh toán cọc qua VNPay.
*   **UC-12 View Expected Arrivals Matrix:** Xem biểu đồ lượng khách dự kiến Check-in/Check-out.
*   **UC-13 Check-In & Allocate Physical Rooms:** Làm thủ tục nhận phòng và gán số phòng thực tế.
*   **UC-14 Authorize Spend Limit:** Cấp hạn mức chi tiêu (Credit limit) cho dịch vụ.

### 2.3. Phân hệ Nhà hàng (Restaurant POS)
Quản lý vận hành khu vực ẩm thực và phục vụ tận phòng.
*   **UC-15 Order Room Service:** Khách đặt đồ ăn giao lên phòng.
*   **UC-16 Order Dine-In Meal:** Nhân viên F&B gọi món cho khách dùng tại nhà hàng.
*   **UC-17 Track Kitchen Order Ticket (KOT):** Bếp trưởng theo dõi vé gọi món (Pending → Preparing → Served).
*   **UC-18 Flag Out-of-Stock Dishes:** Đánh dấu món hết hạn/hết nguyên liệu.
*   **UC-19 Reserve Dining Table:** Đặt bàn trước tại nhà hàng.
*   **UC-20 Charge Meal Bill to Room:** Ghi nợ tiền ăn vào hóa đơn tổng của phòng.

### 2.4. Phân hệ Đặt & Điều hành Tour (Tour Booking & Operations)
*   **UC-21 Schedule Tour Deliveries:** Lên lịch trình khởi hành Tour.
*   **UC-22 Browse & Book Tours:** Tìm kiếm và đặt mua vé Tour.
*   **UC-23 Auto-Cancel Under-Capacity Tours:** Tự động hủy Tour không đủ số lượng tối thiểu và hoàn tiền.
*   **UC-24 Execute AI Face Scan Attendance:** Điểm danh khách đi Tour bằng AI Face Scan.

### 2.5. Phân hệ Kế toán & Tài chính (Hospitality Finance)
*   **UC-27 Track Itemized Room Debt (Folio):** Theo dõi công nợ phòng chi tiết.
*   **UC-28 Generate Consolidated Invoice:** Tạo hóa đơn tổng hợp (Phòng, F&B, Tour).
*   **UC-29 Process Final Checkout Payment:** Xử lý thanh toán khi Check-out.
*   **UC-30 Run Night Audit & Daily Closing:** Chạy nghiệp vụ chốt sổ ngày tự động (Night Audit).
*   **UC-31 Monitor Financial Dashboards:** Xem thống kê doanh thu theo chuẩn USALI.
*   **UC-33 Export Financial Reports:** Xuất báo cáo tài chính ra Excel/PDF.

### 2.6. Phân hệ Buồng phòng & Bảo trì (Housekeeping & Maintenance)
*   **UC-34 Auto-Trigger Checkout Cleaning:** Tự động tạo Task dọn dẹp khi khách Check-out.
*   **UC-35 Update Cleaning Status:** Buồng phòng cập nhật trạng thái phòng sau khi dọn.
*   **UC-36 Log Maintenance Requests:** Báo cáo yêu cầu bảo trì thiết bị hư hỏng.
*   **UC-37 Resolve Engineering Failures:** Kỹ thuật viên cập nhật trạng thái sửa chữa.

### 2.7. Phân hệ Quản trị Dữ liệu Gốc & Hệ thống (Master Data & Administration)
*   **UC-08 Manage Staff Accounts & Roles:** Quản lý tài khoản nhân viên và phân quyền.
*   **UC-09 Manage Master Data:** Quản lý danh mục (Loại phòng, Món ăn, Tour).
*   **UC-32 Track Anti-Fraud Audit Logs:** Theo dõi Log hệ thống chống gian lận.
*   **UC-38 Apply Promotion Campaign Vouchers:** Áp dụng mã giảm giá.
*   **UC-39 Log Multi-Stage Transactions:** Ghi nhận chuỗi giao dịch đa bước.
*   **UC-40 Set Dynamic Holiday Pricing:** Cấu hình giá phòng biến động theo ngày lễ.

### 2.8. Phân hệ Đánh giá Hệ thống (Feedback System)
*   **UC-25 Submit 5-Star Reviews:** Đánh giá dịch vụ 5 sao.
*   **UC-26 Moderate Customer Reviews:** Kiểm duyệt đánh giá (hiện/ẩn).

---

## 3. Các Logic Nổi Bật (Key Business Rules)
1. **Auto-Cancellation (BR-FO-02):** Hủy đặt chỗ tự động nếu không thanh toán cọc trong 15 phút.
2. **Folio Real-time Limit (BR-FB-01):** Khách chỉ có thể charge hóa đơn dịch vụ vào phòng nếu nằm trong hạn mức (Credit limit) và phải nhập PIN xác thực.
3. **Face Scan Attendance (BR-TR-02):** Nhận diện khuôn mặt AI khi lên tour, yêu cầu độ chính xác > 85%.
4. **Night Audit (BR-FIN-03):** Tự động chốt sổ doanh thu và chuyển ngày kế toán vào 02:00 AM hằng ngày.
5. **USALI Standard (BR-FIN-04):** Báo cáo tài chính chia chuẩn thành 3 nhánh doanh thu: Room, F&B, và Tour.
6. **Trigger Room Status (BR-FO-04):** Luồng trạng thái phòng chuẩn mực `Vacant_Clean → Occupied → Vacant_Dirty → Vacant_Clean`.
