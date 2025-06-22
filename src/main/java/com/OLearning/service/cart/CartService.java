package com.OLearning.service.cart;

import com.OLearning.entity.Order;
import java.util.Map;

public interface CartService {
    Map<String, Object> getCartDetails(String cartJson, String userEmail);
    Map<String, Object> addCourseToCart(String cartJson, Long courseId, String userEmail);
    Map<String, Object> removeCartDetail(String cartJson, String cartDetailId, String userEmail);
    Map<String, Object> clearCart(String userEmail);
    String processCheckout(String cartJson, String ipAddr, String userEmail);
    void completeCheckout(Map<String, Object> cart, Order order, boolean useCoins, String refCode);
    boolean isCourseInCart(String cartJson, Long courseId, String userEmail);
}