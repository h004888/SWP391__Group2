$(document).ready(function () {

    $('#categoryTable').on('click', '.delete-category', function () {
        const id = $(this).data('id');
        if (confirm("Bạn có chắc chắn muốn xóa danh mục này?")) {
            deleteCategoryById(id);
        }
    });
    // ⏱ Gõ đến đâu search đến đó (debounce 300ms)
    let typingTimer;
    $('#searchInput').on('input', function () {
        clearTimeout(typingTimer);
        typingTimer = setTimeout(fetchData, 300); // đợi 300ms sau khi ngừng gõ
    });
    // 🔄 Khi chọn sort thì cũng gọi lại
    $('#sortSelect').on('change', fetchData);

    // ❌ Ngăn reload nếu form bị submit
    $('#filterForm').on('submit', function (e) {
        e.preventDefault();
    });

});


function deleteCategoryById(id) {
    $.ajax({
        url: '/admin/categories/delete?id=' + id,
        method: 'GET',
        success: function (data) {
            console.log("Xóa thành công danh mục có ID: " + id);
            fetchData(); // Cập nhật lại bảng danh mục sau khi xóa
        },
        error: function () {
            alert("Lỗi khi xóa danh mục!");
        }
    });
}



function fetchData() {
    const name = $('#searchInput').val();
    const select = $('#sortSelect').val();

    $.ajax({
        url: '/admin/categories/search',
        method: 'GET',
        data: {
            name: name,
            select: select
        },
        success: function (data) {
            $('#categoryTable').html(data);
        },
        error: function () {
            console.error("Lỗi khi tìm kiếm dữ liệu");
        }
    });
}