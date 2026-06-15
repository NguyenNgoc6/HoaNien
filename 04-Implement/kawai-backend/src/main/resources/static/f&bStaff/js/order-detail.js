/**
 * ORDER DETAIL - MOCK LOGIC FOR DEMONSTRATION
 */

// Mock logic removed, using Thymeleaf dynamic rendering instead
document.addEventListener('DOMContentLoaded', () => {
    // Payment logic
    const btnPay = document.getElementById('btn-pay');
    if (btnPay) {
        btnPay.addEventListener('click', () => {
            const orderIdText = document.getElementById('order-id').innerText;
            const orderId = orderIdText.replace('ORD-', '').trim();
            if (confirm('Xác nhận thanh toán đơn hàng này?')) {
                fetch(`/api/pos/orders/${orderId}/pay`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' }
                })
                .then(res => res.json())
                .then(data => {
                    if (data.status === 'success') {
                        alert('Thanh toán thành công!');
                        location.reload();
                    } else {
                        alert('Lỗi: ' + data.error);
                    }
                })
                .catch(err => {
                    console.error(err);
                    alert('Lỗi kết nối khi thanh toán');
                });
            }
        });
    }
});
