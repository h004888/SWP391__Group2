let currentPage = 0;
let pageSize = 5;



function getFilterValues() {
    const name = $('#filterUsername').val()?.trim() || '';
    const sort = $('#sortSelect').val() || '';

    console.log('Filter values:', { name, sort });

    return {
        name: name,
        sort: sort
    };
}

function createAjaxData(page = 0, size = pageSize) {
    const filters = getFilterValues();
    const data = {
        name: filters.name || null,
        sort: filters.sort || null,
        page: page,
        size: size
    };

    console.log('AJAX data:', data);

    return data;
}

function handleAjaxResponse(tableData, paginationData, tableBody, paginationContainer) {
    // Update table
    if (tableData && tableData.trim() !== '') {
        tableBody.html(tableData);
    } else {
        tableBody.html('<tr><td colspan="4" class="text-center">No categories found</td></tr>');
    }

    // Update pagination
    if (paginationData && paginationData.trim() !== '') {
        paginationContainer.html(paginationData);
    } else {
        paginationContainer.html('');
    }

    // Re-attach event handlers for new elements
    attachEventHandlers();
}

function handleAjaxError(error, tableBody, paginationContainer) {
    console.error("Error loading category data", error);
    tableBody.html('<tr><td colspan="4" class="text-center text-danger">Error loading data</td></tr>');
    paginationContainer.html('');
}

function filterCategories(page = 0, size = pageSize) {
    const tableBody = $('#categoryTableBody');
    const paginationContainer = $('.category-pagination-container');

    currentPage = page;

    console.log('Filtering categories for page:', page, 'size:', size);

    // Show loading state
    tableBody.html('<tr><td colspan="4" class="text-center">Loading...</td></tr>');

    const ajaxData = createAjaxData(page, size);

    console.log('Making AJAX requests to:', {
        filterUrl: '/admin/category/filter',
        paginationUrl: '/admin/category/pagination'
    });

    Promise.all([
        $.ajax({
            url: '/admin/category/filter',
            method: 'GET',
            data: ajaxData
        }),
        $.ajax({
            url: '/admin/category/pagination',
            method: 'GET',
            data: ajaxData
        })
    ]).then(function ([tableData, paginationData]) {
        console.log('AJAX responses received:', {
            tableDataLength: tableData ? tableData.length : 0,
            paginationDataLength: paginationData ? paginationData.length : 0
        });
        handleAjaxResponse(tableData, paginationData, tableBody, paginationContainer);
    }).catch(function (error) {
        console.error('AJAX error:', error);
        handleAjaxError(error, tableBody, paginationContainer);
    });
}

function resetAndFilter() {
    console.log('resetAndFilter called');
    currentPage = 0;
    // Don't automatically reset sort - let user choose
    filterCategories(0, pageSize);
}

// Function to load course details for a category
function loadCourseDetails(categoryId, categoryName) {
    // Navigate to course list content page instead of opening modal
    window.location.href = '/admin/category/' + categoryId + '/courses';
}

// Function to attach event handlers
function attachEventHandlers() {
    // Re-attach category name link events
    $('.category-name-link').off('click').on('click', function(e) {
        e.preventDefault();
        const categoryId = $(this).data('id');
        const categoryName = $(this).text().trim();
        loadCourseDetails(categoryId, categoryName);
    });

    // Re-attach delete category events
    $('.delete-category').off('click').on('click', function() {
        const categoryId = $(this).data('id');
        $.ajax({
            url: '/admin/category/delete/' + categoryId,
            method: 'DELETE',
            success: function(response) {
                // Reload current page after successful deletion
                filterCategories(currentPage, pageSize);
            },
            error: function(xhr, status, error) {
                console.error('Error deleting category:', error);
                alert('Error deleting category. Please try again.');
            }
        });
    });

    // Re-attach edit category events
    $('.edit-category-btn').off('click').on('click', function() {
        const categoryId = $(this).data('id');
        const categoryName = $(this).data('name');
        $('#editCategoryId').val(categoryId);
        $('#editCategoryName').val(categoryName);
    });
}

