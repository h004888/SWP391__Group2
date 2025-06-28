let currentStatus = 'PENDING';
let currentPages = {
    PENDING: 0,
    PAID: 0,
    OVERDUE: 0
};

$(document).ready(function () {
    // Initialize max enrollment fields
    $('.max-enrollments').each(function() {
        const input = this;
        const form = $(input).closest('form');
        const saveBtn = form.find('.save-btn');

        if (input.value.trim() === '') {
            input.placeholder = 'MAX';
            saveBtn.hide();
        } else {
            input.placeholder = '';
            saveBtn.show();
        }
    });

    // Set current date to month year filter
    const today = new Date();
    const currentMonth = String(today.getMonth() + 1).padStart(2, '0');
    const currentYear = today.getFullYear();
    const currentMonthYear = `${currentYear}-${currentMonth}`;
    $('#monthYearFilter').val(currentMonthYear);

    // Load initial data for all tabs
    const statuses = ['pending', 'paid', 'overdue'];
    statuses.forEach(status => {
        loadMaintenances(status, 0);
    });

    // Tab change event
    $('button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
        const status = $(this).data('status');
        console.log("Tab changed to:", status);
        currentStatus = status;
        // Load current page for this status
        filterMaintenances(currentStatus, currentPages[currentStatus] || 0);
    });

    // Filter events
    $('#monthYearFilter').on('change', function () {
        console.log("Month year filter changed:", $(this).val());
        // Reset to first page when filtering
        currentPages[currentStatus] = 0;
        filterMaintenances(currentStatus, 0);
    });

    // Search input with debounce
    let searchTimer;
    $('#searchInput').on('input', function () {
        const searchValue = $(this).val();
        console.log("Search input triggered - value:", searchValue);
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function () {
            // Reset to first page when searching
            currentPages[currentStatus] = 0;
            filterMaintenances(currentStatus, 0);
        }, 500);
    });

    // Prevent form submission
    $('#filterForm').on('submit', function (e) {
        e.preventDefault();
        currentPages[currentStatus] = 0;
        filterMaintenances(currentStatus, 0);
    });
});

// Function to handle max enrollment input changes
function handleMaxEnrollmentChange(input) {
    const form = $(input).closest('form');
    const saveBtn = form.find('.save-btn');

    if (input.value.trim() === '') {
        input.placeholder = 'MAX';
        saveBtn.hide();
    } else {
        input.placeholder = '';
        saveBtn.show();
    }
}

// Function to handle fee deletion
function deleteFee(button) {
    if (confirm('Are you sure you want to delete this fee rule?')) {
        const feeId = $(button).data('fee-id');
        const form = $('<form>', {
            'method': 'POST',
            'action': '/admin/courseMaintenance/fees/delete'
        });

        form.append($('<input>', {
            'type': 'hidden',
            'name': 'feeId',
            'value': feeId
        }));

        $('body').append(form);
        form.submit();
    }
}

// Auto-hide alerts after 3 seconds
setTimeout(function () {
    $('#success-alert').fadeOut('slow');
    $('#error-alert').fadeOut('slow');
}, 5000);

function getTableBodyElement(status) {
    switch (status) {
        case 'pending':
            return $('#pendingTableBody');
        case 'paid':
            return $('#paidTableBody');
        case 'overdue':
            return $('#overdueTableBody');
        default:
            return $('#pendingTableBody');
    }
}

function getPaginationElement(status) {
    switch (status) {
        case 'pending':
            return $('#pendingPagination');
        case 'paid':
            return $('#paidPagination');
        case 'overdue':
            return $('#overduePagination');
        default:
            return $('#pendingPagination');
    }
}

function filterMaintenances(status, page = 0, size = 10) {
    let username = $('#searchInput').val() ? $('#searchInput').val().trim() : '';
    let monthYear = $('#monthYearFilter').val() || '';

    let tableBody = getTableBodyElement(status);
    let paginationContainer = getPaginationElement(status);

    if (!tableBody || tableBody.length === 0) {
        console.error("Table body not found for status:", status);
        return;
    }

    // Update current page for this status
    currentPages[status] = page;

    // Show loading state
    tableBody.html('<tr><td colspan="9" class="text-center">Loading...</td></tr>');

    // Load table data and pagination simultaneously
    Promise.all([
        // Load table rows
        $.ajax({
            url: '/admin/courseMaintenance/filter',
            method: 'GET',
            data: {
                username: username || null,
                status: status,
                monthYear: monthYear || null,
                page: page,
                size: size
            }
        }),
        // Load pagination
        $.ajax({
            url: '/admin/courseMaintenance/pagination',
            method: 'GET',
            data: {
                username: username || null,
                status: status,
                monthYear: monthYear || null,
                page: page,
                size: size
            }
        }),
        // Load total count for badge
        $.ajax({
            url: '/admin/courseMaintenance/count',
            method: 'GET',
            data: {
                username: username || null,
                status: status,
                monthYear: monthYear || null
            }
        })
    ]).then(function ([tableData, paginationData, countData]) {

        // Update table
        if (tableData && tableData.trim() !== '') {
            tableBody.html(tableData);
        } else {
            tableBody.html('<tr><td colspan="9" class="text-center">No maintenances found</td></tr>');
        }

        // Update pagination
        if (paginationData && paginationData.trim() !== '') {
            paginationContainer.html(paginationData);
        } else {
            paginationContainer.html('');
        }

        // Update count badge
        updateStatusCountBadge(status, countData);

    }).catch(function (error) {
        console.error("Error loading data for status:", status, error);
        tableBody.html('<tr><td colspan="9" class="text-center text-danger">Error loading data</td></tr>');
        paginationContainer.html('');
    });
}

function updateStatusCountBadge(status, totalCount) {
    const statusMap = {
        'pending': 'pendingCount',
        'paid': 'paidCount',
        'overdue': 'overdueCount'
    };

    const badgeId = statusMap[status];
    const badgeElement = document.getElementById(badgeId);

    if (!badgeElement) {
        console.error("Badge element not found for status:", status);
        return;
    }

    // Set the total count from server response
    const count = typeof totalCount === 'number' ? totalCount : (totalCount ? parseInt(totalCount) : 0);
    badgeElement.textContent = count;
}

function loadMaintenances(status, page = 0) {
    console.log("Loading maintenances for status:", status, "page:", page);
    filterMaintenances(status, page);
}

// Event handler for pagination clicks
$(document).on('click', '.pagination .page-link', function (e) {
    e.preventDefault();
    const page = parseInt($(this).data('page'));
    if (!isNaN(page) && page >= 0) {
        console.log("Pagination clicked - page:", page, "for status:", currentStatus);
        filterMaintenances(currentStatus, page);
    }
}); 