// Wishlist functionality
function toggleWishlist(button, courseId) {
    // Kiểm tra đăng nhập trước khi thực hiện
    if (typeof window.userId === 'undefined' || window.userId === null || window.userId === 0) {
        window.location.href = '/login';
        return;
    }
    
    fetch(`/home/wishlist/toggle/${courseId}`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Update heart icon color
            const heartIcon = button.querySelector('i');
            if (data.action === 'added') {
                heartIcon.style.color = '#dc3545'; // Red color
                heartIcon.classList.remove('far');
                heartIcon.classList.add('fas');
            } else {
                heartIcon.style.color = '#6c757d'; // Gray color
                heartIcon.classList.remove('fas');
                heartIcon.classList.add('far');
            }
            
            // Update wishlist total in header if exists
            updateWishlistTotal(data.total);
            
            // Remove the card from wishlist page if removed
            if (data.action === 'removed' && window.location.pathname === '/home/wishlist') {
                // Update wishlist count immediately
                const countElement = document.querySelector('[data-wishlist-count]');
                if (countElement) {
                    countElement.textContent = data.total;
                }
                
                // Find and remove the card immediately
                const card = button.closest('.col-sm-6.col-lg-4.col-xl-3');
                if (card) {
                    card.remove();
                    
                    // Check if no more items on current page
                    const remainingCards = document.querySelectorAll('.col-sm-6.col-lg-4.col-xl-3');
                    if (remainingCards.length === 0) {
                        // Check if we're not on the first page
                        const urlParams = new URLSearchParams(window.location.search);
                        const currentPage = parseInt(urlParams.get('page') || '0');
                        
                        if (currentPage > 0) {
                            // Go to previous page
                            window.location.href = `/home/wishlist?page=${currentPage - 1}`;
                        } else {
                            // Reload to show empty state
                            location.reload();
                        }
                    } else {
                        // Reload the page to update pagination properly
                        // This ensures courses from next pages move up correctly
                        location.reload();
                    }
                }
            }
        } else {
            showNotification(data.message || 'Failed to update wishlist', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Failed to update wishlist', 'error');
    });
}

function updateWishlistTotal(total) {
    // Update wishlist total in header if exists
    const wishlistTotalElement = document.querySelector('.wishlist-total');
    if (wishlistTotalElement) {
        wishlistTotalElement.textContent = total;
    }
    
    // Update wishlist count in page
    const wishlistCountElement = document.querySelector('[data-wishlist-count]');
    if (wishlistCountElement) {
        wishlistCountElement.textContent = total;
    }
    
    // Trigger custom event to update header badge
    document.dispatchEvent(new CustomEvent('wishlist-updated'));
}

// Function to check wishlist status for all course cards
function checkWishlistStatus() {
    const wishlistButtons = document.querySelectorAll('.wishlist-toggle');
    wishlistButtons.forEach(button => {
        const courseId = button.getAttribute('data-course-id');
        if (courseId) {
            fetch(`/home/wishlist/check/${courseId}`, {
                credentials: 'include'
            })
            .then(response => response.json())
            .then(data => {
                const heartIcon = button.querySelector('i');
                if (data.inWishlist) {
                    heartIcon.style.color = '#dc3545'; // Red color
                    heartIcon.classList.remove('far');
                    heartIcon.classList.add('fas');
                } else {
                    heartIcon.style.color = '#6c757d'; // Gray color
                    heartIcon.classList.remove('fas');
                    heartIcon.classList.add('far');
                }
            })
            .catch(error => {
                console.error('Error checking wishlist status:', error);
            });
        }
    });
}

// Initialize wishlist status on page load
document.addEventListener('DOMContentLoaded', function() {
    checkWishlistStatus();
});

// Listen for AJAX content updates (for pagination and filtering)
document.addEventListener('DOMContentLoaded', function() {
    // Create a MutationObserver to watch for changes in the DOM
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.type === 'childList' && mutation.addedNodes.length > 0) {
                // Check if any new course cards were added
                let hasNewCards = false;
                mutation.addedNodes.forEach(function(node) {
                    if (node.nodeType === 1) { // Element node
                        if (node.querySelector && node.querySelector('.wishlist-toggle')) {
                            hasNewCards = true;
                        }
                        if (node.querySelectorAll && node.querySelectorAll('.wishlist-toggle').length > 0) {
                            hasNewCards = true;
                        }
                    }
                });
                
                if (hasNewCards) {
                    // Wait a bit for the DOM to be fully updated
                    setTimeout(checkWishlistStatus, 100);
                }
            }
        });
    });
    
    // Start observing the document body for changes
    observer.observe(document.body, {
        childList: true,
        subtree: true
    });
});

// Also check wishlist status when window loads (for cases where DOMContentLoaded already fired)
window.addEventListener('load', function() {
    setTimeout(checkWishlistStatus, 200);
});

// Listen for custom events that might indicate content has been updated
document.addEventListener('courses-loaded', function() {
    setTimeout(checkWishlistStatus, 100);
});

document.addEventListener('pagination-updated', function() {
    setTimeout(checkWishlistStatus, 100);
});

// Function to change page size
function changePageSize(size) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('size', size);
    urlParams.set('page', '0'); // Reset to first page when changing size
    window.location.href = `/home/wishlist?${urlParams.toString()}`;
}