// Function to show alerts
function showAlert(message, type) {
    const alertClass = type === 'success' ? 'alert-success' : 'alert-danger';
    const alertHtml = `
        <div class="alert ${alertClass} alert-dismissible fade show position-fixed top-0 end-0 m-4 shadow"
             role="alert" style="z-index: 1055; min-width: 300px;">
            <strong>${message}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

    // Remove existing alerts
    $('.alert').remove();

    // Add new alert
    $('body').append(alertHtml);

    // Auto-hide after 5 seconds
    setTimeout(function() {
        $('.alert').fadeOut('slow');
    }, 5000);
}

$(document).on('click', '.pagination .page-link', function (e) {
    e.preventDefault();
    const page = parseInt($(this).data('page'));
    if (!isNaN(page) && page >= 0) {
        filterCategories(page, pageSize);
    }
});

$(document).ready(function () {
    console.log('Document ready - initializing category management');

    // Initial load
    filterCategories(0, pageSize);

    // Filter events
    $('#sortSelect').on('change', function () {
        console.log('Sort select changed:', $(this).val());
        resetAndFilter();
    });

    let searchTimer;
    $('#filterUsername').on('input', function () {
        console.log('Search input changed:', $(this).val());
        clearTimeout(searchTimer);
        searchTimer = setTimeout(resetAndFilter, 500);
    });

    $('#filterForm').on('submit', function (e) {
        console.log('Filter form submitted');
        e.preventDefault();
        resetAndFilter();
    });

    // Clear filters button
    $('#clearFilters').on('click', function() {
        console.log('Clear filters clicked');
        $('#filterUsername').val('');
        $('#sortSelect').val('');
        resetAndFilter();
    });

    // Back to categories button
    $(document).on('click', '#backToCategories', function() {
        window.location.href = '/admin/category';
    });

    // Add category form submission
    $('#categoryForm').on('submit', function(e) {
        e.preventDefault();

        const formData = $(this).serialize();
        const submitBtn = $(this).find('button[type="submit"]');
        const originalText = submitBtn.html();

        // Disable button and show loading
        submitBtn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i>Adding...');

        $.ajax({
            url: $(this).attr('action'),
            method: 'POST',
            data: formData,
            success: function(response) {
                // Close modal and reset form
                $('#categoryModal').modal('hide');
                $('#categoryForm')[0].reset();

                // Reload categories
                filterCategories(0, pageSize);

                // Show success message
                showAlert('Category added successfully!', 'success');
            },
            error: function(xhr, status, error) {
                console.error('Error adding category:', error);
                showAlert('Error adding category. Please try again.', 'error');
            },
            complete: function() {
                // Re-enable button
                submitBtn.prop('disabled', false).html(originalText);
            }
        });
    });

    // Delete Category Modal - Fill data when modal opens
    $('.delete-category').on('click', function() {
        const categoryId = $(this).data('id');
        const categoryName = $(this).data('name');
        $('#deleteCategoryId').val(categoryId);
        $('#deleteCategoryName').text(categoryName);
    });

    // Edit Category Modal - Fill data when modal opens
    $('.edit-category-btn').on('click', function() {
        const categoryId = $(this).data('id');
        const categoryName = $(this).data('name');
        $('#editCategoryId').val(categoryId);
        $('#editCategoryName').val(categoryName);
    });

    // Edit Category form submission via AJAX
    $('#editCategoryForm').on('submit', function(e) {
        e.preventDefault();
        
        const formData = $(this).serialize();
        const submitBtn = $(this).find('button[type="submit"]');
        const originalText = submitBtn.html();
        
        // Disable button and show loading
        submitBtn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i>Saving...');
        
        $.ajax({
            url: $(this).attr('action'),
            method: 'POST',
            data: formData,
            success: function(response) {
                // Kiểm tra response JSON từ server
                if (response.success) {
                    // Thành công
                    $('#editCategoryModal').modal('hide');
                    $('#editCategoryForm')[0].reset();
                    filterCategories(0, pageSize);
                    showAlert(response.success, 'success');
                } else if (response.error) {
                    // Có lỗi từ server
                    showAlert(response.error, 'error');
                } else {
                    // Fallback
                    showAlert('Category updated successfully!', 'success');
                }
            },
            error: function(xhr, status, error) {
                console.error('Error updating category:', error);
                let errorMessage = 'Error updating category. Please try again.';
                
                // Cố gắng parse JSON error response
                if (xhr.responseJSON && xhr.responseJSON.error) {
                    errorMessage = xhr.responseJSON.error;
                }
                
                showAlert(errorMessage, 'error');
            },
            complete: function() {
                submitBtn.prop('disabled', false).html(originalText);
            }
        });
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        $('.alert').fadeOut('slow');
    }, 5000);
});
