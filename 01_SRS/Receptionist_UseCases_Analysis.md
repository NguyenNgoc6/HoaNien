# Phân tích Use Case: Nhiệm vụ của Lễ tân (Receptionist Duties)
**Hệ thống:** Kawai Resort & Tour Hub
**Vai trò (Actor):** Receptionist

Tài liệu này phân tích chi tiết các Use Case cụ thể thuộc quy trình vận hành Tiền sảnh (Front Desk), giúp Lễ tân quản lý nhận/trả phòng và trạng thái phòng của khách sạn.

---

## 1. Monitor Expected Arrivals (Theo dõi danh sách khách dự kiến đến)
*   **Mô tả:** Lễ tân truy cập bảng điều khiển (Dashboard) để xem danh sách khách hàng dự kiến nhận phòng (Check-in) trong ngày.
*   **Tiền điều kiện:** Lễ tân đã đăng nhập vào hệ thống.
*   **Luồng cơ bản:**
    1. Lễ tân mở màn hình "Expected Arrivals Matrix".
    2. Hệ thống hiển thị danh sách các đặt phòng có `check_in_date` là ngày hiện tại và trạng thái là `Confirmed`.
    3. Lễ tân có thể lọc theo loại phòng (Room Category) hoặc tìm kiếm theo mã đặt phòng (Booking ID).
*   **Mục đích:** Giúp Lễ tân chủ động chuẩn bị hồ sơ, thẻ từ và phối hợp với Buồng phòng (Housekeeping) để chuẩn bị các phòng `Rush Room` (BR-FO-05) nếu khách đến sớm.

## 2. Monitor Planned Departures (Theo dõi danh sách khách dự kiến đi)
*   **Mô tả:** Lễ tân xem danh sách các phòng dự kiến trả (Check-out) trong ngày để chuẩn bị hóa đơn thanh toán và báo trước cho bộ phận Buồng phòng dọn dẹp.
*   **Tiền điều kiện:** Lễ tân đã đăng nhập vào hệ thống.
*   **Luồng cơ bản:**
    1. Lễ tân mở màn hình "Planned Departures".
    2. Hệ thống hiển thị danh sách các phòng có `check_out_date` là ngày hiện tại và trạng thái là `Occupied` (đang ở).
    3. Lễ tân rà soát công nợ (Folio Balance) hiện tại của các phòng này để báo trước cho khách nếu cần.

## 3. Validate Guest Information (Xác thực thông tin khách hàng)
*   **Mô tả:** Lễ tân kiểm tra giấy tờ tùy thân của khách hàng (CCCD/Passport) để đối chiếu với thông tin đã đăng ký trong Booking.
*   **Tiền điều kiện:** Khách hàng có mặt tại quầy lễ tân để làm thủ tục nhận phòng.
*   **Luồng cơ bản:**
    1. Lễ tân yêu cầu khách cung cấp CCCD/Passport.
    2. Lễ tân nhập hoặc quét mã trên giấy tờ vào hệ thống để tìm kiếm hồ sơ khách hàng.
    3. Hệ thống đối chiếu thông tin và hiển thị kết quả hợp lệ.
*   **Quy tắc nghiệp vụ (Business Rules):** 
    *   **BR-FO-03:** Khách đại diện làm thủ tục check-in bắt buộc phải từ 18 tuổi trở lên và phải xuất trình giấy tờ hợp lệ.

## 4. Process Check-in Request (Xử lý yêu cầu nhận phòng)
*   **Mô tả:** Quy trình tổng thể để ghi nhận khách bắt đầu lưu trú tại khách sạn.
*   **Tiền điều kiện:** Đặt phòng đang ở trạng thái `Confirmed` và thông tin khách hàng đã được xác thực (Validate Guest Information).
*   **Luồng cơ bản:**
    1. Lễ tân chọn Booking cần Check-in.
    2. Lễ tân tiến hành gán phòng vật lý (Allocate Physical Rooms).
    3. Lễ tân có thể cấp hạn mức chi tiêu (Credit Limit) và mã PIN (để gọi Room Service) cho khách.
    4. Lễ tân xác nhận Check-in.
*   **Hậu điều kiện:** Trạng thái đặt phòng (`Room_Booking_Details`) chuyển sang `Checked_In`. Công nợ (Folio) của phòng được kích hoạt.

## 5. Allocate Physical Rooms (Gán phòng vật lý)
*   **Mô tả:** Lễ tân chọn một số phòng vật lý cụ thể (ví dụ: Phòng 201) gán cho khách dựa trên loại phòng (Room Category) mà khách đã đặt.
*   **Tiền điều kiện:** Đang trong quy trình Check-in hoặc đổi phòng.
*   **Luồng cơ bản:**
    1. Hệ thống hiển thị danh sách các phòng vật lý đang trống dựa trên loại phòng của Booking.
    2. Lễ tân chọn một phòng theo ý muốn của khách (ví dụ: phòng tầng cao, view biển).
    3. Hệ thống khóa phòng đó lại cho Booking này.
