$(document).ready(function() {
    // Initialize tooltips
    $('[title]').each(function() {
        if (!bootstrap.Tooltip.getInstance(this)) {
            new bootstrap.Tooltip(this);
        }
    });

    // Initialize toasts
    $('.toast').each(function() {
        new bootstrap.Toast(this, { delay: 5000 }).show();
    });

    // Initialize dropdowns
    initializeDropdowns();

    // Tab change functionality
    initCourseTab();

    // Update status badges
    updateStatusBadges();
});

// Hàm khởi tạo các dropdown
function initializeDropdowns() {
    // Khởi tạo tất cả dropdown sử dụng API của Bootstrap
    var dropdownElementList = [].slice.call(document.querySelectorAll('.dropdown-toggle'));
    dropdownElementList.map(function(dropdownToggleEl) {
        return new bootstrap.Dropdown(dropdownToggleEl);
    });
}

// Sau khi update content qua AJAX, re-init dropdown, tooltip, và xóa toast cũ trước khi show mới
function afterAjaxUpdate() {
    // Reinitialize tooltips
    $('[title]').each(function() {
        var instance = bootstrap.Tooltip.getInstance(this);
        if (instance) {
            instance.dispose();
        }
        new bootstrap.Tooltip(this);
    });
    // Reinitialize dropdowns
    initializeDropdowns();
    // Xóa toast cũ
    $('.toast').remove();
}

// Filter function
function filterCourses(status, page = 0) {
    // Show loading indicator
    showLoading(status);

    // Get filter values
    let category = $('#filterCategory').val();
    if (!category) category = null;

    let price = $('#filterPrice').val();
    let keyword = $('#searchInput').val();

    // Prepare data
    let requestData = {
        status: status,
        page: page,
        size: 10
    };

    if (category) requestData.category = category;
    if (price) requestData.price = price;
    if (keyword) requestData.title = keyword;

    // AJAX request for table body
    $.ajax({
        url: '/instructor/courses/filter',
        type: 'GET',
        data: requestData,
        success: function(response) {
            // Update table body
            $('#' + status + 'TableBody').html(response);
            hideLoading(status);
            afterAjaxUpdate();
            // Update badge cho tất cả các tab với filter hiện tại
            updateAllTabBadges({category, price, keyword});
        },
        error: function(xhr, status, error) {
            console.error("Error filtering courses:", error);
            hideLoading(status);
            showToast('An error occurred while filtering courses. Please try again.', 'error');
        }
    });

    // AJAX request for pagination
    $.ajax({
        url: '/instructor/courses/pagination',
        type: 'GET',
        data: requestData,
        success: function(response) {
            // Update pagination
            $('#' + status + 'Pagination').html(response);
        },
        error: function(xhr, status, error) {
            console.error("Error loading pagination:", error);
        }
    });
}

// Show loading indicator
function showLoading(status) {
    $('#' + status + 'TableBody').html('<tr><td colspan="6" class="text-center py-4"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div></td></tr>');
}

// Hide loading indicator
function hideLoading(status) {
    // Loading will be replaced by actual content
}

