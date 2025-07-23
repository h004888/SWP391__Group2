package com.OLearning.service.wishlist;

import java.util.Map;

public interface WishlistService {
    Map<String, Object> getWishlistDetails(String encodedWishlistJson, String userEmail);
    Map<String, Object> addCourseToWishlist(String encodedWishlistJson, Long courseId, String userEmail);
    Map<String, Object> removeCourseFromWishlist(String encodedWishlistJson, Long courseId, String userEmail);
    Map<String, Object> clearWishlist(String userEmail);
    boolean isCourseInWishlist(String encodedWishlistJson, Long courseId, String userEmail);
}
