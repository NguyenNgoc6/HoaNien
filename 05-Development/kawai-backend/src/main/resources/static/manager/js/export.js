/* export.js — Xuất báo cáo: form handling + format selection */

let selectedFormat = 'xlsx';

document.addEventListener('DOMContentLoaded', () => {
    setDefaultDates();
});

function setDefaultDates() {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    const fmt = d => d.toISOString().split('T')[0];
    const fromEl = document.getElementById('export-from');
    const toEl = document.getElementById('export-to');
    if (fromEl) fromEl.value = fmt(firstDay);
    if (toEl) toEl.value = fmt(today);
}

function selectFormat(fmt) {
    selectedFormat = fmt;
    document.querySelectorAll('.mgr-format-chip').forEach(el => el.classList.remove('active'));
    const chip = document.getElementById('fmt-' + fmt);
    if (chip) chip.classList.add('active');
}

function handleExport() {
    const type = document.getElementById('export-type')?.value || 'revenue';
    const from = document.getElementById('export-from')?.value;
    const to = document.getElementById('export-to')?.value;

    if (!from || !to) {
        alert('Vui lòng chọn khoảng thời gian trước khi xuất báo cáo.');
        return;
    }
    if (from > to) {
        alert('"Từ ngày" phải nhỏ hơn hoặc bằng "Đến ngày".');
        return;
    }

    const typeLabels = {
        revenue: 'Doanh thu tổng hợp',
        room: 'Báo cáo phòng',
        fnb: 'Báo cáo F&B',
        tour: 'Báo cáo Tour',
        occupancy: 'Tỷ lệ lấp đầy',
        stay: 'Thời gian lưu trú'
    };
    const label = typeLabels[type] || type;

    // 1. Thêm bản ghi mới vào bảng với trạng thái Đang xử lý
    const tbody = document.getElementById('export-history-body');
    if (!tbody) {
        alert(`Bắt đầu xuất báo cáo: ${label}`);
        return;
    }

    const rowId = 'export-' + Date.now();
    const now = new Date();
    const dateStr = now.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' }) + ' ' +
        now.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });

    const tr = document.createElement('tr');
    tr.id = rowId;
    tr.innerHTML = `
        <td>${label}</td>
        <td><span style="padding:2px 8px;border-radius:4px;background:rgba(201,169,110,0.12);color:#C9A96E;font-size:11px;font-weight:500;text-transform:uppercase;">CSV</span></td>
        <td>${dateStr}</td>
        <td>Manager</td>
        <td id="${rowId}-size">--</td>
        <td id="${rowId}-status">
            <span style="display:flex;align-items:center;gap:6px;color:#C9A96E;font-weight:500;font-size:13px;">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="spin-anim"><path d="M21 12a9 9 0 1 1-6.219-8.56"></path></svg>
                Đang xử lý
            </span>
        </td>
        <td>
            <button id="${rowId}-btn" disabled style="padding:4px 10px;border-radius:5px;border:1px solid rgba(201,169,110,0.3);background:transparent;color:#C9A96E;font-size:11px;cursor:not-allowed;opacity:0.5;">Tải lại</button>
        </td>
    `;
    tbody.prepend(tr);

    if (!document.getElementById('spin-style')) {
        const style = document.createElement('style');
        style.id = 'spin-style';
        style.innerHTML = `@keyframes spin { 100% { transform: rotate(360deg); } } .spin-anim { animation: spin 1s linear infinite; }`;
        document.head.appendChild(style);
    }

    // 2. Gửi request thực tế lên Server
    const url = `/api/export?type=${type}&from=${from}&to=${to}&format=csv`;

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.blob().then(blob => ({ blob, headers: response.headers }));
        })
        .then(({ blob, headers }) => {
            const statusTd = document.getElementById(`${rowId}-status`);
            const btn = document.getElementById(`${rowId}-btn`);
            const sizeTd = document.getElementById(`${rowId}-size`);

            if (statusTd && btn && sizeTd) {
                statusTd.innerHTML = `
                    <span style="display:flex;align-items:center;gap:4px;color:#7A9C7A;font-weight:500;font-size:13px;">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
                        Thành công
                    </span>
                `;
                const sizeKb = (blob.size / 1024).toFixed(2);
                sizeTd.innerText = sizeKb + ' KB';
                
                // Kích hoạt nút Tải lại
                btn.disabled = false;
                btn.style.cursor = 'pointer';
                btn.style.opacity = '1';
                btn.onclick = () => {
                    const objectUrl = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = objectUrl;
                    a.download = `baocao_${type}_${Date.now()}.csv`;
                    document.body.appendChild(a);
                    a.click();
                    window.URL.revokeObjectURL(objectUrl);
                    a.remove();
                };
                
                // Tự động tải xuống ngay khi xuất thành công
                btn.click();
            }
        })
        .catch(err => {
            console.error('Export Error:', err);
            const statusTd = document.getElementById(`${rowId}-status`);
            const sizeTd = document.getElementById(`${rowId}-size`);
            if (statusTd && sizeTd) {
                statusTd.innerHTML = `
                    <span style="display:flex;align-items:center;gap:4px;color:#D9534F;font-weight:500;font-size:13px;">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>
                        Lỗi
                    </span>
                `;
                sizeTd.innerText = '--';
            }
        });
}

function toggleSubmenu(id, arrowId) {
    const s = document.getElementById(id);
    const a = document.getElementById(arrowId);
    if (!s || !a) return;
    const open = s.classList.contains('open');
    document.querySelectorAll('.mgr-submenu').forEach(e => e.classList.remove('open'));
    document.querySelectorAll('.mgr-nav-arrow').forEach(e => e.classList.remove('open'));
    if (!open) { s.classList.add('open'); a.classList.add('open'); }
}
