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

    // Filter functionality
    $('#filterCategory, #courseStatus, #filterPrice').on('change', function() {
        filterCourses();
    });

    // Pagination functionality
    $(document).on('click', '.pagination .page-link', function(e) {
        e.preventDefault();

        if ($(this).parent().hasClass('disabled')) {
            return;
        }

        let page = $(this).data('page');
        if (page !== undefined) {
            filterCourses(page);
        }
    });
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
function filterCourses(page = 0) {
    // Show loading indicator
    $('#loadingIndicator').show();
    $('#courseTableContainer').hide();

    // Get filter values
    let category = $('#filterCategory').val();
    let status = $('#courseStatus').val();
    let price = $('#filterPrice').val();

    // Prepare data
    let requestData = {
        page: page
    };

    if (category) requestData.category = category;
    if (status) requestData.status = status;
    if (price) requestData.price = price;

    // AJAX request
    $.ajax({
        url: '/instructordashboard/courses/filter',
        type: 'GET',
        data: requestData,
        success: function(response) {
            // Update content
            $('#courseContentContainer').html(response);

            // Update URL without page reload
            updateUrl(requestData);

            // Scroll to top of content
            $('html, body').animate({
                scrollTop: $("#courseContentContainer").offset().top - 100
            }, 300);

            afterAjaxUpdate();

            // Re-bind action handlers for dynamically loaded content
            window.upToPublic = upToPublic;
            window.unpublishCourse = unpublishCourse;
            window.hideCourse = hideCourse;
            window.deleteCourse = deleteCourse;
        },
        error: function(xhr, status, error) {
            console.error("Error filtering courses:", error);

            // Hide loading and show error
            $('#loadingIndicator').hide();
            $('#courseTableContainer').show();

            // Show error message
            showToast('An error occurred while filtering courses. Please try again.', 'error');
        }
    });
}

// Update URL function
function updateUrl(params) {
    let url = new URL(window.location.href);

    // Clear existing params
    url.searchParams.delete('category');
    url.searchParams.delete('status');
    url.searchParams.delete('price');
    url.searchParams.delete('page');

    // Add new params
    Object.keys(params).forEach(key => {
        if (params[key]) {
            url.searchParams.set(key, params[key]);
        }
    });

    window.history.pushState({}, '', url);
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
    showConfirmActionModal(
        'Bạn có chắc chắn muốn công khai khóa học này?',
        '/instructordashboard/courses/uptopublic',
        courseId
    );
}

function unpublishCourse(courseId) {
    showConfirmActionModal(
        'Bạn có chắc chắn muốn hủy công khai khóa học này?',
        '/instructordashboard/courses/unpublish',
        courseId
    );
}

function hideCourse(courseId) {
    showConfirmActionModal(
        'Bạn có chắc chắn muốn ẩn khóa học này?',
        '/instructordashboard/courses/hide',
        courseId
    );
}

function showConfirmActionModal(message, actionUrl, courseId) {
    $('#confirmActionMessage').text(message);
    $('#confirmActionForm').attr('action', actionUrl);
    $('#confirmActionCourseId').val(courseId);
    
    var modal = new bootstrap.Modal(document.getElementById('confirmActionModal'));
    modal.show();
}

function deleteCourse(courseId, courseTitle) {
    $('#courseTitle').text(courseTitle);
    $('#deleteCourseId').val(courseId);
    
    var modal = new bootstrap.Modal(document.getElementById('deleteModal'));
    modal.show();
}

// Handle delete form submission
$('#deleteForm').on('submit', function(e) {
    e.preventDefault();

    // Lưu lại các giá trị filter hiện tại để tải lại sau khi xóa
    let category = $('#filterCategory').val();
    let status = $('#courseStatus').val();
    let price = $('#filterPrice').val();

    $.ajax({
        url: $(this).attr('action'),
        type: 'POST',
        data: $(this).serialize(),
        success: function(response) {
            // Close modal
            bootstrap.Modal.getInstance(document.getElementById('deleteModal')).hide();

            // Tải lại danh sách khóa học với filter hiện tại
            $.ajax({
                url: '/instructordashboard/courses/filter',
                type: 'GET',
                data: {
                    category: category,
                    status: status,
                    price: price,
                    page: 0
                },
                success: function(response) {
                    // Cập nhật nội dung
                    $('#courseContentContainer').html(response);

                    // Hiển thị thông báo thành công
                    let toastHtml = `
                        <div class="toast align-items-center text-white bg-success border-0" role="alert">
                            <div class="d-flex">
                                <div class="toast-body">Course deleted successfully!</div>
                                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                            </div>
                        </div>
                    `;

                    $('.position-fixed.bottom-0.end-0').append(toastHtml);
                    let toast = new bootstrap.Toast($('.toast').last()[0], { delay: 5000 });
                    toast.show();
                },
                error: function(xhr, status, error) {
                    console.error("Error refreshing courses:", error);
                }
            });
        },
        error: function(xhr, status, error) {
            console.error("Error deleting course:", error);

            // Hiển thị thông báo lỗi
            let toastHtml = `
                <div class="toast align-items-center text-white bg-danger border-0" role="alert">
                    <div class="d-flex">
                        <div class="toast-body">An error occurred while deleting the course. Please try again.</div>
                        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                    </div>
                </div>
            `;

            $('.position-fixed.bottom-0.end-0').append(toastHtml);
            let toast = new bootstrap.Toast($('.toast').last()[0], { delay: 5000 });
            toast.show();
        }
    });
});

// Xử lý submit form xác nhận action (Up to Public, Unpublish, Hide) bằng AJAX
$('#confirmActionForm').on('submit', function(e) {
    e.preventDefault();
    var $form = $(this);
    var actionUrl = $form.attr('action');
    var formData = $form.serialize();

    // Lưu lại các giá trị filter hiện tại để tải lại sau khi xử lý
    let category = $('#filterCategory').val();
    let status = $('#courseStatus').val();
    let price = $('#filterPrice').val();
    let page = 0;

    $.ajax({
        url: actionUrl,
        type: 'POST',
        data: formData,
        success: function(response) {
            // Đóng modal
            bootstrap.Modal.getInstance(document.getElementById('confirmActionModal')).hide();
            // Tải lại danh sách khoá học với filter hiện tại
            filterCourses(page);
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

// Đảm bảo các hàm action luôn được bind lại sau khi AJAX load content
window.upToPublic = upToPublic;
window.unpublishCourse = unpublishCourse;
window.hideCourse = hideCourse;
window.deleteCourse = deleteCourse;
