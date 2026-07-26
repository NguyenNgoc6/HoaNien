# Hòa Niên Retreat & Resort

**Đồ án Môn học:** SWP391 - Software Development Project  
**Lớp:** SE2023-G2  
**Nhóm:** Group 2  
**Giảng viên hướng dẫn:** Nguyễn Mạnh Cường  

---

## 📖 Giới thiệu Dự án (Project Description)

**Hòa Niên Retreat & Resort** là một hệ thống phần mềm quản lý vận hành toàn diện dành cho Khu nghỉ dưỡng (Resort) tích hợp với trung tâm điều hành Tour du lịch. 
Dự án được thiết kế để số hóa và tự động hóa các quy trình từ Front-Desk (Lễ tân), F&B (Nhà hàng & Bếp), Dọn phòng (Housekeeping) cho đến Tour Operations. 
Với kiến trúc monolithic mạnh mẽ trên nền tảng Spring Boot kết hợp giao diện Thymeleaf, hệ thống giải quyết bài toán quản lý tập trung, nâng cao trải nghiệm khách hàng và tối ưu hóa hiệu suất làm việc của nhân viên.

---

## 🌟 Các phân hệ chức năng chính (Core Modules)

1. **Quản lý Lễ tân (Reception & Front Desk):** 
   - Check-in / Check-out cho khách Walk-in và In-house.
   - Gán phòng tự động, quản lý Folio (Hóa đơn lưu trú) đa dịch vụ.
2. **Quản lý Ẩm thực (F&B / POS):** 
   - Hệ thống POS dành riêng cho nhà hàng và đặt món tại phòng (Room Service).
   - Đặt bàn, quản lý menu, đồng bộ order với bếp.
3. **Quản lý Buồng phòng (Housekeeping):** 
   - Tự động tạo Task dọn phòng khi khách Check-out.
   - Quản lý trạng thái phòng (Clean, Dirty, Maintenance).
4. **Quản lý Tour Du lịch (Tour Operations):** 
   - Lên lịch trình, quản lý danh sách khách tham gia (Tour Attendees).
5. **Night Audit & Tài chính (Finance):**
   - Chạy tiến trình Night Audit để chốt doanh thu cuối ngày.
   - Quản lý thanh toán tích hợp VNPay, xuất hóa đơn gộp (Consolidated Invoice).
6. **Hệ thống Quy trình (Workflow Engine):**
   - Xử lý các quy trình phê duyệt nội bộ (VD: Phê duyệt hoàn tiền - Refund Request).

---

## 🚀 Công nghệ sử dụng (Tech Stack)

### Backend
- **Core Framework:** Java 17, Spring Boot 3.x
- **Data Access:** Spring Data JPA, Hibernate ORM
- **Security:** Spring Security (OAuth2, BCrypt Password Encoding)
- **Database:** MySQL 8.x
- **Mail/Communication:** JavaMailSender, SendGrid, Cloudinary (Lưu trữ ảnh)
- **Thanh toán:** Tích hợp cổng VNPay Sandbox

### Frontend
- **Template Engine:** Thymeleaf
- **Styling:** HTML5, CSS3, Bootstrap 5, Custom CSS
- **Interactivity:** Vanilla JavaScript, GSAP (Animations), Toastify (Notifications), SweetAlert2

---

## 📂 Cấu trúc Thư mục (Project Structure)

Dự án được tổ chức chặt chẽ theo chuẩn quy trình phát triển phần mềm:

- `00-Policy/`: Các chính sách và quy định quản lý dự án.
- `01-Planning/`: Tài liệu lên kế hoạch, quản lý timeline và task.
- `02-Requirement/`: Tài liệu Đặc tả Yêu cầu Phần mềm (SRS - Software Requirement Specification).
- `03-Design/`: Tài liệu Đặc tả Thiết kế Hệ thống (Database Schema, UI/UX Mockups, UML Diagrams).
- `04-Implement/`: Các quy chuẩn coding convention.
- `05-Development/kawai-backend/`: **Chứa toàn bộ mã nguồn chính của ứng dụng Spring Boot.**
  - `src/main/java/`: Source code Java phân chia theo chuẩn MVC (Controllers, Services, Models, Repositories).
  - `src/main/resources/`: Cấu hình ứng dụng (`application.yml`), file giao diện Thymeleaf (`templates/`), static assets (`static/`), và dữ liệu mẫu (`data.sql`).
- `07-Reports/`: Báo cáo tiến độ và kết quả giai đoạn.
- `08-Document-References/`: Tài liệu tham khảo thêm.

---

## ⚙️ Hướng dẫn Cài đặt & Chạy dự án (Getting Started)

### Yêu cầu hệ thống:
- Java JDK 17+
- Maven 3.8+
- MySQL Server 8.0+
- IDE: IntelliJ IDEA, Eclipse, hoặc VS Code.

### Các bước triển khai:

1. **Clone repository:**
   ```bash
   git clone <repository-url>
   cd su26-swp391-se2023-g2/05-Development/kawai-backend
   ```

2. **Cấu hình Cơ sở dữ liệu:**
   - Tạo mới database trong MySQL có tên `kawai_db` (hoặc để Spring Boot tự động tạo thông qua cấu hình URL).
   - Mở file `src/main/resources/application.yml` và điều chỉnh các biến môi trường hoặc thông tin kết nối trực tiếp:
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://localhost:3306/kawai_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
         username: root
         password: your_password
     ```

3. **Thiết lập biến môi trường (Environment Variables) (Tùy chọn nhưng khuyến nghị):**
   - `SENDGRID_API_KEY`: Key của SendGrid để gửi mail.
   - `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`, `CLOUDINARY_CLOUD_NAME`: Cấu hình Cloudinary.

4. **Khởi tạo dữ liệu mẫu (Dummy Data):**
   - Hệ thống tự động chạy script `src/main/resources/data.sql` khi khởi động (nếu `spring.sql.init.mode=always`). Điều này sẽ insert sẵn danh sách tài khoản nhân viên, phòng, danh mục, v.v. để test ngay lập tức.
   - **Tài khoản test mặc định:**
     - Admin: `nguyenquantri` / `admin123`
     - Lễ tân: `lelinh` / `staff123`
     - POS (Nhà hàng): `pos1` / `staff123`

5. **Build và Chạy ứng dụng:**
   Sử dụng Maven để build và chạy ứng dụng:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   *Ứng dụng sẽ chạy ở cổng `8080`. Truy cập trình duyệt tại: `http://localhost:8080`*

---

## 👥 Nhóm phát triển (Group 2)
*(Vui lòng cập nhật chi tiết các thành viên và role tại đây)*
- Thành viên 1 - Backend / Tech Lead
- Thành viên 2 - Frontend Developer
- Thành viên 3 - Database & Business Analyst
- Thành viên 4 - Tester & QA
- Thành viên 5 - Documentation

---
*Mọi thắc mắc liên quan đến dự án vui lòng liên hệ trực tiếp với Technical Lead của nhóm để được giải đáp.*