*   **Quy tắc nghiệp vụ (Business Rules):**
    *   **BR-FO-01:** Chống Overbooking, đảm bảo một phòng vật lý không bị gán cho 2 đặt phòng trùng thời gian (Sử dụng `SELECT ... FOR UPDATE`).
    *   **BR-FO-04:** Lễ tân CHỈ được phép gán các phòng đang có trạng thái là `Vacant_Clean` (Trống & Sạch).

## 6. Process Check-out Request (Xử lý yêu cầu trả phòng)
*   **Mô tả:** Quy trình tổng hợp hóa đơn, thu tiền và hoàn tất việc lưu trú của khách.
*   **Tiền điều kiện:** Khách có mặt tại quầy yêu cầu trả phòng hoặc đến giờ trả phòng quy định.
*   **Luồng cơ bản:**
    1. Lễ tân tìm kiếm phòng đang yêu cầu Check-out.
    2. Hệ thống tổng hợp toàn bộ chi phí: Tiền phòng, F&B (Nhà hàng/Room Service), Tiền Tour (nếu có) và sinh ra Hóa đơn tổng hợp (Consolidated Invoice).
    3. Lễ tân áp dụng mã giảm giá (Vouchers) nếu có.
    4. Khách hàng thanh toán phần tiền còn thiếu. Lễ tân ghi nhận thanh toán (Payment Transaction).
    5. Lễ tân xác nhận Check-out thành công.
*   **Hậu điều kiện:**
    *   Trạng thái phòng chuyển sang `Vacant_Dirty` (BR-FO-04).
    *   Hệ thống tự động kích hoạt tạo nhiệm vụ (Task) báo cho Buồng phòng (Housekeeping) đi dọn dẹp (`Housekeeping_Task_Trigger`).
*   **Quy tắc nghiệp vụ (Business Rules):**
    *   **BR-FIN-01:** KHÔNG được phép Check-out nếu số dư hóa đơn (Folio) chưa được thanh toán toàn bộ.

## 7. Maintain Room Status (Quản lý trạng thái phòng)
*   **Mô tả:** Lễ tân sử dụng bảng điều khiển (Room Matrix) để theo dõi và cập nhật trạng thái vật lý của các phòng khi có sự cố hoặc yêu cầu đặc biệt.
*   **Tiền điều kiện:** Lễ tân đã đăng nhập vào hệ thống.
*   **Luồng cơ bản:**
    1. Lễ tân mở màn hình Sơ đồ phòng (Room Matrix Dashboard).
    2. Hệ thống hiển thị sơ đồ các phòng với các màu sắc đại diện cho trạng thái (`Vacant_Clean`, `Occupied`, `Vacant_Dirty`, `Maintenance`).
    3. Lễ tân có thể xem trực tiếp phòng nào đang trống, phòng nào đang có khách.
    4. Trong trường hợp đặc biệt, Lễ tân có thể can thiệp thủ công để đổi trạng thái phòng (ví dụ: khóa phòng lại không cho đặt nếu có dấu hiệu hư hỏng đột xuất trước khi báo bảo trì).
*   **Quy tắc nghiệp vụ (Business Rules):**
    *   **BR-FO-04:** Chu trình chuẩn của phòng bắt buộc là: `Vacant_Clean` → `Occupied` → `Vacant_Dirty` → `Vacant_Clean`. Lễ tân cần phối hợp với Housekeeping để đảm bảo vòng lặp này diễn ra trơn tru.

## 8. Create Customer Folio (Khởi tạo công nợ phòng - Folio)
*   **Mô tả:** Hệ thống tự động tạo hoặc Lễ tân chủ động thiết lập một sổ ghi nợ (Folio) cho khách hàng ngay khi Check-in để ghi nhận các chi phí phát sinh trong quá trình lưu trú.
*   **Tiền điều kiện:** Khách hàng đã được Check-in thành công (trạng thái phòng là `Occupied`).
*   **Luồng cơ bản:**
    1. Khi hoàn tất Check-in, hệ thống cấp phát một bảng theo dõi công nợ (Folio) gắn với `Room_Booking_Details`.
    2. Lễ tân có thể xem và kiểm tra số dư hiện tại của Folio (thường bằng 0 ở thời điểm Check-in nếu tiền phòng đã thanh toán trước).
    3. Thiết lập hạn mức tín dụng (Credit Limit) nếu khách có yêu cầu sử dụng các dịch vụ trả sau.
