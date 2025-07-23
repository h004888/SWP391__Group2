package com.OLearning.service.payment;

import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class CartServiceUtil {
    public static void updateCartCookie(Map<String, Object> cart, HttpServletResponse response, Long userId, ObjectMapper objectMapper) throws Exception {
        String cartJson = objectMapper.writeValueAsString(cart);
        String encodedCartJson = Base64.getEncoder().encodeToString(cartJson.getBytes(StandardCharsets.UTF_8));
        Cookie cartCookie = new Cookie("cart_" + userId, encodedCartJson);
        cartCookie.setPath("/");
        cartCookie.setMaxAge(14 * 24 * 60 * 60);
        cartCookie.setHttpOnly(true);
        response.addCookie(cartCookie);
    }

    public static String getCartCookie(HttpServletRequest request, Long userId) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("cart_" + userId)) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    public static String getWishlistCookie(HttpServletRequest request, Long userId) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("wishlist_" + userId)) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    public static Long getLongValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalStateException("Value is not a number: " + (value != null ? value.getClass().getName() : "null"));
    }

    public static Long getUserIdFromUserDetails(UserDetails userDetails, UserRepository userRepository) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return user.getUserId();
    }

    public static String getBuyNowCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("buy_now")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void clearBuyNowCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("buy_now", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static void updateWishlistCookie(Map<String, Object> wishlist, HttpServletResponse response, Long userId) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String wishlistJson = objectMapper.writeValueAsString(wishlist);
        String encodedWishlistJson = Base64.getEncoder().encodeToString(wishlistJson.getBytes(StandardCharsets.UTF_8));
        Cookie wishlistCookie = new Cookie("wishlist_" + userId, encodedWishlistJson);
        wishlistCookie.setPath("/");
        wishlistCookie.setMaxAge(14 * 24 * 60 * 60);
        wishlistCookie.setHttpOnly(true);
        response.addCookie(wishlistCookie);
    }
} 