function deleteNotification(form, event) {
    event.preventDefault();
    if (confirm('Delete this notification?')) {
        fetch(form.action, {
            method: 'POST',
            headers: { 'X-Requested-With': 'XMLHttpRequest' },
        }).then(res => {
            // Tự động chuyển hướng theo context
            if (res.ok) {
                if (window.location.pathname.startsWith('/admin/')) {
                    window.location.href = '/admin/notifications';
                } else {
                    window.location.href = '/instructordashboard/notifications';
                }
            } else alert('Delete failed!');
        });
    }
    return false;
} 