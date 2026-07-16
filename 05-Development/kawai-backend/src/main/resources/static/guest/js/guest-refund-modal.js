let currentGuestRefundType = 'FOOD';

function openGuestRefundModal(orderId) {
    document.getElementById('guestRefundOrderId').value = orderId;
    currentGuestRefundType = 'FOOD';
    document.getElementById('guestRefundModal').style.display = 'flex';
}

function openRefundModal(orderId, type) {
    document.getElementById('guestRefundOrderId').value = orderId;
    currentGuestRefundType = type;
    document.getElementById('guestRefundModal').style.display = 'flex';
}

function closeGuestRefundModal() {
    document.getElementById('guestRefundModal').style.display = 'none';
    document.getElementById('guestRefundForm').reset();
}

function submitGuestRefundForm(event) {
    event.preventDefault();
    
    const orderId = document.getElementById('guestRefundOrderId').value;
    const btn = document.getElementById('btn-submit-guest-refund');
    
    const payload = {
        bankName: document.getElementById('guestRefundBankName').value,
        accountNumber: document.getElementById('guestRefundAccountNumber').value,
        accountName: document.getElementById('guestRefundAccountName').value,
        phoneNumber: document.getElementById('guestRefundPhoneNumber').value,
        reason: 'Khách hàng tự hủy trên Profile'
    };
    
    if (!confirm('Xác nhận gửi thông tin hoàn tiền và hủy đơn hàng này?')) return;
    
    btn.disabled = true;
    btn.innerText = 'Đang xử lý...';
    
    let apiUrl = '/api/pos/orders/' + orderId + '/cancel';
    if (currentGuestRefundType === 'ROOM') {
        apiUrl = '/api/bookings/' + orderId + '/cancel';
    } else if (currentGuestRefundType === 'TOUR') {
        apiUrl = '/profile/tours/cancel/' + orderId;
    }
    
    fetch(apiUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
    .then(res => {
        if(!res.ok) return res.json().then(e => { throw new Error(e.message || 'Lỗi xử lý hoàn tiền') });
        return res.json();
    })
    .then(data => {
        alert(data.message || 'Đã tạo yêu cầu hoàn tiền và hủy đơn thành công!');
        window.location.reload();
    })
    .catch(err => {
        alert('Lỗi: ' + err.message);
        btn.disabled = false;
        btn.innerText = 'Gửi Yêu cầu Hủy đơn';
    });
}
