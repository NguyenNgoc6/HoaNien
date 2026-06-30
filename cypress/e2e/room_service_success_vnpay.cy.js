describe('Module 3: Khách hàng đặt món (Room Service) - Kịch bản thanh toán qua VNPay', () => {
  it('Thực hiện quy trình đặt món và thanh toán trực tuyến qua cổng VNPay', () => {
    cy.viewport(1440, 900);

    // 1. Đăng nhập bằng tài khoản hợp lệ
    cy.visit('http://localhost:8080/living');
    cy.contains('Log In').click({ force: true });
    cy.get('#ajaxLoginForm input[name="username"]').type('tmduc');
    cy.get('#ajaxLoginForm input[name="password"]').type('admin123');
    cy.get('#ajaxLoginForm button[type="submit"]').click();
    cy.wait(1500);

    // 2. Vào mục Order Food
    cy.contains('Dining').click({ force: true });
    cy.url().should('include', '/dining');
    cy.contains('Order Food').click({ force: true });

    // 3. Chọn món ăn bất kỳ
    cy.get('#menuSearchInput').type('Bò');
    cy.contains('Bò').first().click({ force: true });
    cy.get('#modalAddToCartBtn').should('be.visible').click();

    // 4. Chọn phương thức thanh toán VNPay (ONLINE)
    cy.get('input[value="ONLINE"]').check({ force: true });
    cy.get('#orderConsent').check({ force: true });

    // 5. Đặt hàng và thao tác trên cổng VNPay
    cy.get('#submitOrderBtn').click();

    // Cấu hình origin để cho phép thao tác chéo tên miền (Cross-Origin) sang VNPay Sandbox
    cy.origin('https://sandbox.vnpayment.vn', () => {
      // Bỏ qua các lỗi JS ngoại lệ từ trang VNPay
      cy.on('uncaught:exception', () => false);

      // Bước 1: Chọn ngân hàng NCB
      cy.get('body').then($body => {
        // Tuỳ vào giao diện sandbox hiện tại, có thể cần click mở danh sách thẻ nội địa trước
        if ($body.text().includes('Thẻ ATM và tài khoản ngân hàng')) {
          cy.contains('Thẻ ATM và tài khoản ngân hàng').click({ force: true });
        }
      });
      // Click vào logo NCB
      cy.get('img[src*="ncb"]').parent().click({ force: true });

      // Bước 2: Điền thông tin thẻ kiểm thử
      // (Sử dụng selector linh hoạt phòng trường hợp VNPay cập nhật UI Sandbox)
      cy.get('input[id*="cardNumber"], input[id*="card_number"]').filter(':visible').first().type('9704198526191432198');
      cy.get('input[id*="cardHolder"], input[id*="card_holder"]').filter(':visible').first().type('NGUYEN VAN A');
      cy.get('input[id*="cardDate"], input[id*="issueDate"]').filter(':visible').first().type('07/15');

      // Bước 3: Xác thực
      cy.contains(/^Tiếp tục$/i).click({ force: true });

      // Bấm "Đồng ý & Tiếp tục" ở popup Điều khoản sử dụng
      cy.contains('Đồng ý & Tiếp tục', { timeout: 10000 }).should('be.visible').click({ force: true });

      // Bước 4: Nhập OTP
      // Chờ loading ẩn đi và ô nhập mã OTP thực sự xuất hiện
      cy.get('input[placeholder="Nhập mã OTP"]', { timeout: 15000 })
        .should('be.visible')
        .type('123456');

      // Bấm nút Thanh toán (Chỉ đích danh thẻ button để không click nhầm vào các đoạn text "thanh toán" trên trang)
      cy.contains('button', 'Thanh toán').click({ force: true });

      // Bước 5: Sau khi thanh toán, VNPay chuyển sang trang Confirm.html, bấm nút Về trang web / Tiếp tục để redirect
      cy.contains(/Trở về|Về trang|Hoàn thành|Tiếp tục/i, { timeout: 20000 })
        .should('be.visible')
        .click({ force: true });
    });

    // 6. Sau khi thanh toán thành công, hệ thống tự động redirect về trang web của mình (VNPay xử lý mất 10-15s)
    // Hệ thống sẽ xoá URL params và hiển thị alert
    cy.url({ timeout: 20000 }).should('include', 'localhost:8080');

    // Vì Javascript của web đã tự động gọi alert() và xoá param vnp_ResponseCode khỏi URL, 
    // chúng ta chỉ cần verify alert đã được Cypress tự động bắt và chứa chữ thành công
    cy.on('window:alert', (str) => {
      expect(str).to.include('thành công');
    });
  });
});
