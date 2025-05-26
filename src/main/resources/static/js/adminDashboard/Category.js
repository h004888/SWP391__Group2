$(document).ready(function () {
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