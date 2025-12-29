let revenueChart;
let currentReportData = null;

document.addEventListener('DOMContentLoaded', function() {
    const reportForm = document.getElementById('reportForm');
    const exportBtn = document.getElementById('exportBtn');
    
    reportForm.addEventListener('submit', function(e) {
        e.preventDefault();
        generateReport();
    });
    
    exportBtn.addEventListener('click', function() {
        exportToExcel();
    });
    
    // Validate date inputs
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    
    startDateInput.addEventListener('change', validateDates);
    endDateInput.addEventListener('change', validateDates);
});

function validateDates() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    
    if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
        alert('Khoảng thời gian không hợp lệ. Ngày bắt đầu phải nhỏ hơn ngày kết thúc.');
        return false;
    }
    return true;
}

async function generateReport() {
    if (!validateDates()) return;
    
    const formData = new FormData(document.getElementById('reportForm'));
    const submitBtn = document.querySelector('button[type="submit"]');
    
    // Show loading
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<span class="loading-spinner"></span> Đang tạo báo cáo...';
    submitBtn.disabled = true;
    
    try {
        const response = await fetch('/admin/revenue/generate', {
            method: 'POST',
            body: formData
        });
        
        if (response.ok) {
            const reportData = await response.json();
            currentReportData = reportData;
            displayReport(reportData);
        } else {
            throw new Error('Lỗi tạo báo cáo');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Có lỗi xảy ra khi tạo báo cáo: ' + error.message);
        showNoDataMessage();
    } finally {
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    }
}

async function loadQuickReport(type) {
    const submitBtn = document.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<span class="loading-spinner"></span> Đang tải...';
    submitBtn.disabled = true;
    
    try {
        const response = await fetch(`/admin/revenue/quick/${type}`);
        
        if (response.ok) {
            const reportData = await response.json();
            currentReportData = reportData;
            
            // Update form values
            document.getElementById('startDate').value = reportData.startDate;
            document.getElementById('endDate').value = reportData.endDate;
            document.getElementById('reportType').value = reportData.reportType;
            
            displayReport(reportData);
        } else {
            throw new Error('Lỗi tải báo cáo nhanh');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Có lỗi xảy ra khi tải báo cáo: ' + error.message);
    } finally {
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    }
}

function displayReport(reportData) {
    if (!reportData.orderDetails || reportData.orderDetails.length === 0) {
        showNoDataMessage();
        return;
    }
    
    // Hide no data message and show results
    document.getElementById('noDataMessage').classList.add('d-none');
    document.getElementById('reportResults').classList.remove('d-none');
    
    // Update summary cards
    document.getElementById('totalRevenue').textContent = 
        formatCurrency(reportData.totalRevenue) + ' VND';
    document.getElementById('totalOrders').textContent = 
        reportData.totalCompletedOrders.toLocaleString();
    document.getElementById('averageValue').textContent = 
        formatCurrency(reportData.averageOrderValue) + ' VND';
    
    // Update chart
    updateChart(reportData.dailyBreakdown);
    
    // Update table
    updateOrdersTable(reportData.orderDetails);
    
    // Enable export button
    document.getElementById('exportBtn').disabled = false;
}

function showNoDataMessage() {
    document.getElementById('reportResults').classList.add('d-none');
    document.getElementById('noDataMessage').classList.remove('d-none');
    document.getElementById('exportBtn').disabled = true;
    currentReportData = null;
}

function updateChart(dailyData) {
    const ctx = document.getElementById('revenueChart').getContext('2d');
    
    if (revenueChart) {
        revenueChart.destroy();
    }
    
    const labels = dailyData.map(day => formatDate(day.date));
    const revenues = dailyData.map(day => day.revenue);
    const orderCounts = dailyData.map(day => day.orderCount);
    
    revenueChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Doanh thu (VND)',
                data: revenues,
                borderColor: '#0d6efd',
                backgroundColor: 'rgba(13, 110, 253, 0.1)',
                tension: 0.4,
                yAxisID: 'y'
            }, {
                label: 'Số đơn hàng',
                data: orderCounts,
                borderColor: '#198754',
                backgroundColor: 'rgba(25, 135, 84, 0.1)',
                tension: 0.4,
                yAxisID: 'y1'
            }]
        },
        options: {
            responsive: true,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                title: {
                    display: false
                },
                legend: {
                    position: 'top'
                }
            },
            scales: {
                x: {
                    display: true,
                    title: {
                        display: true,
                        text: 'Ngày'
                    }
                },
                y: {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    title: {
                        display: true,
                        text: 'Doanh thu (VND)'
                    },
                    ticks: {
                        callback: function(value) {
                            return formatCurrency(value);
                        }
                    }
                },
                y1: {
                    type: 'linear',
                    display: true,
                    position: 'right',
                    title: {
                        display: true,
                        text: 'Số đơn hàng'
                    },
                    grid: {
                        drawOnChartArea: false,
                    },
                }
            }
        }
    });
}

function updateOrdersTable(orders) {
    const tbody = document.getElementById('ordersTableBody');
    tbody.innerHTML = '';
    
    orders.forEach(order => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><span class="badge bg-secondary">#${order.orderId}</span></td>
            <td>${escapeHtml(order.customerName)}</td>
            <td>${escapeHtml(order.customerEmail)}</td>
            <td>${formatDate(order.orderDate)}</td>
            <td><strong>${formatCurrency(order.totalAmount)} VND</strong></td>
            <td><span class="badge bg-success">${escapeHtml(order.status)}</span></td>
        `;
        tbody.appendChild(row);
    });
}

async function exportToExcel() {
    if (!currentReportData) return;
    
    const exportBtn = document.getElementById('exportBtn');
    const originalText = exportBtn.innerHTML;
    exportBtn.innerHTML = '<span class="loading-spinner"></span> Đang xuất...';
    exportBtn.disabled = true;
    
    try {
        const params = new URLSearchParams({
            startDate: currentReportData.startDate,
            endDate: currentReportData.endDate
        });
        
        const response = await fetch(`/admin/revenue/export?${params}`);
        
        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `BaoCaoDoanhThu_${currentReportData.startDate}_${currentReportData.endDate}.csv`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        } else {
            throw new Error('Lỗi xuất file');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Có lỗi xảy ra khi xuất file: ' + error.message);
    } finally {
        exportBtn.innerHTML = originalText;
        exportBtn.disabled = false;
    }
}

// Utility functions
function formatCurrency(amount) {
    if (typeof amount === 'string') {
        amount = parseFloat(amount);
    }
    return amount.toLocaleString('vi-VN', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    });
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}