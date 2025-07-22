
    document.addEventListener('DOMContentLoaded', () => {
    const cartTotalBadge = document.getElementById('cartTotalBadge');
    const wishlistTotalBadge = document.getElementById('wishlistTotalBadge');

    // Function to fetch and update cart total
    const updateCartTotal = async () => {
    try {
    const response = await fetch('/home/cart/total', {
    method: 'GET',
    headers: {
    'X-Requested-With': 'XMLHttpRequest'
}
});
    if (!response.ok) throw new Error('Failed to fetch cart total');
    const data = await response.json();
    if (cartTotalBadge) {
    cartTotalBadge.textContent = data.total || 0;
}
} catch (error) {
    if (cartTotalBadge) {
    cartTotalBadge.textContent = '0';
}
}
};

    // Function to fetch and update wishlist total
    const updateWishlistTotal = async () => {
    try {
    const response = await fetch('/home/wishlist/total', {
    method: 'GET',
    headers: {
    'X-Requested-With': 'XMLHttpRequest'
}
});
    if (!response.ok) throw new Error('Failed to fetch wishlist total');
    const data = await response.json();
    if (wishlistTotalBadge) {
    wishlistTotalBadge.textContent = data.total || 0;
}
} catch (error) {
    if (wishlistTotalBadge) {
    wishlistTotalBadge.textContent = '0';
}
}
};

    // Lắng nghe sự kiện custom để cập nhật cartTotal từ bất kỳ nơi nào
    document.addEventListener('cart-updated', updateCartTotal);
    document.addEventListener('wishlist-updated', updateWishlistTotal);

    // Initial updates
    updateCartTotal();
    updateWishlistTotal();
});