*   **Hậu điều kiện:** Khách có thể dùng số phòng và mã PIN để gọi đồ ăn/dịch vụ (dữ liệu lưu vào bảng `Folio_Items`).

## 9. Attach Service Charges (Ghi nhận phụ phí/dịch vụ vào phòng)
*   **Mô tả:** Lễ tân hoặc các bộ phận khác (Nhà hàng, Spa, Tour) đẩy các khoản phí phát sinh vào Folio của phòng để khách thanh toán một lần khi Check-out.
*   **Tiền điều kiện:** Phòng đang có trạng thái `Occupied` và hạn mức (Credit Limit) vẫn còn đủ.
*   **Luồng cơ bản:**
    1. Lễ tân chọn phòng cần thêm phụ phí (ví dụ: phí giặt là, mini-bar, đền bù đồ vỡ).
    2. Nhập chi tiết khoản phí, số tiền và bộ phận phát sinh (`source_department`).
    3. Hệ thống kiểm tra tổng nợ mới so với hạn mức (Credit Limit).
    4. Cập nhật khoản phí vào danh sách `Folio_Items` của phòng.
*   **Quy tắc nghiệp vụ (Business Rules):**
    *   **BR-FB-01:** Phí chỉ được đẩy vào phòng đang có người ở và phải thông qua xác thực PIN hoặc chữ ký (signature) đối với các dịch vụ tự gọi.

## 10. Generate Consolidated Invoice (Tạo hóa đơn tổng hợp)
*   **Mô tả:** Lễ tân tổng hợp mọi chi phí chưa thanh toán (Tiền phòng, F&B, Tour, Phụ phí) để xuất thành một hóa đơn duy nhất phục vụ Check-out.
*   **Tiền điều kiện:** Khách hàng yêu cầu trả phòng (Check-out).
*   **Luồng cơ bản:**
    1. Lễ tân nhấn nút tạo hóa đơn cho phòng đang làm thủ tục.
    2. Hệ thống rà soát toàn bộ các bảng `Room_Bookings`, `Folio_Items`, `Tour_Bookings` liên kết với khách hàng.
    3. Hệ thống tính toán tổng tiền trước thuế (`pre_tax_amount`), thuế VAT (`vat_amount`) và ra số tiền cuối cùng (`total_amount`).
    4. Lễ tân có thể áp dụng mã khuyến mãi (Promotions) tại bước này.
    5. Hóa đơn (`Consolidated_Invoices`) được tạo và lưu lại.

## 11. Process Final Payment (Xử lý thanh toán cuối cùng)
*   **Mô tả:** Lễ tân ghi nhận dòng tiền khách thanh toán cho hóa đơn tổng hợp vừa tạo bằng các hình thức khác nhau (Tiền mặt, Thẻ tín dụng, Chuyển khoản).
*   **Tiền điều kiện:** Hóa đơn tổng hợp (`Consolidated_Invoices`) đã được tạo và chưa thanh toán đủ.
*   **Luồng cơ bản:**
    1. Lễ tân thông báo số tiền cuối cùng cần thanh toán cho khách.
    2. Khách thực hiện thanh toán.
    3. Lễ tân chọn phương thức thanh toán (`gateway_method`) và nhập số tiền nhận được.
    4. Hệ thống ghi nhận một `Payment_Transactions` nối với hóa đơn.
    5. Nếu số dư hóa đơn về 0, trạng thái hóa đơn đổi thành `Paid`.
*   **Quy tắc nghiệp vụ (Business Rules):**
    *   **BR-FIN-01:** Chỉ khi hóa đơn chuyển sang trạng thái `Paid`, quy trình Check-out mới được phép hoàn tất.

## 12. Manage History Payment (Quản lý lịch sử thanh toán)
*   **Mô tả:** Lễ tân tra cứu lại các giao dịch thanh toán đa bước (cọc trước, trả góp, hoàn tiền) và các hóa đơn cũ để đối soát hoặc hỗ trợ khách hàng.
*   **Tiền điều kiện:** Lễ tân đã đăng nhập và có quyền xem giao dịch.
*   **Luồng cơ bản:**
    1. Lễ tân vào giao diện Lịch sử thanh toán.
    2. Tìm kiếm theo mã khách hàng, mã hóa đơn hoặc khoảng thời gian.
    3. Xem chuỗi các giao dịch (`Payment_Transactions`) liên quan đến một Booking cụ thể.
    4. Thực hiện in lại biên lai (Receipt) hoặc xử lý hoàn tiền (Refund) nếu có quy định áp dụng (VD: Hủy tour, hủy phòng đúng hạn theo BR-FIN-02).
