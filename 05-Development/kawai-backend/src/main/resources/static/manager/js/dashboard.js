/* dashboard.js — Tổng quan: bar chart, donut chart, line chart */

const C_ROOM = '#C9A96E', C_FNB = '#7A9C7A', C_TOUR = '#6B8C9A';
const C_GRID = 'rgba(44,42,30,0.07)', C_TICK = '#8B7355';

Chart.defaults.font.family = "'DM Sans',sans-serif";
Chart.defaults.font.size = 12;
Chart.defaults.color = C_TICK;
Chart.defaults.plugins.legend.display = false;
Chart.defaults.plugins.tooltip.backgroundColor = 'rgba(28,26,20,0.92)';
Chart.defaults.plugins.tooltip.titleColor = '#F5F0E8';
Chart.defaults.plugins.tooltip.bodyColor = '#C9A96E';
Chart.defaults.plugins.tooltip.padding = 10;
Chart.defaults.plugins.tooltip.cornerRadius = 8;
Chart.defaults.plugins.tooltip.displayColors = false;

document.addEventListener('DOMContentLoaded', () => {
    initLineToday(); initDonut(); initLine();
});

function initLineToday() {
    const ctx = document.getElementById('chart-line-today');
    if (!ctx) return;
    const d = window.todayData || { labels: ['06:00', '09:00', '12:00', '15:00', '18:00', '21:00'], room: [0,0,0,0,0,0], fnb: [0,0,0,0,0,0], tour: [0,0,0,0,0,0] };
    
    const sum = d.room.reduce((a,b)=>a+b, 0) + d.fnb.reduce((a,b)=>a+b, 0) + d.tour.reduce((a,b)=>a+b, 0);
    if (sum === 0) {
        ctx.parentElement.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:#9A9485;font-size:13px;font-style:italic;">Chưa có dữ liệu doanh thu trong ngày</div>';
        return;
    }

    // Đăng ký plugin datalabels nếu có (tuỳ chọn)
    let plugins = [];
    if (typeof ChartDataLabels !== 'undefined') {
        Chart.register(ChartDataLabels);
        plugins = [ChartDataLabels];
    }
    
    new Chart(ctx, {
        type: 'bar', 
        data: {
            labels: d.labels, 
            datasets: [
                { 
                    label: 'Phòng', 
                    data: d.room, 
                    backgroundColor: '#C9A96E'
                },
                { 
                    label: 'F&B', 
                    data: d.fnb, 
                    backgroundColor: '#7A9C7A'
                },
                { 
                    label: 'Tour', 
                    data: d.tour, 
                    backgroundColor: '#6B8C9A'
                }
            ]
        }, 
        plugins: plugins,
        options: { 
            responsive: true, 
            maintainAspectRatio: false, 
            plugins: { 
                legend: { display: false }, 
                tooltip: { callbacks: { label: c => ` ${c.dataset.label}: ${c.parsed.y}M` } },
                datalabels: {
                    align: 'center',
                    font: { weight: 'bold', size: 11 },
                    color: '#ffffff',
                    formatter: function(value) { return value > 0 ? value + 'M' : ''; }
                }
            }, 
            scales: { 
                x: { stacked: true, grid: { display: false }, border: { display: false }, ticks: { color: C_TICK } }, 
                y: { stacked: true, grid: { color: C_GRID }, border: { display: false }, ticks: { color: C_TICK, callback: v => v + 'M' } } 
            } 
        }
    });
}

