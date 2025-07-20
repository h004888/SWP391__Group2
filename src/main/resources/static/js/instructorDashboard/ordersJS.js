let currentTab = 'INVOICE';
let currentPage = 0;

// Helper function to get table body element
function getTableBodyElement() {
    return $('#invoiceTableBody');
}

// Helper function to get pagination element
function getPaginationElement() {
    return $('#invoicePagination');
}

// Helper function to get badge element
function getBadgeElement() {
    return document.getElementById('invoiceCount');
}

// Helper function to get filter values
function getFilterValues() {
    return {
        username: $('#filterUsername').val()?.trim() || '',
        amountDirection: $('#filterAmount').val() || '',
        orderType: $('#filterOrderType').val() || '',
        startDate: $('#filterStartDate').val() || '',
        endDate: $('#filterEndDate').val() || ''
    };
}

// Helper function to create AJAX data object
function createAjaxData(page = 0, size = 5) {
    const filters = getFilterValues();
    return {
        username: filters.username || null,
        amountDirection: filters.amountDirection || null,
        orderType: filters.orderType || null,
        startDate: filters.startDate || null,
        endDate: filters.endDate || null,
        page: page,
        size: size
    };
}

// Helper function to handle AJAX response
function handleAjaxResponse(tableData, paginationData, countData, tableBody, paginationContainer) {
    // Update table
    if (tableData && tableData.trim() !== '') {
        tableBody.html(tableData);
    } else {
        tableBody.html('<tr><td colspan="6" class="text-center">No orders found</td></tr>');
    }

    // Update pagination
    if (paginationData && paginationData.trim() !== '') {
        paginationContainer.html(paginationData);
    } else {
        paginationContainer.html('');
    }

    // Update count badge
    updateCountBadge(countData);
}

// Helper function to handle AJAX error
function handleAjaxError(error, tableBody, paginationContainer) {
    console.error("Error loading data for invoice tab:", error);
    tableBody.html('<tr><td colspan="6" class="text-center text-danger">Error loading data</td></tr>');
    paginationContainer.html('');
}

function filterInvoices(page = 0, size = 5) {
    const tableBody = getTableBodyElement();
    const paginationContainer = getPaginationElement();

    currentPage = page;
    tableBody.html('<tr><td colspan="6" class="text-center">Loading...</td></tr>');

    const ajaxData = createAjaxData(page, size);

    $.ajax({
        url: '/instructor/orders/filter',
        method: 'GET',
        data: ajaxData,
        success: function (data) {
            tableBody.html(data.table);
            paginationContainer.html(data.pagination);
        },
        error: function (error) {
            handleAjaxError(error, tableBody, paginationContainer);
        }
    });
}

function updateCountBadge(totalCount) {
    const badgeElement = getBadgeElement();
    if (!badgeElement) {
        console.error("Badge element not found for invoice tab");
        return;
    }
    // Set the total count from server response
    const count = typeof totalCount === 'number' ? totalCount : (totalCount ? parseInt(totalCount) : 0);
    badgeElement.textContent = count;
}

function loadInvoices(page = 0) {
    filterInvoices(page, 5);
}

function loadStatistics() {
    $.ajax({
        url: '/instructor/orders/statistics',
        method: 'GET',
        data: {},
        success: function (data) {
            // Đổ bảng doanh thu từng khóa
            let tableHtml = '';
            if (data.courseSales && data.courseSales.length > 0) {
                data.courseSales.forEach(cs => {
                    tableHtml += `<tr>
                        <td>${cs.courseName}</td>
                        <td>${cs.sold}</td>
                        <td>${cs.revenue.toLocaleString()}</td>
                    </tr>`;
                });
            } else {
                tableHtml = '<tr><td colspan="3" class="text-center">No data</td></tr>';
            }
            $('#courseSalesTable').html(tableHtml);

            // Best seller, least seller
            $('#bestSeller').text(data.bestSeller ? data.bestSeller.courseName + ' (' + data.bestSeller.sold + ')' : 'N/A');
            $('#leastSeller').text(data.leastSeller ? data.leastSeller.courseName + ' (' + data.leastSeller.sold + ')' : 'N/A');

            // Biểu đồ doanh thu theo tháng
            if (window.courseMonthlyChartInstance) {
                window.courseMonthlyChartInstance.destroy();
            }
            const ctx = document.getElementById('courseMonthlyChart').getContext('2d');
            const labels = Object.keys(data.monthlyRevenue || {});
            const values = Object.values(data.monthlyRevenue || {});
            window.courseMonthlyChartInstance = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Revenue',
                        data: values,
                        backgroundColor: 'rgba(54, 162, 235, 0.5)'
                    }]
                },
                options: {
                    responsive: true,
                    plugins: { legend: { display: false } }
                }
            });
        }
    });
}

// Helper function to reset pagination and filter
function resetAndFilter() {
    currentPage = 0;
    filterInvoices(0, 5);
}

// Event handler for pagination clicks
$(document).on('click', '.pagination .page-link', function (e) {
    console.log('Clicked page link', $(this).data('page'), $(this).parent().attr('class'));
    e.preventDefault();
    const page = parseInt($(this).data('page'));
    if (!isNaN(page) && page >= 0) {
        filterInvoices(page, 5);
    }
});

$(document).ready(function () {
    // Load initial data for invoice tab
    loadInvoices(0);
    loadStatistics();

    // Tab change event
    $('button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
        const tabId = $(this).attr('id');
        if (tabId === 'invoice-tab') {
            loadInvoices(currentPage || 0);
        }
    });

    // Filter events with debouncing
    const filterSelectors = ['#filterAmount', '#filterOrderType', '#filterStartDate', '#filterEndDate'];
    filterSelectors.forEach(selector => {
        $(selector).on('change', function () {
            resetAndFilter();
            loadStatistics();
        });
    });

    // Search input with debounce
    let searchTimer;
    $('#filterUsername').on('input', function () {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => {
            resetAndFilter();
            loadStatistics();
        }, 400);
    });
}); 