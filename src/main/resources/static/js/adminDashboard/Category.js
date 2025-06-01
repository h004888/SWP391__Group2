$(document).ready(function () {
    let typingTimer;
    const debounceDelay = 300;
    let currentName = '';
    let currentSort = '';
    let currentPage = 0;

    // Xử lý tìm kiếm
    $(document).on('input', '#searchInput', function () {
        clearTimeout(typingTimer);
        typingTimer = setTimeout(() => {
            currentName = $(this).val().trim();
            currentPage = 0;
            fetchCategories(currentPage);
        }, debounceDelay);
    });

    // Xử lý sắp xếp
    $(document).on('change', '#sortSelect', function () {
        currentSort = $(this).val();
        currentPage = 0;
        fetchCategories(currentPage);
    });

    // Xử lý phân trang
    $(document).on('click', '.pagination a', function (e) {
        e.preventDefault();
        const page = $(this).data('page');
        if (page !== undefined) {
            currentPage = page;
            fetchCategories(currentPage);
        }
    });

    // Xử lý xoá
    $(document).on('click', '.delete-category', function (e) {
        e.preventDefault();

        const id = $(this).data('id');
        if (!confirm('Bạn có chắc chắn muốn xoá danh mục này?')) {
            return;
        }

        $.ajax({
            url: '/admin/categories/delete',
            type: 'GET',
            data: { id: id },
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            success: function () {
                fetchCategories(currentPage); // Không reset về trang đầu
            },
            error: function (xhr, status, error) {
                console.error("Lỗi khi xoá danh mục:", status, error);
                alert("Đã xảy ra lỗi khi xoá danh mục.");
            }
        });
    });

    // Hàm gọi danh sách category bằng AJAX
    function fetchCategories(page) {
        $.ajax({
            url: '/admin/categories',
            type: 'GET',
            data: {
                name: currentName,
                sort: currentSort,
                page: page,
                size: 5
            },
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            success: function (response) {
                $('#categoryPage').html(response);
            },
            error: function (xhr, status, error) {
                console.error("Lỗi AJAX:", status, error);
                alert("Lỗi khi tải danh mục.");
            }
        });
    }

    // Load ban đầu (nếu cần)
    fetchCategories(currentPage);
});
