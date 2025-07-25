let currentRole = 1; // Default to admin (role ID 1)
let searchTimer;
let currentPage = 0;
let totalPages = 0;
let currentStatus = 'true'; // Default to active status

// Event listeners
$(document).ready(function () {
    // Load data for all tabs but only show pagination for admin tab initially
    [1, 2, 3].forEach(roleId => {
        loadUsers(roleId,'', 0, roleId === 1); // Only show pagination for admin tab (roleId === 1)
    });
    
    // Load initial counts
    updateAllCounts();
    
    function toggleAddButton(tabId) {
        if (tabId === "#admin") {
            $("#addAccountBtnContainer").show();
        } else {
            $("#addAccountBtnContainer").hide();
        }
    }

    const initialActiveTabId = $(".tab-pane.active").attr("id");
    toggleAddButton(`#${initialActiveTabId}`);

    $('button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
        const target = $(e.target).data("bsTarget");
        toggleAddButton(target);
    });

    // Xử lý phân trang với AJAX
    $(document).on('click', '.pagination a', function (e) {
        e.preventDefault();
        const page = $(this).data('page');

        // Kiểm tra page hợp lệ
        if (page !== undefined && page >= 0) {
            currentPage = page;
            const keyword = $('#searchInput').val().trim();
            loadUsers(currentRole, keyword, currentPage, true);
        } else {
            console.log("Invalid page number:", page);
        }
    });

    // Tab change
    $('#accountTabs button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
        const roleId = parseInt($(e.target).data('role'));
        currentRole = roleId;
        currentPage = 0;
        const keyword = $('#searchInput').val().trim();
        loadUsers(roleId, keyword, 0, true);
        console.log("Tab changed to role:", roleId);
    });

    // Search input event
    $('#searchInput').on('input', function () {
        const keyword = $(this).val().trim();
        console.log("Search keyword:", keyword);

        clearTimeout(searchTimer);
        searchTimer = setTimeout(function () {
            currentPage = 0; // Reset về trang đầu khi search
            loadUsers(currentRole, keyword, 0, true);
            // Removed updateAllCounts() - counts should not update when searching
        }, 300);
    });

    // Status filter change event
    $('#statusFilter').on('change', function() {
        currentStatus = $(this).val();
        currentPage = 0;
        const keyword = $('#searchInput').val().trim();
        loadUsers(currentRole, keyword, 0, true);
        // Removed updateAllCounts() - counts should not update when filtering
    });

    // Khi click nút block
    $(document).on('click', '.btn-danger-soft[title="Blocked"]', function (e) {
        e.preventDefault();

        const blockUrl = $(this).attr('href');
        const userId = blockUrl.split('/').pop();
        const userEmail = $(this).closest('tr').find('td:nth-child(3)').text();

        currentBlockUserId = userId;
        currentBlockAction = 'block';

        $('#deleteModal .modal-title').text("Block Account");
        $('#deleteModal .modal-body h6').text(`Bạn có chắc chắn muốn block tài khoản "${userEmail}" không?`);
        $('#confirmDelete').text("Block").removeClass("btn-warning").addClass("btn-danger");

        $('#deleteModal').modal('show');
    });

    // Khi click nút active
    $(document).on('click', '.btn-warning[title="Activate"]', function (e) {
        e.preventDefault();

        const blockUrl = $(this).attr('href');
        const userId = blockUrl.split('/').pop();
        const userEmail = $(this).closest('tr').find('td:nth-child(3)').text();

        currentBlockUserId = userId;
        currentBlockAction = 'active';

        $('#deleteModal .modal-title').text("Activate Account");
        $('#deleteModal .modal-body h6').text(`Bạn có chắc chắn muốn active tài khoản "${userEmail}" không?`);
        $('#confirmDelete').text("Active").removeClass("btn-danger").addClass("btn-warning");

        $('#deleteModal').modal('show');
    });

    // Confirm block button click
    $(document).on('click', '#confirmDelete', function (e) {
        e.preventDefault();

        if (currentBlockUserId) {
            $.ajax({
                url: '/admin/account/block/' + currentBlockUserId + '?action=' + currentBlockAction,
                method: 'GET',
                beforeSend: function () {
                    $('#confirmDelete').prop('disabled', true).html('<i class="fa fa-spinner fa-spin"></i> Đang xử lý...');
                },
                success: function (response) {
                    $('#deleteModal').modal('hide');
                    const keyword = $('#searchInput').val().trim();
                    loadUsers(currentRole, keyword, currentPage, true);
                    updateAllCounts(); // Update counts after blocking/activating
                },
                error: function () {
                    alert("Có lỗi xảy ra!");
                },
                complete: function () {
                    $('#confirmDelete').prop('disabled', false).text(currentBlockAction === 'block' ? 'Block' : 'Active');
                    currentBlockUserId = null;
                    currentBlockAction = null;
                }
            });
        }
    });

    // Reset password confirmation
    $(document).on('click', '.btn-success[title="Reset Password"]', function (e) {
        if (!confirm('Bạn có chắc chắn muốn reset password tài khoản này không?')) {
            e.preventDefault();
        }
    });

    // Reset modal khi đóng
    $('#deleteModal').on('hidden.bs.modal', function () {
        $('#confirmDelete').prop('disabled', false).html('Block');
        currentBlockUserId = null;
    });
});

