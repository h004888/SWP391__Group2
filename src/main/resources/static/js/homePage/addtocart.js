document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.add-to-cart-btn').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const courseId = btn.getAttribute('data-course-id');
            fetch('/cart/add/' + courseId, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Đã thêm vào giỏ hàng!');
                        document.dispatchEvent(new Event('cart-updated'));
                    } else {
                        alert(data.error || 'Có lỗi xảy ra!');
                    }
                })
                .catch(() => alert('Có lỗi xảy ra!'));
        });
    });
});
