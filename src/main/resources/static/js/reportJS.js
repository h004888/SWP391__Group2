let currentReportStatus = 'pending';
let currentReportPages = {
    pending: 0,
    processed: 0
};

function getReportTableBodyElement(status) {
    return $('#' + status + 'TableBody');
}

function getReportPaginationElement(status) {
    return $('#' + status + 'Pagination');
}

function filterReports(status, page = 0, size = 10) {
    let keyword = $('#searchInput').val() || '';
    let type = $('#filterType').val() || '';

    let tableBody = getReportTableBodyElement(status);
    let paginationContainer = getReportPaginationElement(status);

    currentReportPages[status] = page;

    tableBody.html('<tr><td colspan="7" class="text-center">Loading...</td></tr>');

    Promise.all([
        $.ajax({
            url: '/admin/reports/filter',
            method: 'GET',
            data: {
                keyword: keyword,
                type: type,
                status: status,
                page: page,
                size: size
            }
        }),
        $.ajax({
            url: '/admin/reports/pagination',
            method: 'GET',
            data: {
                keyword: keyword,
                type: type,
                status: status,
                page: page,
                size: size
            }
        }),
        $.ajax({
            url: '/admin/reports/count',
            method: 'GET',
            data: {
                keyword: keyword,
                type: type,
                status: status
            }
        })
    ]).then(function ([tableData, paginationData, countData]) {
        tableBody.html(tableData);
        paginationContainer.html(paginationData);
        updateReportStatusCountBadge(status, countData);
    }).catch(function (error) {
        tableBody.html('<tr><td colspan="7" class="text-center text-danger">Error loading data</td></tr>');
        paginationContainer.html('');
    });
}

function updateReportStatusCountBadge(status, totalCount) {
    $('#' + status + 'Count').text(totalCount);
}

$(document).ready(function () {
    ['pending', 'processed'].forEach(status => {
        filterReports(status, 0);
    });

    $('button[data-bs-toggle="tab"]').on('shown.bs.tab', function () {
        let status = $(this).data('status');
        currentReportStatus = status;
        filterReports(status, currentReportPages[status] || 0);
    });

    $('#filterType').on('change', function () {
        currentReportPages[currentReportStatus] = 0;
        filterReports(currentReportStatus, 0);
    });
    $('#searchBtn').on('click', function () {
        currentReportPages[currentReportStatus] = 0;
        filterReports(currentReportStatus, 0);
    });
    $('#searchInput').on('keypress', function (e) {
        if (e.which === 13) {
            currentReportPages[currentReportStatus] = 0;
            filterReports(currentReportStatus, 0);
        }
    });
    $(document).on('click', '.pagination .page-link', function (e) {
        e.preventDefault();
        const page = parseInt($(this).data('page'));
        if (!isNaN(page) && page >= 0) {
            filterReports(currentReportStatus, page);
        }
    });
}); 