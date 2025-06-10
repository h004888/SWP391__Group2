let currentRole = 1; // Default to admin (role ID 1)
let searchTimer;
let currentPage = 0;
let totalPages = 0;

// Event listeners
$(document).ready(function () {
    [1, 2, 3].forEach(roleId => {
        loadUsers(roleId,'', 0);
    });
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
        if (page !== undefined && page >= 0 && page < totalPages) {
            currentPage = page;
            const keyword = $('#searchInput').val().trim();
            loadUsers(currentRole, keyword, currentPage);
        } else {
            console.log("Invalid page number:", page);
        }
    });


// Load initial data for admin tab

    // loadUsers(1, ); // Load trang đầu tiên cho admin

    // Tab change
    $('#accountTabs button[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
        const roleId = parseInt($(e.target).data('role'));
        currentRole = roleId;
        currentPage = 0;
        const keyword = $('#searchInput').val().trim();
        loadUsers(roleId, keyword, 0);
        console.log("Tab changed to role:", roleId);
    });

    // Search input event
    $('#searchInput').on('input', function () {
        const keyword = $(this).val().trim();
        console.log("Search keyword:", keyword);

        clearTimeout(searchTimer);
        searchTimer = setTimeout(function () {
            currentPage = 0; // Reset về trang đầu khi search
            loadUsers(currentRole, keyword, 0);
        }, 300);
    });

    // Khi click nút block
    $(document).on('click', '.btn-danger[title="Blocked"]', function (e) {
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
                    loadUsers(currentRole, keyword, currentPage);
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
function loadUsers(roleId, keyword = '', page = 0) {
    const tableBodyElement = getTableBodyElement(roleId);

    if (!tableBodyElement) {
        console.error("Table body element NOT FOUND for role:", roleId);
        return;
    }
    $.ajax({
        url: '/admin/account/filter',
        method: 'GET',
        data: {
            keyword: keyword || null,
            role: roleId,
            page: page,
            size: 5
        },
        beforeSend: function () {
            console.log("=== AJAX SENDING ===");
            console.log("Data:", {keyword: keyword || null, role: roleId, page: page});
        },
        success: function (data) {
            //Parse response để lấy cả table content và pagination info
            if (typeof data === 'object' && data.tableContent && data.pagination) {
                // Nếu server trả về object với tableContent và pagination
                $(tableBodyElement).html(data.tableContent);
                updateCountBadge(roleId, data.tableContent);
                totalPages = data.pagination.totalPages;
            } else {
                // Nếu server chỉ trả về HTML content (current implementation)
                $(tableBodyElement).html(data);
                updateCountBadge(roleId, data);

                //call API riêng để lấy pagination info hoặc modify server response
                getPaginationInfo(roleId, keyword, page);
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
            loadUsers(currentRole, keyword, currentPage);
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
    $.ajax({
        url: '/admin/account/pagination-info',
        method: 'GET',
        data: {
            keyword: keyword || null,
            role: roleId,
            page: page,
            size: 5
        },
        success: function (paginationData) {
            totalPages = paginationData.totalPages;
            updatePaginationUI(paginationData, roleId, keyword, page);
        },
        error: function (xhr, status, error) {
            console.error("Error getting pagination info:", error);
        }
    });
}

// update pagination UI
function updatePaginationUI(pagination, roleId, keyword, currentPage) {
    const paginationContainer = $('.pagination');

    paginationContainer.parent().show();
    paginationContainer.empty();

    // Previous button
    const prevDisabled = currentPage === 0 ? 'disabled' : '';
    paginationContainer.append(`
        <li class="page-item ${prevDisabled}">
            <a class="page-link" data-page="${currentPage - 1}" href="#">
                <i class="fa fa-angle-double-left"></i>
            </a>
        </li>
    `);

    // Page numbers
    for (let i = 0; i < pagination.totalPages; i++) {
        const active = currentPage === i ? 'active' : '';
        paginationContainer.append(`
            <li class="page-item ${active}">
                <a class="page-link" data-page="${i}" href="#">${i + 1}</a>
            </li>
        `);
    }

    // Next button
    const nextDisabled = currentPage + 1 >= pagination.totalPages ? 'disabled' : '';
    paginationContainer.append(`
        <li class="page-item ${nextDisabled}">
            <a class="page-link" data-page="${currentPage + 1}" href="#">
                <i class="fa fa-angle-double-right"></i>
            </a>
        </li>
    `);

    // Update hint text
    $('.hint-text').html(`
        Showing <b>${pagination.currentElements}</b> out of <b>${pagination.totalElements}</b> entries
    `);
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
            loadUsers(currentRole, keyword, currentPage);
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
        case 3:
            return document.getElementById('instructorTableBody');
        case 2:
            return document.getElementById('userTableBody');
        default:
            return null;
    }
}

function updateCountBadge(roleId, data) {
    const tableBodyElement = getTableBodyElement(roleId);

    if (!tableBodyElement) return;

    const tempDiv = document.createElement('tbody');
    tempDiv.innerHTML = data;

    const rows = tempDiv.querySelectorAll('tr');

    let badgeElement;
    switch (roleId) {
        case 1:
            badgeElement = document.getElementById('adminCount');
            break;
        case 3:
            badgeElement = document.getElementById('instructorCount');
            break;
        case 2:
            badgeElement = document.getElementById('userCount');
            break;
    }

    // Trừ đi hàng "Không tìm thấy tài khoản nào" nếu có
    const rowCount = Array.from(rows).filter(row => !row.textContent.includes("Không tìm thấy")).length;

    if (badgeElement) {
        badgeElement.textContent = rowCount;
    }
}


