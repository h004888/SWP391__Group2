package com.OLearning.service.cart;

import com.OLearning.entity.Order;
import java.util.Map;

public interface CartService {
    Map<String, Object> getCartDetails(String encodedCartJson, String userEmail);
    Map<String, Object> addCourseToCart(String encodedCartJson, Long courseId, String userEmail);
    Map<String, Object> removeCartDetail(String encodedCartJson, String cartDetailId, String userEmail);
    Map<String, Object> clearCart(String userEmail);
    String processCheckout(String encodedCartJson, String userEmail);
    void completeCheckout(Map<String, Object> cart, Order order, boolean useCoins, String refCode);
    boolean isCourseInCart(String encodedCartJson, Long courseId, String userEmail);
}