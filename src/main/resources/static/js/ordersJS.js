let currentStatus = 'PAID';
let currentPages = {
    PAID: 0,
    CANCELLED: 0
};

// Helper function to get table body element
function getTableBodyElement(status) {
    const tableMap = {
        'PAID': '#paidTableBody',
        'CANCELLED': '#cancelledTableBody'
    };
    
    const selector = tableMap[status] || '#paidTableBody';
    return $(selector);
}

// Helper function to get pagination element
function getPaginationElement(status) {
    const paginationMap = {
        'PAID': '#paidPagination',
        'CANCELLED': '#cancelledPagination'
    };
    
    const selector = paginationMap[status] || '#paidPagination';
    return $(selector);
}

// Helper function to get badge element
function getBadgeElement(status) {
    const badgeMap = {
        'PAID': 'paidCount',
        'CANCELLED': 'cancelledCount'
    };
    
    const badgeId = badgeMap[status];
    return document.getElementById(badgeId);
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
function createAjaxData(status, page = 0, size = 10) {
    const filters = getFilterValues();
    return {
        username: filters.username || null,
        amountDirection: filters.amountDirection || null,
        orderType: filters.orderType || null,
        startDate: filters.startDate || null,
        endDate: filters.endDate || null,
        status: status,
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
        tableBody.html('<tr><td colspan="8" class="text-center">No orders found</td></tr>');
    }

    // Update pagination
    if (paginationData && paginationData.trim() !== '') {
        paginationContainer.html(paginationData);
    } else {
        paginationContainer.html('');
    }

    // Update count badge
    updateStatusCountBadge(currentStatus, countData);
}

// Helper function to handle AJAX error
function handleAjaxError(error, tableBody, paginationContainer) {
    console.error("Error loading data for status:", currentStatus, error);
    tableBody.html('<tr><td colspan="8" class="text-center text-danger">Error loading data</td></tr>');
    paginationContainer.html('');
}

function filterOrders(status, page = 0, size = 10) {
    const tableBody = getTableBodyElement(status);
    const paginationContainer = getPaginationElement(status);

    if (!tableBody || tableBody.length === 0) {
        console.error("Table body not found for status:", status);
        return;
    }

    // Update current page for this status
    currentPages[status] = page;

    // Show loading state
    tableBody.html('<tr><td colspan="8" class="text-center">Loading...</td></tr>');

    const ajaxData = createAjaxData(status, page, size);

    // Load table data and pagination simultaneously
    Promise.all([
        // Load table rows
        $.ajax({
            url: '/admin/orders/filter',
            method: 'GET',
            data: ajaxData
        }),
        // Load pagination
        $.ajax({
            url: '/admin/orders/pagination',
            method: 'GET',
            data: ajaxData
        }),
        // Load total count for badge
        $.ajax({
            url: '/admin/orders/count',
            method: 'GET',
            data: { ...ajaxData, page: 0, size: 1 }
        })
    ]).then(function ([tableData, paginationData, countData]) {
        handleAjaxResponse(tableData, paginationData, countData, tableBody, paginationContainer);
    }).catch(function (error) {
        handleAjaxError(error, tableBody, paginationContainer);
    });
}

function updateStatusCountBadge(status, totalCount) {
    const badgeElement = getBadgeElement(status);

    if (!badgeElement) {
        console.error("Badge element not found for status:", status);
        return;
    }

    // Set the total count from server response
    const count = typeof totalCount === 'number' ? totalCount : (totalCount ? parseInt(totalCount) : 0);
    badgeElement.textContent = count;
}

function loadOrders(status, page = 0) {
    console.log("Loading orders for status:", status, "page:", page);
    filterOrders(status, page);
}

// Helper function to reset pagination and filter
function resetAndFilter() {
    currentPages[currentStatus] = 0;
    filterOrders(currentStatus, 0);
}

// Event handler for pagination clicks
$(document).on('click', '.pagination .page-link', function (e) {
    e.preventDefault();
    const page = parseInt($(this).data('page'));
    if (!isNaN(page) && page >= 0) {
        console.log("Pagination clicked - page:", page, "for status:", currentStatus);
        filterOrders(currentStatus, page);
    }
});

$(document).ready(function () {
    // Load initial data for all tabs
    const statuses = ['PAID', 'CANCELLED'];
    statuses.forEach(status => {
        loadOrders(status, 0);
    });

    // Tab change event
    $('button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
        const status = $(this).data('status');
        console.log("Tab changed to:", status);
        currentStatus = status;
        // Load current page for this status
        filterOrders(currentStatus, currentPages[currentStatus] || 0);
    });

    // Filter events with debouncing
    const filterSelectors = ['#filterAmount', '#filterOrderType', '#filterStartDate', '#filterEndDate'];
    filterSelectors.forEach(selector => {
        $(selector).on('change', function () {
            console.log("Filter changed:", $(this).attr('id'), $(this).val());
            resetAndFilter();
        });
    });

    // Search input with debounce
    let searchTimer;
    $('#filterUsername').on('input', function () {
        const searchValue = $(this).val();
        console.log("Search input triggered - value:", searchValue);
        clearTimeout(searchTimer);
        searchTimer = setTimeout(resetAndFilter, 500);
    });

    // Prevent form submission
    $('#filterForm').on('submit', function (e) {
        e.preventDefault();
        resetAndFilter();
    });
}); 