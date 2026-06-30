describe('Module 3: Khách hàng đặt món (Room Service) - Kịch bản thành công 2', () => {
  beforeEach(() => {
    cy.viewport(1440, 900);
    cy.visit('http://localhost:8080/living');
    cy.contains('Log In').click({ force: true });

    cy.get('#ajaxLoginForm input[name="username"]').type('tmduc');
    cy.get('#ajaxLoginForm input[name="password"]').type('admin123');
    cy.get('#ajaxLoginForm button[type="submit"]').click();
    cy.wait(1500);

    cy.contains('Dining').click({ force: true });
    cy.url().should('include', '/dining');
    cy.contains('Order Food').click({ force: true });
  });

  it('Lọc món theo danh mục, chỉnh sửa giỏ hàng và đặt món ghi nợ phòng', () => {
    // Add item 1
    cy.get('#menuSearchInput').clear().type('Bò');
    cy.contains('Bò').first().click({ force: true });
    cy.get('#modalAddToCartBtn').should('be.visible').click();

    // Add item 2
    cy.get('#menuSearchInput').clear().type('Nước');
    cy.contains('Nước').first().click({ force: true });
    cy.get('#modalAddToCartBtn').should('be.visible').click();

    // Thanh toán ghi nợ phòng
    cy.get('input[value="CHARGE_TO_ROOM"]').check({ force: true });
    cy.get('#orderConsent').check({ force: true });

    cy.intercept('POST', '**/api/pos/orders').as('createOrder2');
    cy.get('#submitOrderBtn').click();

    cy.wait('@createOrder2').its('response.statusCode').should('eq', 200);
  });
});
