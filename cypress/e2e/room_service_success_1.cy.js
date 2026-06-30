describe('Module 3: Khách hàng đặt món (Room Service) - Kịch bản thành công 1', () => {
  beforeEach(() => {
    cy.viewport(1440, 900); // Desktop view
    cy.visit('http://localhost:8080/living');
    cy.contains('Log In').click({ force: true });

    // Login
    cy.get('#ajaxLoginForm input[name="username"]').type('tmduc');
    cy.get('#ajaxLoginForm input[name="password"]').type('admin123');
    cy.get('#ajaxLoginForm button[type="submit"]').click();
    cy.wait(1500); // Wait for reload

    // Go to dining -> order food
    cy.contains('Dining').click({ force: true });
    cy.url().should('include', '/dining');
    cy.contains('Order Food').click({ force: true });
  });

  it('Thực hiện quy trình đặt món E-Menu và thanh toán Ghi nợ phòng', () => {
    // 1. Tìm món
    cy.get('#menuSearchInput').type('Bò');

    // 2. Click vào món để mở modal
    cy.contains('Bò').first().click({ force: true });

    // 3. Nhập ghi chú và thêm vào giỏ
    cy.get('#modalItemChefNote').should('be.visible').type('Nhiều hành, không lấy nước béo');
    cy.get('#modalAddToCartBtn').click();

    // 4. Kiểm tra giỏ hàng và điền ghi chú chung
    cy.get('#orderNote').type('Giao ngay lập tức');

    // 5. Chọn Ghi nợ phòng (CHARGE_TO_ROOM) và check consent
    cy.get('input[value="CHARGE_TO_ROOM"]').check({ force: true });
    cy.get('#orderConsent').check({ force: true });

    // 6. Đặt hàng
    cy.intercept('POST', '**/api/pos/orders').as('createOrder');
    cy.get('#submitOrderBtn').click();

    cy.wait('@createOrder').its('response.statusCode').should('eq', 200);

    // 7. Verify thành công
    cy.get('#posTimelineModal').should('be.visible');
  });
});
