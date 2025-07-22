let currentStatus = 'pending';
let currentPages = {
    pending: 0,
    approved: 0,
    rejected: 0,
    resubmit: 0,
    draft: 0
};

function getTableBodyElement(status) {
    switch (status) {
        case 'draft':
            return $('#draftTableBody');
        case 'pending':
            return $('#pendingTableBody');
        case 'approved':
            return $('#approvedTableBody');
        case 'rejected':
            return $('#rejectedTableBody');
        case 'publish':
            return $('#publishTableBody');
        default:
            return $('#pendingTableBody');
    }
}

function getPaginationElement(status) {
    switch (status) {
        case 'draft':
            return $('#draftPagination');
        case 'pending':
            return $('#pendingPagination');
        case 'approved':
            return $('#approvedPagination');
        case 'rejected':
            return $('#rejectedPagination');
        case 'publish':
            return $('#publishPagination');
        default:
            return $('#pendingPagination');
    }
}

function filterCourses(status, page = 0, size = 5) {
    let keyword = $('#searchInput').val() ? $('#searchInput').val().trim() : '';
    let category = $('#filterCategory').val() || '';
    let price = $('#filterPrice').val() || '';

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
            url: '/admin/course/filter',
            method: 'GET',
            data: {
                keyword: keyword || null,
                category: category || null,
                price: price || null,
                status: status,
                page: page,
                size: size
            }
        }),
        // Load pagination
        $.ajax({
            url: '/admin/course/pagination',
            method: 'GET',
            data: {
                keyword: keyword || null,
                category: category || null,
                price: price || null,
                status: status,
                page: page,
                size: size
            }
        }),
        // Load total count for badge
        $.ajax({
            url: '/admin/course/count',
            method: 'GET',
            data: {
                keyword: keyword || null,
                category: category || null,
                price: price || null,
                status: status
            }
        })
    ]).then(function ([tableData, paginationData, countData]) {

        // Update table
        if (tableData && tableData.trim() !== '') {
            tableBody.html(tableData);
        } else {
            tableBody.html('<tr><td colspan="9" class="text-center">No courses found</td></tr>');
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
        'approved': 'approveCount',
        'rejected': 'rejectCount',
        'resubmit': 'resubmitCount',
        'draft': 'draftCount',
        'publish': 'publishCount'
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

function loadCourses(status, page = 0) {
    console.log("Loading courses for status:", status, "page:", page);
    filterCourses(status, page);
}

// Event handler for pagination clicks
$(document).on('click', '.pagination .page-link', function (e) {
    e.preventDefault();
    const page = parseInt($(this).data('page'));
    if (!isNaN(page) && page >= 0) {
        console.log("Pagination clicked - page:", page, "for status:", currentStatus);
        filterCourses(currentStatus, page);
    }
});

$(document).ready(function () {
    // Load initial data for all tabs
    const statuses = ['pending', 'draft', 'approved', 'rejected', 'resubmit', 'publish'];
    statuses.forEach(status => {
        loadCourses(status, 0);
    });

    // Tab change event
    $('button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
        const status = $(this).data('status');
        console.log("Tab changed to:", status);
        currentStatus = status;
        // Load current page for this status
        filterCourses(currentStatus, currentPages[currentStatus] || 0);
    });

    // Filter events
    $('#filterCategory').on('change', function () {
        console.log("Category filter changed:", $(this).val());
        // Reset to first page when filtering
        currentPages[currentStatus] = 0;
        filterCourses(currentStatus, 0);
    });

    $('#filterPrice').on('change', function () {
        console.log("Price filter changed:", $(this).val());
        // Reset to first page when filtering
        currentPages[currentStatus] = 0;
        filterCourses(currentStatus, 0);
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
            filterCourses(currentStatus, 0);
        }, 500);
    });

    // Prevent form submission
    $('#filterForm').on('submit', function (e) {
        e.preventDefault();
        currentPages[currentStatus] = 0;
        filterCourses(currentStatus, 0);
    });
});