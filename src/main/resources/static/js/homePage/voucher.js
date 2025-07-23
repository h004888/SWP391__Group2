// voucher.js - dùng chung cho cart và course-detail

window.voucherJS = (function() {
    function formatNumberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    function closeVoucherModal() {
        const modal = document.getElementById('voucherModal');
        if (modal) {
            modal.style.display = 'none';
        }
    }

    // --- CART CONTEXT ---
    function initCartVoucher(options) {
        // Chỉ khởi tạo nếu user đã login
        if (!window.userId || window.userId === 0) {
            return;
        }

        const userId = window.userId;
        window.selectedVouchers = {};

        function saveSelectedVouchers() {
            localStorage.setItem('selectedVouchers', JSON.stringify(window.selectedVouchers));
        }

        function loadSelectedVouchers() {
            const data = localStorage.getItem('selectedVouchers');
            if (data && data !== 'null' && data !== 'undefined') {
                try {
                    window.selectedVouchers = JSON.parse(data);
                } catch (e) {
                    window.selectedVouchers = {};
                }
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
                    fetch(`/home/vouchers/course/${courseId}/user/${userId}`, {
                        credentials: 'include'
                    })
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
                    const expiry = voucher.expiryDate ? `<span class="voucher-expiry"><i class='fas fa-clock me-1'></i>HSD: ${voucher.expiryDate}</span>` : '';
                    let isSelected = selectedVoucherId == voucherId;
                    html += `
            <div class='voucher-card-cart${isSelected ? ' selected' : ''}'>
              <div class='voucher-icon'><i class='fas fa-ticket-alt'></i></div>
              <div class='voucher-info'>
                <div class='voucher-title'>${voucherCode}</div>
                <div><span class='voucher-discount'>-${discount}%</span> ${expiry}</div>
              </div>
              <button class='btn voucher-apply-btn ${isSelected ? 'btn-secondary' : 'btn-success'}'
                onclick='voucherJS.${isSelected ? `removeVoucherCart(${voucherId},${courseId})` : `applyVoucherCart(${voucherId},${courseId})`}'>
                ${isSelected ? 'Huỷ' : 'Áp dụng'}
              </button>
            </div>`;
                });
            }
            document.getElementById('voucher-list').innerHTML = html;
            document.getElementById('voucherModal').style.display = 'block';
        }

        window.voucherJS.applyVoucherCart = function(voucherId, courseId) {
            // Validate voucherId
            if (!voucherId || voucherId === 'null' || voucherId === 'undefined') {
                showNotification('Invalid voucher selected', 'error');
                return;
            }
            
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
            
            fetch('/home/cart/apply-voucher', {
                method: 'POST',
                credentials: 'include',
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
                .then(res => {
                    if (!res.ok) {
                        throw new Error(`HTTP error! status: ${res.status}`);
                    }
                    const contentType = res.headers.get('content-type');
                    if (contentType && contentType.includes('application/json')) {
                        return res.json();
                    } else {
                        throw new Error('Server returned HTML instead of JSON');
                    }
                })
                .then(data => {
                    if (data.error) {
                        delete window.selectedVouchers[courseId];
                        saveSelectedVouchers();
                        if (typeof options.updatePriceUI === 'function') options.updatePriceUI(courseId, {});
                    } else {
                        if (typeof options.updatePriceUI === 'function') options.updatePriceUI(courseId, data);
                    }
                    closeVoucherModal();
                    if (typeof options.updateCartTotal === 'function') options.updateCartTotal();
                })
                .catch(error => {
                    delete window.selectedVouchers[courseId];
                    saveSelectedVouchers();
                    if (typeof options.updatePriceUI === 'function') options.updatePriceUI(courseId, {});
                    showNotification('Có lỗi xảy ra khi áp dụng voucher', 'error');
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
                // Validate voucherId
                if (!voucherId || voucherId === 'null' || voucherId === 'undefined') {
                    delete window.selectedVouchers[courseId];
                    continue;
                }
                
                fetch('/home/cart/apply-voucher', {
                    method: 'POST',
                    credentials: 'include',
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
                    .then(res => {
                        if (!res.ok) {
                            throw new Error(`HTTP error! status: ${res.status}`);
                        }
                        const contentType = res.headers.get('content-type');
                        if (contentType && contentType.includes('application/json')) {
                            return res.json();
                        } else {
                            throw new Error('Server returned HTML instead of JSON');
                        }
                    })
                    .then(data => {
                        if (data.error) {
                            delete window.selectedVouchers[courseId];
                            saveSelectedVouchers();
                            if (typeof options.updatePriceUI === 'function') options.updatePriceUI(courseId, {});
                        } else {
                            if (typeof options.updatePriceUI === 'function') options.updatePriceUI(courseId, data);
                        }
                    })
                    .catch(error => {
                        delete window.selectedVouchers[courseId];
                        saveSelectedVouchers();
                        if (typeof options.updatePriceUI === 'function') options.updatePriceUI(courseId, {});
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
        document.querySelectorAll('a[href^="/home/cart/remove/"]').forEach(btn => {
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
        // Chỉ khởi tạo nếu user đã login và course chưa được mua
        if (!window.userId || window.userId === 0) {
            return;
        }

        const userId = window.userId;
        const courseId = options.courseId;
        
        // Kiểm tra xem course đã được mua chưa
        const isEnrolled = options.isEnrolled || false;
        if (isEnrolled) {
            return; // Không cần voucher nếu đã mua course
        }

        window.selectedVoucher = null;
        
        function saveSelectedVoucher() {
            localStorage.setItem('selectedVoucher_' + courseId, window.selectedVoucher);
        }
        
        function loadSelectedVoucher() {
            const data = localStorage.getItem('selectedVoucher_' + courseId);
            if (data && data !== 'null' && data !== 'undefined') {
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
                    fetch(`/home/vouchers/course/${courseId}/user/${userId}`, {
                        credentials: 'include'
                    })
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
            // Validate voucherId
            if (!voucherId || voucherId === 'null' || voucherId === 'undefined') {
                showNotification('Invalid voucher selected', 'error');
                return;
            }
            
            window.selectedVoucher = voucherId;
            saveSelectedVoucher();
            if (typeof options.syncVoucherToBuyNowForm === 'function') options.syncVoucherToBuyNowForm();
            
            fetch('/home/cart/apply-voucher', {
                method: 'POST',
                credentials: 'include',
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
                .then(res => {
                    if (!res.ok) {
                        throw new Error(`HTTP error! status: ${res.status}`);
                    }
                    const contentType = res.headers.get('content-type');
                    if (contentType && contentType.includes('application/json')) {
                        return res.json();
                    } else {
                        throw new Error('Server returned HTML instead of JSON');
                    }
                })
                .then(data => {
                    if (data.error) {
                        window.selectedVoucher = null;
                        saveSelectedVoucher();
                        if (typeof options.syncVoucherToBuyNowForm === 'function') options.syncVoucherToBuyNowForm();
                        if (typeof options.updatePriceUI === 'function') options.updatePriceUI({});
                        showNotification('Invalid Voucher: ' + data.error, 'error');
                    } else {
                        if (typeof options.updatePriceUI === 'function') options.updatePriceUI(data);
                    }
                    closeVoucherModal();
                })
                .catch(error => {
                    window.selectedVoucher = null;
                    saveSelectedVoucher();
                    if (typeof options.syncVoucherToBuyNowForm === 'function') options.syncVoucherToBuyNowForm();
                    if (typeof options.updatePriceUI === 'function') options.updatePriceUI({});
                    showNotification('An error occurred while applying the voucher.', 'error');
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

    // Lắng nghe sự kiện click vào nút View voucher public

    document.addEventListener('DOMContentLoaded', function() {
        document.querySelectorAll('.view-voucher-btn').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const voucherId = btn.getAttribute('data-voucher-id');
                fetch(`/home/vouchers/voucher/${voucherId}/courses`, {
                    credentials: 'include'
                })
                    .then(res => res.json())
                    .then(data => {
                        let html = '';
                        if (!Array.isArray(data) || !data || data.length === 0) {
                            html = '<p class="text-muted">No courses available for this voucher.</p>';
                        } else {
                            data.forEach(course => {
                                html += `<div class='border rounded p-2 mb-2 d-flex justify-content-between align-items-center'>
                                    <div>
                                        <strong>${course.title}</strong><br>
                                        <span class='text-muted'>${course.instructorName || ''}</span>
                                    </div>
                                    ${window.userId && window.userId !== 0 ? `<button class='btn btn-success btn-sm apply-voucher-course-btn' data-course-id='${course.courseId}' data-voucher-id='${voucherId}'>Apply</button>` : ''}
                                </div>`;
                            });
                        }
                        document.getElementById('voucher-courses-list').innerHTML = html;
                        const modal = new bootstrap.Modal(document.getElementById('voucherCoursesModal'));
                        modal.show();
                    })
                    .catch(() => {
                        document.getElementById('voucher-courses-list').innerHTML = '<p class="text-danger">Unable to get course list. Please try again or check your connection.</p>';
                        const modal = new bootstrap.Modal(document.getElementById('voucherCoursesModal'));
                        modal.show();
                    });
            });
        });
    });

    // Xóa voucher khỏi giao diện khi đã sử dụng
    function removeVoucherFromList(voucherId) {
        const voucherCard = document.querySelector(`[data-voucher-id="${voucherId}"]`);
        if (voucherCard) {
            const col = voucherCard.closest('.col-md-6, .col-lg-4');
            if (col) {
                col.remove();
                
                // Cập nhật tổng số voucher
                const totalItemsSpan = document.querySelector('h5 span');
                if (totalItemsSpan) {
                    const currentTotal = parseInt(totalItemsSpan.textContent) || 0;
                    totalItemsSpan.textContent = Math.max(0, currentTotal - 1);
                }

                const remainingVouchers = document.querySelectorAll('#voucher-list .voucher-card');
                if (remainingVouchers.length === 0) {
                    setTimeout(() => {
                        window.location.reload();
                    }, 300);
                }
            }
        }
    }

    return {
        formatNumberWithCommas,
        closeVoucherModal,
        initCartVoucher,
        initDetailVoucher,
        removeVoucherFromList
    };
})();