// Show toast function
function showToast(message, type = 'info') {
    let bgClass = type === 'error' ? 'bg-danger' : 'bg-success';
    let toastHtml = `
        <div class="toast align-items-center text-white ${bgClass} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    $('.position-fixed.bottom-0.end-0').append(toastHtml);
    let toast = new bootstrap.Toast($('.toast').last()[0], { delay: 5000 });
    toast.show();
}

// Các hàm xử lý hành động khóa học
function upToPublic(courseId) {
    if (!courseId) return;
    if (confirm('Are you sure you want to publish this course?')) {
        $.post('/instructor/courses/uptopublic', { courseId: courseId })
            .done(function() {
                filterAllTabs(0); // Reload tất cả các tab
                updateStatusBadges();
            });
    }
}

function unpublishCourse(courseId) {
    showConfirmActionModal(
        'Bạn có chắc chắn muốn hủy công khai khóa học này?',
        '/instructor/courses/unpublish',
        courseId,
        function() {
            filterAllTabs(0);
            updateStatusBadges();
        }
    );
}

function hideCourse(courseId) {
    showConfirmActionModal(
        'Bạn có chắc chắn muốn ẩn khóa học này?',
        '/instructor/courses/hide',
        courseId,
        function() {
            filterAllTabs(0);
            updateStatusBadges();
        }
    );
}

function unhideCourse(courseId) {
    showConfirmActionModal(
        'Bạn có chắc chắn muốn hiển thị lại khóa học này?',
        '/instructor/courses/unhide',
        courseId,
        function() {
            filterAllTabs(0);
            updateStatusBadges();
        }
    );
}

function blockCourse(courseId) {
    showConfirmActionModal(
        'Bạn có chắc chắn muốn chặn khóa học này?',
        '/instructor/courses/block',
        courseId,
        function() {
            filterAllTabs(0);
            updateStatusBadges();
        }
    );
}

function unblockCourse(courseId) {
    showConfirmActionModal(
        'Bạn có chắc chắn muốn bỏ chặn khóa học này?',
        '/instructor/courses/unblock',
        courseId,
        function() {
            filterAllTabs(0);
            updateStatusBadges();
        }
    );
}

function showConfirmActionModal(message, actionUrl, courseId, callback) {
    $('#confirmActionMessage').text(message);
    $('#confirmActionForm').attr('action', actionUrl);
    $('#confirmActionCourseId').val(courseId);
    $('#confirmActionForm').off('submit').on('submit', function(e) {
        e.preventDefault();
        $.post(actionUrl, { courseId: courseId })
            .done(function() {
                if (typeof callback === 'function') callback();
                $('#confirmActionModal').modal('hide');
            });
    });
    var modal = new bootstrap.Modal(document.getElementById('confirmActionModal'));
    modal.show();
}

function deleteCourse(courseId, courseTitle) {
    $('#courseTitle').text(courseTitle);
    $('#deleteCourseId').val(courseId);
    $('#deleteForm').off('submit').on('submit', function(e) {
        e.preventDefault();
        $.post('/instructor/createcourse/deletecourse', { courseId: courseId })
            .done(function() {
                filterAllTabs(0);
                updateStatusBadges();
                $('#deleteModal').modal('hide');
            });
    });
    $('#deleteModal').modal('show');
}

// Handle delete form submission
$('#deleteForm').on('submit', function(e) {
    e.preventDefault();

    // Lưu lại các giá trị filter hiện tại để tải lại sau khi xóa
    let category = $('#filterCategory').val();
    let status = $('.nav-link.active').data('status');
    let price = $('#filterPrice').val();

    $.ajax({
        url: $(this).attr('action'),
        type: 'POST',
        data: $(this).serialize(),
        success: function(response) {
            // Close modal
            bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();

            // Tải lại danh sách khóa học với filter hiện tại
            filterCourses(status, 0);

            // Hiển thị thông báo thành công
            showToast('Course deleted successfully!', 'success');
        },
        error: function(xhr, status, error) {
            console.error("Error deleting course:", error);
            showToast('An error occurred while deleting the course.', 'error');
        }
    });
});

// Xử lý submit form xác nhận action (Up to Public, Unpublish, Hide, etc.) bằng AJAX
$('#confirmActionForm').on('submit', function(e) {
    e.preventDefault();
    var $form = $(this);
    var actionUrl = $form.attr('action');
    var formData = $form.serialize();

    // Lưu lại các giá trị filter hiện tại để tải lại sau khi xử lý
    let status = $('.nav-link.active').data('status');
    let page = 0;

    $.ajax({
        url: actionUrl,
        type: 'POST',
        data: formData,
        success: function(response) {
            // Đóng modal
            bootstrap.Modal.getInstance(document.getElementById('confirmActionModal')).hide();
            // Tải lại danh sách khoá học với filter hiện tại
            filterCourses(status, page);
            // Hiển thị toast thành công
            showToast('Action completed successfully!', 'success');
        },
        error: function(xhr, status, error) {
            // Đóng modal
            bootstrap.Modal.getInstance(document.getElementById('confirmActionModal')).hide();
            // Hiển thị toast lỗi
            showToast('An error occurred. Please try again.', 'error');
        }
    });
});


window.upToPublic = upToPublic;
window.unpublishCourse = unpublishCourse;
window.hideCourse = hideCourse;
window.unhideCourse = unhideCourse;
window.blockCourse = blockCourse;
window.unblockCourse = unblockCourse;
window.deleteCourse = deleteCourse;

function updateStatusBadges() {
    const statuses = [
        { tab: 'pending', badge: 'pendingCount' },
        { tab: 'approved', badge: 'approvedCount' },
        { tab: 'publish', badge: 'publishCount'},
        { tab: 'rejected', badge: 'rejectedCount' },
        { tab: 'hidden', badge: 'hiddenCount' },
        { tab: 'blocked', badge: 'blockedCount' },
        { tab: 'draft', badge: 'draftCount' }
    ];
    statuses.forEach(function(s) {
        let status = s.db || s.tab;
        $.ajax({
            url: '/instructor/courses/count-by-status',
            type: 'GET',
            data: { status: status },
            success: function(count) {
                $('#' + s.badge).text(count);
            },
            error: function() {
                $('#' + s.badge).text('0');
            }
        });
    });
}

// Update badge cho tất cả các tab với filter hiện tại
function updateAllTabBadges(filter) {
    const statuses = [
        { tab: 'pending', badge: 'pendingCount' },
        { tab: 'approved', badge: 'approvedCount' },
        { tab: 'publish', badge: 'publishCount'},
        { tab: 'rejected', badge: 'rejectedCount' },
        { tab: 'hidden', badge: 'hiddenCount' },
        { tab: 'blocked', badge: 'blockedCount' },
        { tab: 'draft', badge: 'draftCount' }
    ];
    statuses.forEach(function(s) {
        let status = s.db || s.tab;
        let data = { status: status };
        if (filter.category) data.category = filter.category;
        if (filter.price) data.price = filter.price;
        if (filter.keyword) data.title = filter.keyword;
        $.ajax({
            url: '/instructor/courses/count-by-status',
            type: 'GET',
            data: data,
            success: function(count) {
                $('#' + s.badge).text(count);
            },
            error: function() {
                $('#' + s.badge).text('0');
            }
        });
    });
}

function filterAllTabs(page = 0) {
    const statuses = ['pending', 'approved', 'publish', 'rejected', 'hidden', 'blocked', 'draft'];
    statuses.forEach(status => {
        filterCourses(status, page);
    });
}

function initCourseTab() {
    // Bind lại tất cả event handler cho tab, filter, search, pagination...
    $('.nav-link[data-bs-toggle="tab"]').off('shown.bs.tab').on('shown.bs.tab', function(e) {
        let status = $(this).data('status');
        filterCourses(status, 0);
    });

    $('#filterCategory, #filterPrice').off('change').on('change', function() {
        filterAllTabs(0);
    });

    $('#searchInput').off('input').on('input', function() {
        filterAllTabs(0);
    });

    $(document).off('click', '.pagination .page-link').on('click', '.pagination .page-link', function(e) {
        e.preventDefault();
        if ($(this).parent().hasClass('disabled')) return;
        let page = $(this).data('page');
        let status = $(this).data('status');
        if (page !== undefined && status !== undefined) {
            filterCourses(status, page);
        }
    });

    // Khi load trang, gọi filterAllTabs để đảm bảo tất cả tab đều update đúng
    filterAllTabs(0);
}