// Function to load users based on role and search keyword
function loadUsers(roleId, keyword = '', page = 0, showPagination = false) {
    const tableBodyElement = getTableBodyElement(roleId);

    if (!tableBodyElement) {
        console.error("Table body element NOT FOUND for role:", roleId);
        return;
    }

    // Convert status string to boolean or null
    let statusParam = null;
    if (currentStatus !== 'all') {
        statusParam = currentStatus === 'true';
    }

    $.ajax({
        url: '/admin/account/filter',
        method: 'GET',
        data: {
            keyword: keyword || null,
            role: roleId,
            page: page,
            size: 5,
            status: statusParam
        },
        beforeSend: function () {
            console.log("=== AJAX SENDING ===");
            console.log("Data:", {keyword: keyword || null, role: roleId, page: page, status: statusParam});
        },
        success: function (data) {
            //Parse response để lấy cả table content và pagination info
            if (typeof data === 'object' && data.tableContent && data.pagination) {
                // Nếu server trả về object với tableContent và pagination
                $(tableBodyElement).html(data.tableContent);
                // Removed updateAllCounts() - counts should not update during normal loading
                if (showPagination) {
                    totalPages = data.pagination.totalPages;
                }
            } else {
                // Nếu server chỉ trả về HTML content (current implementation)
                $(tableBodyElement).html(data);
                // Removed updateAllCounts() - counts should not update during normal loading

                //call API riêng để lấy pagination info hoặc modify server response
                if (showPagination) {
                    getPaginationInfo(roleId, keyword, page);
                }
            }
        },
        error: function (xhr, status, error) {
            console.error("Response Text:", xhr.responseText);
            console.error("Status Code:", xhr.status);
        }
    });
}

function blockUser(userId, userEmail) {
    $.ajax({
        url: '/admin/account/block/' + userId,
        method: 'GET',
        data: {},
        beforeSend: function () {
            $('#confirmDelete').prop('disabled', true).html('<i class="fa fa-spinner fa-spin"></i> Đang xử lý...');
        },
        success: function (response) {
            $('#deleteModal').modal('hide');
            const keyword = $('#searchInput').val().trim();
            loadUsers(currentRole, keyword, currentPage, true);
            updateAllCounts(); // Update counts after blocking/activating
        },
        error: function (xhr, status, error) {
            $('#deleteModal').modal('hide');
            let errorMessage = 'Có lỗi xảy ra khi block tài khoản!';

            // Xử lý error response từ server
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            } else if (xhr.responseText) {
                try {
                    const errorData = JSON.parse(xhr.responseText);
                    errorMessage = errorData.message || errorMessage;
                } catch (e) {
                }
            }
            console.error('Error blocking user:', error);
        },
        complete: function () {
            // Reset lại confirm button
            $('#confirmDelete').prop('disabled', false).html('Block');
            currentBlockUserId = null;
        }
    });
}

function getPaginationInfo(roleId, keyword = '', page = 0) {
    // Convert status string to boolean or null
    let statusParam = null;
    if (currentStatus !== 'all') {
        statusParam = currentStatus === 'true';
    }

    $.ajax({
        url: '/admin/account/pagination',
        method: 'GET',
        data: {
            keyword: keyword || null,
            role: roleId,
            page: page,
            size: 5,
            status: statusParam
        },
        success: function (paginationHtml) {
            $('#accountPaginationContainer').html(paginationHtml);
        },
        error: function (xhr, status, error) {
            console.error("Error getting pagination info:", error);
            $('#accountPaginationContainer').html('<div class="text-center text-danger">Error loading pagination</div>');
        }
    });
}

$(document).on('click', '.btn-reset-password', function (e) {
    e.preventDefault();

    let userId = $(this).data("userid");

    $.ajax({
        url: `/admin/account/resetPass/${userId}`,
        method: 'GET',
        success: function (response) {
            alert("Repassword sucessfully!")
            const keyword = $('#searchInput').val().trim();
            loadUsers(currentRole, keyword, currentPage, true);
            updateAllCounts(); // Update counts after resetting password
        },
        error: function (xhr) {
            alert("Unexpected error: " + xhr.responseText);
        }
    });
});

function getTableBodyElement(roleId) {
    switch (roleId) {
        case 1:
            return document.getElementById('adminTableBody');
        case 2:
            return document.getElementById('instructorTableBody');
        case 3:
            return document.getElementById('userTableBody');
        default:
            return null;
    }
}

function updateCountBadge(roleId, data) {
    // This function is now deprecated since we use updateAllCounts()
    // But we'll keep it for backward compatibility and just call updateAllCounts
    updateAllCounts();
}

function updateAllCounts() {
    const keyword = $('#searchInput').val().trim();

    $.ajax({
        url: '/admin/account/counts',
        method: 'GET',
        data: {
            keyword: keyword || null
        },
        success: function (counts) {
            // Cộng cả active và inactive cho mỗi role
            $('#adminCount').text((counts.adminActive ?? 0) + (counts.adminInactive ?? 0));
            $('#instructorCount').text((counts.instructorActive ?? 0) + (counts.instructorInactive ?? 0));
            $('#userCount').text((counts.userActive ?? 0) + (counts.userInactive ?? 0));
        },
        error: function (xhr, status, error) {
            console.error("Error getting counts:", error);
        }
    });
}