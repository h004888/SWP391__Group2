// voucherFilter.js - Tối ưu UI/UX cho filter voucher instructor dashboard, search đồng thời cả hai tab

$(document).ready(function () {
    function showLoading(status) {
        $(`#voucher-table-body-${status}`).html('<tr><td colspan="8" class="text-center py-4"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div></td></tr>');
    }
    function filterVouchersAll(pageValid, pageExpired) {
        const keyword = $('#voucher-search').val();
        const size = 10;
        // Filter valid
        showLoading('valid');
        $.ajax({
            url: '/instructor/voucher/filter',
            type: 'GET',
            data: { keyword, status: 'valid', page: pageValid || 0, size },
            success: function (data) {
                $('#voucher-table-body-valid').html(data);
                updateVoucherPagination('valid', keyword, pageValid || 0, size);
                if ($('#voucher-table-body-valid tr').length === 0 || $('#voucher-table-body-valid').text().trim() === '') {
                    $('#voucher-table-body-valid').html('<tr><td colspan="8" class="text-center py-4 text-muted"><i class="fa fa-ticket-alt fa-2x mb-2"></i><div>Không có voucher nào.</div></td></tr>');
                }
            },
            error: function () {
                $('#voucher-table-body-valid').html('<tr><td colspan="8" class="text-center text-danger">Lỗi khi tải dữ liệu voucher.</td></tr>');
            }
        });
        // Filter expired
        showLoading('expired');
        $.ajax({
            url: '/instructor/voucher/filter',
            type: 'GET',
            data: { keyword, status: 'expired', page: pageExpired || 0, size },
            success: function (data) {
                $('#voucher-table-body-expired').html(data);
                updateVoucherPagination('expired', keyword, pageExpired || 0, size);
                if ($('#voucher-table-body-expired tr').length === 0 || $('#voucher-table-body-expired').text().trim() === '') {
                    $('#voucher-table-body-expired').html('<tr><td colspan="8" class="text-center py-4 text-muted"><i class="fa fa-ticket-alt fa-2x mb-2"></i><div>Không có voucher nào.</div></td></tr>');
                }
            },
            error: function () {
                $('#voucher-table-body-expired').html('<tr><td colspan="8" class="text-center text-danger">Lỗi khi tải dữ liệu voucher.</td></tr>');
            }
        });
    }
    function updateVoucherPagination(status, keyword, page, size) {
        $.ajax({
            url: '/instructor/voucher/pagination',
            type: 'GET',
            data: { keyword, tabType: status, page, size },
            success: function (data) {
                $(`.voucher-pagination-${status}`).html(data);
            },
            error: function () {
                $(`.voucher-pagination-${status}`).html('<div class="text-center text-danger">Lỗi phân trang</div>');
            }
        });
    }
    // Search filter cho cả hai tab
    $('#voucher-search').on('input', function () {
        filterVouchersAll(0, 0);
    });
    // Phân trang cho từng tab
    $(document).on('click', '.voucher-pagination-valid .page-link', function (e) {
        e.preventDefault();
        const page = $(this).data('page');
        filterVouchersAll(page, null);
    });
    $(document).on('click', '.voucher-pagination-expired .page-link', function (e) {
        e.preventDefault();
        const page = $(this).data('page');
        filterVouchersAll(null, page);
    });
    // Khi chuyển tab, không filter lại (giữ kết quả search hiện tại)
    // Load cả hai bảng khi vào trang
    filterVouchersAll(0, 0);
}); 