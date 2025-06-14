package com.OLearning.service.cart;

import com.OLearning.entity.Orders;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface CartService {
    Map<String, Object> getCartDetails(String cartJson);
    Map<String, Object> addCourseToCart(String cartJson, Long courseId, String userEmail);
    Map<String, Object> removeCartDetail(String cartJson, String cartDetailId);
    Map<String, Object> clearCart();
    String processCheckout(String cartJson, String ipAddr);
    void completeCheckout(Map<String, Object> cart, Orders order, boolean useCoins);
}