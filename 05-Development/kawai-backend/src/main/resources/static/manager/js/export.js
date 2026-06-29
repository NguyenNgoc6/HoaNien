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
    const to   = document.getElementById('export-to')?.value;

    if (!from || !to) {
        alert('Vui lòng chọn khoảng thời gian trước khi xuất báo cáo.');
        return;
    }
    if (from > to) {
        alert('"Từ ngày" phải nhỏ hơn hoặc bằng "Đến ngày".');
        return;
    }

    const btn = document.getElementById('btn-export');
    const origText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="animation:spin 1s linear infinite"><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"/></svg> Đang xuất...';

    const url = `/manager/export/download?type=${encodeURIComponent(type)}&from=${from}&to=${to}&format=${selectedFormat}`;
    window.location.href = url;

    // Restore button after 3 seconds
    setTimeout(() => {
        btn.disabled = false;
        btn.innerHTML = origText;
        // Reload page to refresh history table
        setTimeout(() => location.reload(), 500);
    }, 3000);
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
