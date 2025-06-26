package com.OLearning.service.wishlist;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface WishlistService {
    Map<String, Object> decode(String wishlistJson);
    Map<String, Object> addCourseToWishlist(String wishlistJson, Long courseId, String userEmail);
    Map<String, Object> removeWishlistDetail(String wishlistJson, String wishlistDetailId);
    Map<String, Object> removeWishlistDetailByCourseId(String wishlistJson, Long courseId);
    boolean toggleCourseInWishlist(String wishlistJson, Long courseId, String userEmail, Map<String, Object> wishlist);
}
