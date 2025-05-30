$(document).ready(function () {
    let typingTimer;
    const debounceDelay = 300;
    let currentName = '';
    let currentSort = '';

    $(document).on('input', '#searchInput', function () {
        clearTimeout(typingTimer);
        typingTimer = setTimeout(() => {
            currentName = $(this).val().trim();
            fetchCategories(0);
        }, debounceDelay);
    });

    $(document).on('change', '#sortSelect', function () {
        currentSort = $(this).val();
        fetchCategories(0);
    });

    $(document).on('click', '.pagination a', function (e) {
        e.preventDefault();
        const page = $(this).data('page');
        if (page !== undefined) {
            fetchCategories(page);
        }
    });

    function fetchCategories(page) {
        $.ajax({
            url: '/admin/categories',
            type: 'GET',
            data: { name: currentName, sort: currentSort, page, size: 5 },
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            success: function (response) {
                $('#loadCategory').html(response);
            },
            error: function (xhr, status, error) {
                console.error("Lỗi AJAX:", status, error);
                alert("Lỗi khi tải danh mục.");
            }
        });
    }

  
});
