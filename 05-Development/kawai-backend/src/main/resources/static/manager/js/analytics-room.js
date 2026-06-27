/* analytics-room.js — Phân tích Phòng & Lưu trú: line chart (occupancy) + bar chart (stay distribution) */

const C_ROOM='#C9A96E';
const C_ROOM_BAR='rgba(201,169,110,0.85)';
const C_GRID='rgba(44,42,30,0.07)',C_TICK='#8B7355';

Chart.defaults.font.family="'Inter',sans-serif";
Chart.defaults.font.size=12; Chart.defaults.color=C_TICK;
Chart.defaults.plugins.legend.display=false;
Chart.defaults.plugins.tooltip.backgroundColor='rgba(28,26,20,0.92)';
Chart.defaults.plugins.tooltip.titleColor='#F5F0E8';
Chart.defaults.plugins.tooltip.bodyColor='#C9A96E';
Chart.defaults.plugins.tooltip.padding=10;
Chart.defaults.plugins.tooltip.cornerRadius=8;

document.addEventListener('DOMContentLoaded',()=>{
    // 1. Line Chart: Occupancy
    const ctxOcc=document.getElementById('chart-occ-line');
    if(ctxOcc) {
        const ctx2d = ctxOcc.getContext('2d');
        const grad = ctx2d.createLinearGradient(0, 0, 0, 240);
        grad.addColorStop(0, 'rgba(201,169,110,0.35)');
        grad.addColorStop(1, 'rgba(201,169,110,0.0)');

        const vals=window.chartOccVals||[72,75,78,80,82,85,88,91,89,87,85,83,80,78,76,79,82,85,88,92,94,90,87,84,81,79,77,80,83,86];
        const labels=window.chartOccLabels||Array.from({length:30},(_,i)=>i%5===0?`${i+1}/06`:'');
        new Chart(ctxOcc,{type:'line',data:{labels,datasets:[{label:'Occupancy',data:vals,borderColor:C_ROOM,backgroundColor:grad,borderWidth:2,pointRadius:0,pointHoverRadius:4,tension:0.4,fill:true}]},options:{responsive:true,maintainAspectRatio:false,interaction:{mode:'index',intersect:false},plugins:{legend:{display:false},tooltip:{callbacks:{title:i=>`Ngày ${i[0].dataIndex+1}/06`,label:c=>` Lấp đầy: ${c.parsed.y}%`}}},scales:{x:{grid:{display:false},border:{display:false},ticks:{color:C_TICK,maxRotation:0}},y:{min:0,max:100,grid:{color:C_GRID},border:{display:false},ticks:{color:C_TICK,callback:v=>v+'%',stepSize:10}}}}});
    }

    // 2. Bar Chart: Stay Distribution
    const ctxStay=document.getElementById('chart-stay-bar');
    if(ctxStay) {
        const d=window.stayDistData||{labels:["1 ngày","2 ngày","3 ngày","4 ngày","5 ngày","6+ ngày"],values:[58,72,85,61,28,16]};
        new Chart(ctxStay,{type:'bar',data:{labels:d.labels,datasets:[{label:'Lượt khách',data:d.values,backgroundColor:C_ROOM_BAR,borderRadius:4,borderSkipped:false}]},options:{responsive:true,maintainAspectRatio:false,plugins:{legend:{display:false},tooltip:{callbacks:{label:c=>` ${c.parsed.y} lượt khách`}}},scales:{x:{grid:{display:false},border:{display:false},ticks:{color:C_TICK}},y:{grid:{color:C_GRID},border:{display:false},ticks:{color:C_TICK}}}}});
    }
});
