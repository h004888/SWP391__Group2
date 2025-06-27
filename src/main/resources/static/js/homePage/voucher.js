// voucher.js - dùng chung cho cart và course-detail

window.voucherJS = (function() {
    function formatNumberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    function closeVoucherModal() {
        const modal = document.getElementById('voucherModal');
        if (modal) modal.style.display = 'none';
    }

    // --- CART CONTEXT ---
    function initCartVoucher(options) {
        const userId = options.userId;
        window.selectedVouchers = {};
        function saveSelectedVouchers() {
            localStorage.setItem('selectedVouchers', JSON.stringify(window.selectedVouchers));
        }
        function loadSelectedVouchers() {
            const data = localStorage.getItem('selectedVouchers');
            if (data) {
                window.selectedVouchers = JSON.parse(data);
            } else {
                window.selectedVouchers = {};
            }
        }
        document.addEventListener('DOMContentLoaded', function() {
            loadSelectedVouchers();
            initializeVoucherButtons();
            if (typeof options.initializeCheckoutForm === 'function') options.initializeCheckoutForm();
            restoreVoucherUI();
        });
        function initializeVoucherButtons() {
            document.querySelectorAll('.voucher-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    const courseId = this.getAttribute('data-course-id');
                    fetch(`/vouchers/course/${courseId}/user/${userId}`)
                        .then(res => res.json())
                        .then(data => {
                            displayVouchers(data, courseId);
                        })
                        .catch(() => {
                            document.getElementById('voucher-list').innerHTML = '<p class="text-danger">Lỗi khi tải voucher. Vui lòng thử lại.</p>';
                            document.getElementById('voucherModal').style.display = 'block';
                        });
                });
            });
        }
        function displayVouchers(data, courseId) {
            let html = '';
            let selectedVoucherId = window.selectedVouchers[courseId];
            if (!data || data.length === 0) {
                html = '<p>Không có voucher phù hợp.</p>';
            } else {
                data.forEach(voucher => {
                    const voucherCode = voucher.voucherCode || voucher.code || 'N/A';
                    const discount = voucher.discount || 0;
                    const voucherId = voucher.voucherId || voucher.id;
                    let isSelected = selectedVoucherId == voucherId;
                    html += `
            <div class='d-flex justify-content-between align-items-center mb-2 p-2 border rounded'>
              <div>
                <strong>${voucherCode}</strong><br>
                <small class="text-muted">Giảm ${discount}%</small>
              </div>
              <button class='btn btn-sm ${isSelected ? 'btn-secondary' : 'btn-success'}'
                onclick='voucherJS.${isSelected ? `removeVoucherCart(${voucherId},${courseId})` : `applyVoucherCart(${voucherId},${courseId})`}'>
                ${isSelected ? 'Hủy chọn' : 'Áp dụng'}
              </button>
            </div>`;
                });
            }
            document.getElementById('voucher-list').innerHTML = html;
            document.getElementById('voucherModal').style.display = 'block';
        }
        window.voucherJS.applyVoucherCart = function(voucherId, courseId) {
            // Đảm bảo 1 voucher chỉ áp dụng cho 1 course tại 1 thời điểm
            let previousCourseId = null;
            for (const [cId, vId] of Object.entries(window.selectedVouchers)) {
                if (vId == voucherId && cId != courseId) {
                    previousCourseId = cId;
                    break;
                }
            }
            if (previousCourseId) {
                window.voucherJS.removeVoucherCart(voucherId, previousCourseId);
                delete window.selectedVouchers[previousCourseId];
            }
            window.selectedVouchers[courseId] = voucherId;
            saveSelectedVouchers();
            fetch('/cart/apply-voucher', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: JSON.stringify({
                    userId: userId,
                    courseId: courseId,
                    voucherId: voucherId
                })
            })
                .then(res => res.json())
                .then(data => {
                    if (typeof options.updatePriceUI === 'function') options.updatePriceUI(courseId, data);
                    closeVoucherModal();
                    if (typeof options.updateCartTotal === 'function') options.updateCartTotal();
                });
        };
        window.voucherJS.removeVoucherCart = function(voucherId, courseId) {
            delete window.selectedVouchers[courseId];
            saveSelectedVouchers();
            if (typeof options.updatePriceUI === 'function') options.updatePriceUI(courseId, {});
            closeVoucherModal();
            if (typeof options.updateCartTotal === 'function') options.updateCartTotal();
        };
        function restoreVoucherUI() {
            for (const [courseId, voucherId] of Object.entries(window.selectedVouchers)) {
                fetch('/cart/apply-voucher', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                    body: JSON.stringify({
                        userId: userId,
                        courseId: courseId,
                        voucherId: voucherId
                    })
                })
                    .then(res => res.json())
                    .then(data => {
                        if (typeof options.updatePriceUI === 'function') options.updatePriceUI(courseId, data);
                    });
            }
            setTimeout(() => {
                if (typeof options.updateCartTotal === 'function') options.updateCartTotal();
            }, 300);
        }
        // Đóng modal khi click outside
        document.addEventListener('click', function(e) {
            const modal = document.getElementById('voucherModal');
            if (e.target === modal) {
                closeVoucherModal();
            }
        });
        // Khi xóa course khỏi cart, cũng xóa khỏi selectedVouchers và localStorage
        document.querySelectorAll('a[href^="/cart/remove/"]').forEach(btn => {
            btn.addEventListener('click', function() {
                const tr = this.closest('tr');
                if (tr) {
                    const courseId = tr.getAttribute('data-course-id');
                    if (window.selectedVouchers && window.selectedVouchers[courseId]) {
                        delete window.selectedVouchers[courseId];
                        saveSelectedVouchers();
                    }
                }
            });
        });
    }

    // --- DETAIL CONTEXT ---
    function initDetailVoucher(options) {
        const userId = options.userId;
        const courseId = options.courseId;
        window.selectedVoucher = null;
        function saveSelectedVoucher() {
            localStorage.setItem('selectedVoucher_' + courseId, window.selectedVoucher);
        }
        function loadSelectedVoucher() {
            const data = localStorage.getItem('selectedVoucher_' + courseId);
            if (data) {
                window.selectedVoucher = data;
            } else {
                window.selectedVoucher = null;
            }
        }
        document.addEventListener('DOMContentLoaded', function() {
            loadSelectedVoucher();
            initializeVoucherButton();
            restoreVoucherUI();
        });
        function initializeVoucherButton() {
            document.querySelectorAll('.voucher-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    fetch(`/vouchers/course/${courseId}/user/${userId}`)
                        .then(res => res.json())
                        .then(data => {
                            displayVouchers(data);
                        })
                        .catch(() => {
                            document.getElementById('voucher-list').innerHTML = '<p class="text-danger">Lỗi khi tải voucher. Vui lòng thử lại.</p>';
                            document.getElementById('voucherModal').style.display = 'block';
                        });
                });
            });
        }
        function displayVouchers(data) {
            let html = '';
            let selectedVoucherId = window.selectedVoucher;
            if (!data || data.length === 0) {
                html = '<p>Không có voucher phù hợp.</p>';
            } else {
                data.forEach(voucher => {
                    const voucherCode = voucher.voucherCode || voucher.code || 'N/A';
                    const discount = voucher.discount || 0;
                    const voucherId = voucher.voucherId || voucher.id;
                    let isSelected = selectedVoucherId == voucherId;
                    html += `
            <div class='d-flex justify-content-between align-items-center mb-2 p-2 border rounded'>
              <div>
                <strong>${voucherCode}</strong><br>
                <small class="text-muted">Giảm ${discount}%</small>
              </div>
              <button class='btn btn-sm ${isSelected ? 'btn-secondary' : 'btn-success'}'
                onclick='voucherJS.${isSelected ? `removeVoucherDetail(${voucherId})` : `applyVoucherDetail(${voucherId})`}'>
                ${isSelected ? 'Hủy chọn' : 'Áp dụng'}
              </button>
            </div>`;
                });
            }
            document.getElementById('voucher-list').innerHTML = html;
            document.getElementById('voucherModal').style.display = 'block';
        }
        window.voucherJS.applyVoucherDetail = function(voucherId) {
            window.selectedVoucher = voucherId;
            saveSelectedVoucher();
            if (typeof options.syncVoucherToBuyNowForm === 'function') options.syncVoucherToBuyNowForm();
            fetch('/cart/apply-voucher', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: JSON.stringify({
                    userId: userId,
                    courseId: courseId,
                    voucherId: voucherId
                })
            })
                .then(res => res.json())
                .then(data => {
                    if (typeof options.updatePriceUI === 'function') options.updatePriceUI(data);
                    closeVoucherModal();
                });
        };
        window.voucherJS.removeVoucherDetail = function(voucherId) {
            window.selectedVoucher = null;
            saveSelectedVoucher();
            if (typeof options.syncVoucherToBuyNowForm === 'function') options.syncVoucherToBuyNowForm();
            if (typeof options.updatePriceUI === 'function') options.updatePriceUI({});
            closeVoucherModal();
        };
        function restoreVoucherUI() {
            if (typeof options.syncVoucherToBuyNowForm === 'function') options.syncVoucherToBuyNowForm();
            if (window.selectedVoucher) {
                window.voucherJS.applyVoucherDetail(window.selectedVoucher);
            }
        }
        // Đóng modal khi click outside
        document.addEventListener('click', function(e) {
            const modal = document.getElementById('voucherModal');
            if (e.target === modal) {
                closeVoucherModal();
            }
        });
    }

    return {
        formatNumberWithCommas,
        closeVoucherModal,
        initCartVoucher,
        initDetailVoucher
    };
})();