function initDonut() {
    const ctx = document.getElementById('chart-donut');
    if (!ctx) return;
    const dList = window.donutDataList || [
        {name: 'Phòng', val: 1128, color: C_ROOM},
        {name: 'F&B', val: 487, color: C_FNB},
        {name: 'Tour', val: 231, color: C_TOUR}
    ];
    const labels = dList.map(item => item.name);
    const data = dList.map(item => item.val);
    const bgColors = dList.map(item => item.color);

    new Chart(ctx, { 
        type: 'doughnut', 
        data: { 
            labels: labels, 
            datasets: [{ 
                data: data, 
                backgroundColor: bgColors, 
                borderWidth: 1, 
                borderColor: '#ffffff',
                hoverOffset: 12,
                hoverBorderWidth: 2,
                hoverBorderColor: '#ffffff'
            }] 
        }, 
        options: { 
            responsive: true, 
            maintainAspectRatio: false, 
            cutout: '68%', 
            plugins: { 
                legend: { display: false }, 
                tooltip: { callbacks: { label: c => ` ${c.label}: ${c.parsed}M` } } 
            } 
        } 
    });
}

function initLine() {
    const ctx = document.getElementById('chart-line');
    if (!ctx) return;
    const vals = window.lineVals || [72, 75, 78, 80, 82, 85, 88, 91, 89, 87, 85, 83, 80, 78, 76, 79, 82, 85, 88, 92, 94, 90, 87, 84, 81, 79, 77, 80, 83, 86];
    const labels = window.lineLabels || Array.from({ length: 30 }, (_, i) => `${i + 1}/06`);
    const avg = vals.length > 0 ? Math.round(vals.reduce((a, b) => a + b, 0) / vals.length) : 0;
    const avgData = Array(vals.length).fill(avg);

    const gradient = ctx.getContext('2d').createLinearGradient(0, 0, 0, 200);
    gradient.addColorStop(0, 'rgba(201,169,110,0.3)');
    gradient.addColorStop(1, 'rgba(201,169,110,0.0)');

    new Chart(ctx, { 
        type: 'line', 
        data: { 
            labels: labels, 
            datasets: [
                { 
                    label: 'Tỷ lệ lấp đầy', 
                    data: vals, 
                    borderColor: C_ROOM, 
                    backgroundColor: gradient, 
                    borderWidth: 2, 
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: C_ROOM,
                    pointBorderWidth: 2,
                    pointRadius: 3, 
                    pointHoverRadius: 6, 
                    tension: 0.4, 
                    fill: true 
                },
                { 
                    label: 'TB tháng', 
                    data: avgData, 
                    borderColor: '#8B7355', 
                    borderWidth: 1, 
                    borderDash: [5, 5], 
                    pointRadius: 0, 
                    pointHoverRadius: 0, 
                    fill: false 
                }
            ] 
        }, 
        options: { 
            responsive: true, 
            maintainAspectRatio: false, 
            interaction: { mode: 'index', intersect: false }, 
            plugins: { 
                legend: { display: false }, 
                tooltip: { 
                    callbacks: { 
                        title: i => `Ngày ${i[0].label}`, 
                        label: c => ` ${c.dataset.label}: ${c.parsed.y}%` 
                    } 
                } 
            }, 
            scales: { 
                x: { 
                    grid: { display: false }, 
                    border: { display: false }, 
                    ticks: { 
                        color: C_TICK, 
                        maxRotation: 0,
                        autoSkip: false,
                        callback: function(value, index, ticks) {
                            if (index === 0 || index === ticks.length - 1 || index % 6 === 0) {
                                return this.getLabelForValue(value);
                            }
                            return '';
                        }
                    } 
                }, 
                y: { 
                    min: 0, 
                    max: 100, 
                    grid: { color: C_GRID }, 
                    border: { display: false }, 
                    ticks: { color: C_TICK, callback: v => v + '%', stepSize: 20 } 
                } 
            } 
        } 
    });
}

function toggleSubmenu(id, arrowId) {
    const s = document.getElementById(id), a = document.getElementById(arrowId);
    if (!s || !a) return;
    const open = s.classList.contains('open');
    document.querySelectorAll('.mgr-submenu').forEach(e => e.classList.remove('open'));
    document.querySelectorAll('.mgr-nav-arrow').forEach(e => e.classList.remove('open'));
    if (!open) { s.classList.add('open'); a.classList.add('open'); }
}
