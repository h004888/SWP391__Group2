document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.add-to-cart-btn').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Kiểm tra đăng nhập - chỉ redirect nếu chắc chắn chưa đăng nhập
            if (typeof window.userId === 'undefined' || window.userId === null || window.userId === 0) {
                window.location.href = '/login';
                return;
            }
            
            const courseId = btn.getAttribute('data-course-id');
            fetch('/cart/add/' + courseId, {
                method: 'POST',
                credentials: 'include',
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
                        // Kiểm tra nếu lỗi là do chưa đăng nhập thì redirect
                        if (data.message && data.message.includes('đăng nhập')) {
                            window.location.href = '/login';
                        } else {
                            showNotification(data.message || 'An error occurred!', 'error');
                        }
                    }
                })
                .catch(() => showNotification('An error occurred!', 'error'));
        });
    });
});
