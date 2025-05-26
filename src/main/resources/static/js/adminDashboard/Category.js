// Hàm gắn sự kiện xóa danh mục
function bindDeleteCategoryEvent() {
    $('#loadCategory').off('click', '.delete').on('click', '.delete', function () {
        const id = $(this).data("id");

        $.ajax({
            url: '/admin/categories/delete',
            type: 'GET',
            data: { id: id },
            success: function (fragmentHtml) {
                $('#loadCategory').html(fragmentHtml);
                // Sau khi load lại fragment → gắn lại event
                bindDeleteCategoryEvent();
                bindSearchCategoryEvent();
            },
            error: function () {
                alert('Lỗi khi xóa danh mục.');
            }
        });
    });
}

// Hàm gắn sự kiện tìm kiếm tự động khi nhập
function bindSearchCategoryEvent() {
    let timer;
    const delay = 300;

    $('#loadCategory').off('input', '#searchInput').on('input', '#searchInput', function () {
        clearTimeout(timer);
        const keyword = $(this).val().trim();

        timer = setTimeout(function () {
            $.ajax({
                url: '/admin/categories/search',
                method: 'GET',
                data: { name: keyword },
                success: function (data) {
                    $('#loadCategory').html(data);
                    $('#searchInput').val(keyword); // giữ lại value sau reload
                    // Sau khi load lại fragment → gắn lại event
                    bindDeleteCategoryEvent();
                    bindSearchCategoryEvent();
                },
                error: function () {
                    alert('Lỗi khi tìm kiếm danh mục.');
                }
            });
        }, delay);
    });
}

// Hàm khởi tạo sự kiện khi load trang
$(document).ready(function () {
    bindDeleteCategoryEvent();
    bindSearchCategoryEvent();
});
