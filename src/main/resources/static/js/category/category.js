$(document).ready(function () {
    $('a[data-target="#collapseUtilities"]').on('click', function (e) {
        e.preventDefault();
        $.ajax({
            url: '/admin/categories/fragment',
            method: 'GET',
            success: function (html) {
                $('#main-content').html(html);
            },
            error: function (xhr, status, error) {
                console.error('Error loading categories:', error);
            }
        });
    });
    $('#search-form').on('submit', function (e) {
        e.preventDefault();
        const id = $('#search-category-id').val();

        if (!id || isNaN(id)) {
            alert("Vui lòng nhập ID hợp lệ");
            return;
        }

        $.ajax({
            url: '/admin/categories/' + id,
            method: 'GET',
            success: function (html) {
                $('#main-content').html(html); // phần hiển thị kết quả
            },
            error: function (xhr, status, error) {
                console.error("Lỗi khi tìm category:", error);
                $('#main-content').html('<p class="text-danger">Không tìm thấy Category với ID = ' + id + '</p>');
            }
        });
    });
});
