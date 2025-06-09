package com.OLearning.service.cart;

import com.OLearning.dto.cart.CartDTO;

public interface CartService {
    CartDTO addToCart(Long userId, Long courseId);
    CartDTO getCartByUserId(Long userId);
    void removeFromCart(Long cartDetailId);
}
