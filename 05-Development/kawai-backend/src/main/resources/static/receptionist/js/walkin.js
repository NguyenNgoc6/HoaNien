document.addEventListener('DOMContentLoaded', () => {
    const typeSelect = document.getElementById('walkInTypeSelect');
    if (!typeSelect || typeof roomInventory === 'undefined') return;

    for (const category in roomInventory) {
        const opt = document.createElement('option');
        opt.value = category;
        opt.innerText = category;
        typeSelect.appendChild(opt);
    }

    // Set min date for checkoutDate and max date for guestDob to today (Local Time)
    const checkoutDateInput = document.getElementById('checkoutDate');
    const guestDobInput = document.getElementById('guestDob');
    if (checkoutDateInput || guestDobInput) {
        const localDate = new Date();
        const year = localDate.getFullYear();
        const month = String(localDate.getMonth() + 1).padStart(2, '0');
        const day = String(localDate.getDate()).padStart(2, '0');
        const todayStr = `${year}-${month}-${day}`;
        if (checkoutDateInput) checkoutDateInput.min = todayStr;
        if (guestDobInput) guestDobInput.max = todayStr;
    }
});

function updateWalkInAvailableRooms() {
    const typeSelect = document.getElementById('walkInTypeSelect');
    const roomSelect = document.getElementById('walkInPhysicalRoomSelect');
    roomSelect.innerHTML = '<option value="">-- Select Physical Room --</option>';

    const selectedType = typeSelect.value;
    if (!selectedType) return;

    const available = roomInventory[selectedType] || [];
    available.forEach(r => {
        const opt = document.createElement('option');

        const roomId = r.id !== undefined ? r.id : r;
        const roomNum = r.number !== undefined ? r.number : r;
        opt.value = roomId;
        opt.innerText = roomNum + ' (' + selectedType + ')';
        roomSelect.appendChild(opt);
    });
}

function unlockStep2() {
    const roomSelect = document.getElementById('walkInPhysicalRoomSelect');
    if (roomSelect.value) {
        const step2 = document.getElementById('step2Container');
        step2.style.opacity = '1';
        step2.style.pointerEvents = 'auto';
        step2.style.border = '1px solid #bae6fd';
        step2.style.boxShadow = '0 0 10px rgba(2,132,199,0.1)';
    }
}

function unlockFinalButton(customerId) {
    document.getElementById('guestStatusMsg').style.display = 'inline-block';
    document.getElementById('mockCustomerId').innerText = customerId;
    const completeBtn = document.getElementById('completeBtn');
    completeBtn.style.opacity = '1';
    completeBtn.style.pointerEvents = 'auto';
}

function searchCustomer() {
    const phone = document.getElementById('guestPhone').value;
    const cccd = document.getElementById('guestId').value;
    const keyword = phone || cccd;
    if (!keyword) {
        alert("Vui lòng nhập Số điện thoại hoặc CCCD/Passport để tìm kiếm.");
        return;
    }

    fetch('/api/receptionist/walkin/search-customer?keyword=' + encodeURIComponent(keyword))
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 404) {
                throw new Error('Không tìm thấy khách hàng với SĐT hoặc CCCD vừa nhập.');
            } else {
                throw new Error('Lỗi hệ thống khi tìm kiếm khách hàng.');
            }
        })
        .then(data => {
            document.getElementById('guestName').value = data.fullName || '';
            document.getElementById('guestId').value = data.cccd || '';
            document.getElementById('guestEmail').value = data.email || '';
            if (data.dateOfBirth) {
                document.getElementById('guestDob').value = data.dateOfBirth;
            }

            unlockFinalButton("CUST-" + data.id);
            document.getElementById('guestStatusMsg').innerHTML = `<i class="fa-solid fa-check-circle"></i> Đã tìm thấy khách hàng cũ (ID: <span id="mockCustomerId">${data.id}</span>)`;
            document.getElementById('guestStatusMsg').style.color = '#16a34a';
            document.getElementById('guestStatusMsg').style.background = '#dcfce7';
        })
        .catch(error => {
            alert(error.message);
        });
}

