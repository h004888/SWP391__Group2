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
function createAjaxData(page = 0, size = 10) {
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

function filterInvoices(page = 0, size = 10) {
    const tableBody = getTableBodyElement();
    const paginationContainer = getPaginationElement();

    // Update current page
    currentPage = page;

    // Show loading state
    tableBody.html('<tr><td colspan="6" class="text-center">Loading...</td></tr>');

    const ajaxData = createAjaxData(page, size);

    // Load table data and pagination simultaneously
    Promise.all([
        // Load table rows
        $.ajax({
            url: '/instructor/orders/filter',
            method: 'GET',
            data: ajaxData
        }),
        // Load pagination
        $.ajax({
            url: '/instructor/orders/pagination',
            method: 'GET',
            data: ajaxData
        }),
        // Load total count for badge
        $.ajax({
            url: '/instructor/orders/count',
            method: 'GET',
            data: { ...ajaxData, page: 0, size: 1 }
        })
    ]).then(function ([tableData, paginationData, countData]) {
        handleAjaxResponse(tableData, paginationData, countData, tableBody, paginationContainer);
    }).catch(function (error) {
        handleAjaxError(error, tableBody, paginationContainer);
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
    filterInvoices(page);
}

// Helper function to reset pagination and filter
function resetAndFilter() {
    currentPage = 0;
    filterInvoices(0);
}

// Event handler for pagination clicks
$(document).on('click', '.pagination .page-link', function (e) {
    e.preventDefault();
    const page = parseInt($(this).data('page'));
    if (!isNaN(page) && page >= 0) {
        filterInvoices(page);
    }
});

$(document).ready(function () {
    // Load initial data for invoice tab
    loadInvoices(0);

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
        });
    });

    // Search input with debounce
    let searchTimer;
    $('#filterUsername').on('input', function () {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => {
            resetAndFilter();
        }, 400);
    });
}); 