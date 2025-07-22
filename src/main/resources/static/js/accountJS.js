let currentRole = 1; // Default to admin (role ID 1)
let searchTimer;
let currentPage = 0;
let totalPages = 0;
let currentStatus = 'true'; // Default to active status

$(document).ready(function () {
    // Xác định vai trò ban đầu từ tab đang active
    const initialActiveTab = $('#accountTabs .nav-link.active');
    if (initialActiveTab.length) {
        currentRole = parseInt(initialActiveTab.data('role'));
    }

    // Tải dữ liệu cho tất cả các tab có thể nhìn thấy
    $('#accountTabs .nav-link').each(function() {
        const roleId = parseInt($(this).data('role'));
        const isActive = $(this).hasClass('active');
        loadUsers(roleId, '', 0, isActive); // Chỉ hiển thị pagination cho tab active
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

    $(document).on('click', '.pagination a', function (e) {
        e.preventDefault();
        const page = $(this).data('page');

        if (page !== undefined && page >= 0) {
            currentPage = page;
            const keyword = $('#searchInput').val().trim();
            loadUsers(currentRole, keyword, currentPage, true);
        } else {
            console.log("Invalid page number:", page);
        }
    });

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
            currentPage = 0; 
            loadUsers(currentRole, keyword, 0, true);
        }, 300);
    });

    // Status filter change event
    $('#statusFilter').on('change', function() {
        currentStatus = $(this).val();
        currentPage = 0;
        const keyword = $('#searchInput').val().trim();
        loadUsers(currentRole, keyword, 0, true);
    });

    // Khi click nút block
    $(document).on('click', '.btn-danger-soft[title="Blocked"]', function (e) {
        e.preventDefault();
        const blockUrl = $(this).attr('href');
        const userId = blockUrl.split('/').pop();
        const userEmail = $(this).closest('tr').find('td:nth-child(3)').text();
        currentBlockUserId = userId;
        currentBlockAction = 'block';
        $('#blockModalMessage').text(`Are you sure you want to block the account "${userEmail}"?`);
        $('#blockReasonWrapper').show();
        $('#blockReason').val('').prop('required', true);
        $('#confirmDelete').text("Block").removeClass("btn-warning").addClass("btn-danger");
        $('#deleteModal').modal('show');
    });

    // Khi click nút active
    $(document).on('click', '.btn-danger-soft[title="Activate"]', function (e) {
        e.preventDefault();
        const blockUrl = $(this).attr('href');
        const userId = blockUrl.split('/').pop();
        const userEmail = $(this).closest('tr').find('td:nth-child(3)').text();
        currentBlockUserId = userId;
        currentBlockAction = 'active';
        $('#blockModalMessage').text(`Are you sure you want to activate the account "${userEmail}"?`);
        $('#blockReasonWrapper').hide();
        $('#blockReason').val('').prop('required', false);
        $('#confirmDelete').text("Activate").removeClass("btn-danger").addClass("btn-warning");
        $('#deleteModal').modal('show');
    });

    // Confirm block/active button click
    $(document).on('click', '#confirmDelete', function (e) {
        e.preventDefault();
        if (currentBlockUserId) {
            let data = {};
            let method = 'POST';
            let url = '/admin/account/block/' + currentBlockUserId;
            if (currentBlockAction === 'block') {
                const reason = $('#blockReason').val();
                if (!reason.trim()) {
                    $('#blockReason').focus();
                    return;
                }
                data.reason = reason;
            }
            $.ajax({
                url: url,
                method: method,
                data: data,
                beforeSend: function () {
                    $('#confirmDelete').prop('disabled', true).html('<i class="fa fa-spinner fa-spin"></i> Processing...');
                },
                success: function (response) {
                    $('#deleteModal').modal('hide');
                    const keyword = $('#searchInput').val().trim();
                    loadUsers(currentRole, keyword, currentPage, true);
                    updateAllCounts();
                },
                error: function () {
                    alert("An error occurred!");
                },
                complete: function () {
                    $('#confirmDelete').prop('disabled', false).text(currentBlockAction === 'block' ? 'Block' : 'Activate');
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
        $('#blockReason').val('');
        $('#blockReasonWrapper').hide();
        currentBlockUserId = null;
        currentBlockAction = null;
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
            
            if (typeof data === 'object' && data.tableContent && data.pagination) {
                
                $(tableBodyElement).html(data.tableContent);
                if (showPagination) {
                    totalPages = data.pagination.totalPages;
                }
            } else {
                
                $(tableBodyElement).html(data);
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
            $('#confirmDelete').prop('disabled', false).html('Block');
            currentBlockUserId = null;
        }
    });
}

function getPaginationInfo(roleId, keyword = '', page = 0) {
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
        case 4:
            return document.getElementById('adminTableBody');
        default:
            return null;
    }
}

function updateCountBadge(roleId, data) {
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
            $('#adminCount').text((counts.adminActive ?? 0) + (counts.adminInactive ?? 0));
            $('#instructorCount').text((counts.instructorActive ?? 0) + (counts.instructorInactive ?? 0));
            $('#userCount').text((counts.userActive ?? 0) + (counts.userInactive ?? 0));
        },
        error: function (xhr, status, error) {
            console.error("Error getting counts:", error);
        }
    });
}