function validateGuestInfo() {
    const name = document.getElementById('guestName').value;
    const phone = document.getElementById('guestPhone').value;
    const id = document.getElementById('guestId').value;
    const email = document.getElementById('guestEmail').value;
    const dob = document.getElementById('guestDob').value;

    if (!name || !phone || !id || !dob) {
        alert("Vui lòng điền đầy đủ thông tin bắt buộc (*), bao gồm cả Ngày sinh.");
        return false;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (new Date(dob) > today) {
        alert("LỖI: Ngày sinh không thể ở trong tương lai!");
        return false;
    }

    const phoneRegex = /^(0|\+84)[3|5|7|8|9][0-9]{8}$/;
    if (!phoneRegex.test(phone)) {
        alert("Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam hợp lệ (VD: 0901234567).");
        return false;
    }
    if (email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            alert("Định dạng Email không hợp lệ.");
            return false;
        }
    }
    const cccdRegex = /^\d{12}$/;
    if (!cccdRegex.test(id)) {
        alert("CCCD không hợp lệ. Vui lòng nhập đúng 12 chữ số CCCD.");
        return false;
    }

    return true;
}

function validateNewCustomer() {
    if (!validateGuestInfo()) return;

    unlockFinalButton("NEW-GUEST");
    document.getElementById('guestStatusMsg').innerHTML = `<i class="fa-solid fa-info-circle"></i> Sẵn sàng Check-in (Tài khoản sẽ được tạo tự động)`;
    document.getElementById('guestStatusMsg').style.display = 'inline-block';
    document.getElementById('guestStatusMsg').style.color = '#0284c7';
    document.getElementById('guestStatusMsg').style.background = '#e0f2fe';
}

function submitCheckIn() {
    const roomId = document.getElementById('walkInPhysicalRoomSelect').value;
    if (!roomId) {
        alert("Vui lòng chọn phòng để Check-in!");
        return;
    }

    const name = document.getElementById('guestName').value;
    const phone = document.getElementById('guestPhone').value;
    const cccd = document.getElementById('guestId').value;
    const email = document.getElementById('guestEmail').value;
    const dob = document.getElementById('guestDob').value;
    const checkOutDate = document.getElementById('checkoutDate').value;

    if (!checkOutDate) {
        alert("Vui lòng nhập Ngày Trả Phòng dự kiến!");
        return;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0); // Đặt về 0 giờ để so sánh đúng ngày
    const selectedDate = new Date(checkOutDate);
    if (selectedDate < today) {
        alert("LỖI: Ngày trả phòng (Checkout Date) không thể ở trong quá khứ!");
        return;
    }

    // Xác thực lại thông tin khách hàng trước khi gửi API (để tránh user sửa data sau khi Unlock)
    if (!validateGuestInfo()) return;

    const localNow = new Date();
    const checkInStr = `${localNow.getFullYear()}-${String(localNow.getMonth() + 1).padStart(2, '0')}-${String(localNow.getDate()).padStart(2, '0')}`;

    const payload = {
        roomId: parseInt(roomId),
        checkInDate: checkInStr,
        checkOutDate: checkOutDate,
        fullName: name,
        phone: phone,
        cccd: cccd,
        email: email,
        dateOfBirth: dob
    };

    // Đổi nút thành trạng thái loading
    const completeBtn = document.getElementById('completeBtn');
    completeBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Processing...';
    completeBtn.disabled = true;

    fetch('/api/receptionist/walkin/checkin', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.error || err.message || "Lỗi khi Check-in"); });
            }
            return response.json();
        })
        .then(data => {
            const modal = document.getElementById('successModal');
            const msg = document.getElementById('modalMessage');
            const accInfo = document.getElementById('modalAccountInfo');
            
            if (data.newCustomer && data.newAccountUsername) {
                msg.innerHTML = "Hệ thống đã <b>tự động tạo hồ sơ</b> khách hàng mới và làm thủ tục nhận phòng thành công.";
                document.getElementById('modalUsername').innerText = data.newAccountUsername;
                document.getElementById('modalPassword').innerText = data.newAccountPassword;
                accInfo.style.display = 'block';
            } else {
                msg.innerText = "Đã làm thủ tục nhận phòng hoàn tất cho khách lưu trú này.";
                accInfo.style.display = 'none';
            }
            
            if (modal) {
                modal.style.display = 'flex';
            } else {
                // Fallback nếu không có modal trong HTML
                alert("Check-in thành công!");
                window.location.href = "/receptionist/dashboard";
            }
        })
        .catch(error => {
            alert("Lỗi Check-in: " + error.message);
            completeBtn.innerHTML = '<i class="fa-solid fa-check-double"></i> Complete Walk-In Check-in';
            completeBtn.disabled = false;
        });
}

function closeModalAndRedirect() {
    document.getElementById('successModal').style.display = 'none';
    window.location.href = "/receptionist/dashboard";
}
