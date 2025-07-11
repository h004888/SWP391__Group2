document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.add-to-cart-btn').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            // Kiểm tra đăng nhập
            if (!window.userId || window.userId == 0) {
                window.location.href = '/login'; // Đổi lại nếu URL login khác
                return;
            }
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
                        showNotification('Added to cart successfully!', 'success');
                        document.dispatchEvent(new Event('cart-updated'));
                    } else {
                        showNotification(data.error || 'An error occurred!', 'error');
                    }
                })
                .catch(() => showNotification('An error occurred!', 'error'));
        });
    });
});
