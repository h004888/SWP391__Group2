package com.OLearning.service.wishlist.impl;

import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.OrderDetailRepository;
import com.OLearning.repository.OrdersRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.wishlist.WishlistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Custom exceptions for wishlist operations
    public static class CourseAlreadyInWishlistException extends RuntimeException {
        public CourseAlreadyInWishlistException(String message) {
            super(message);
        }
    }

    public static class CourseAlreadyPurchasedException extends RuntimeException {
        public CourseAlreadyPurchasedException(String message) {
            super(message);
        }
    }

    @Override
    public Map<String, Object> decode(String wishlistJson) {
        try {
            if (wishlistJson == null || wishlistJson.isEmpty()) {
                Map<String, Object> emptyWishlist = new HashMap<>();
                emptyWishlist.put("total", 0L);
                emptyWishlist.put("items", new ArrayList<>());
                return emptyWishlist;
            }
            Map<String, Object> wishlist = objectMapper.readValue(wishlistJson, Map.class);
            wishlist.computeIfAbsent("items", k -> new ArrayList<>());
            if (!wishlist.containsKey("total")) {
                wishlist.put("total", 0L);
            } else {
                Object total = wishlist.get("total");
                if (total instanceof Number) {
                    wishlist.put("total", ((Number) total).longValue());
                } else {
                    wishlist.put("total", 0L);
                }
            }
            return wishlist;
        } catch (Exception e) {
            Map<String, Object> emptyWishlist = new HashMap<>();
            emptyWishlist.put("total", 0L);
            emptyWishlist.put("items", new ArrayList<>());
            return emptyWishlist;
        }
    }

    @Override
    public Map<String, Object> addCourseToWishlist(String wishlistJson, Long courseId, String userEmail) {
        Map<String, Object> wishlist = decode(wishlistJson);
        List<Map<String, Object>> items = (List<Map<String, Object>>) wishlist.getOrDefault("items", new ArrayList<>());
        Object totalObj = wishlist.getOrDefault("total", 0L);
        Long total = totalObj instanceof Number ? ((Number) totalObj).longValue() : 0L;

        // Fetch course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

        // Check if course is already in wishlist
        boolean courseInWishlist = items.stream()
                .anyMatch(item -> {
                    Object itemCourseId = item.get("courseId");
                    return itemCourseId instanceof Number && courseId.equals(((Number) itemCourseId).longValue());
                });
        if (courseInWishlist) {
            throw new CourseAlreadyInWishlistException("Course '" + course.getTitle() + "' is already in your wishlist.");
        }

        // Check if course is already purchased
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));
        boolean coursePurchased = ordersRepository.findByUserAndStatus(user, "completed").stream()
                .flatMap(order -> orderDetailRepository.findByOrder(order).stream())
                .anyMatch(orderDetail -> orderDetail.getCourse().getCourseId().equals(courseId));
        if (coursePurchased) {
            throw new CourseAlreadyPurchasedException("Course '" + course.getTitle() + "' has already been purchased.");
        }

        // Add course to wishlist
        Map<String, Object> item = new HashMap<>();
        item.put("id", UUID.randomUUID().toString());
        item.put("courseId", courseId);
        item.put("price", course.getPrice());
        item.put("courseTitle", course.getTitle());
        items.add(item);
        total++;

        wishlist.put("items", items);
        wishlist.put("total", total);
        return wishlist;
    }

    @Override
    public Map<String, Object> removeWishlistDetail(String wishlistJson, String wishlistDetailId) {
        Map<String, Object> wishlist = decode(wishlistJson);
        List<Map<String, Object>> items = (List<Map<String, Object>>) wishlist.getOrDefault("items", new ArrayList<>());

        items = items.stream()
                .filter(item -> !wishlistDetailId.equals(item.get("id")))
                .collect(Collectors.toList());

        wishlist.put("items", items);
        wishlist.put("total", (long) items.size());
        return wishlist;
    }

    @Override
    public Map<String, Object> removeWishlistDetailByCourseId(String wishlistJson, Long courseId) {
        Map<String, Object> wishlist = decode(wishlistJson);
        List<Map<String, Object>> items = (List<Map<String, Object>>) wishlist.getOrDefault("items", new ArrayList<>());

        items = items.stream()
                .filter(item -> {
                    Object itemCourseId = item.get("courseId");
                    return !(itemCourseId instanceof Number && courseId.equals(((Number) itemCourseId).longValue()));
                })
                .collect(Collectors.toList());

        wishlist.put("items", items);
        wishlist.put("total", (long) items.size());
        return wishlist;
    }

    @Override
    public boolean toggleCourseInWishlist(String wishlistJson, Long courseId, String userEmail, Map<String, Object> wishlist) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) wishlist.getOrDefault("items", new ArrayList<>());
        Object totalObj = wishlist.getOrDefault("total", 0L);
        Long total = totalObj instanceof Number ? ((Number) totalObj).longValue() : 0L;

        // Check if course is in wishlist
        Optional<Map<String, Object>> existingItem = items.stream()
                .filter(item -> {
                    Object itemCourseId = item.get("courseId");
                    return itemCourseId instanceof Number && courseId.equals(((Number) itemCourseId).longValue());
                })
                .findFirst();

        if (existingItem.isPresent()) {
            // Remove course from wishlist
            items = items.stream()
                    .filter(item -> !existingItem.get().get("id").equals(item.get("id")))
                    .collect(Collectors.toList());
            total--;
            wishlist.put("items", items);
            wishlist.put("total", total);
            return false; // Indicates removal
        } else {
            // Add course to wishlist
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));
            boolean coursePurchased = ordersRepository.findByUserAndStatus(user, "completed").stream()
                    .flatMap(order -> orderDetailRepository.findByOrder(order).stream())
                    .anyMatch(orderDetail -> orderDetail.getCourse().getCourseId().equals(courseId));
            if (coursePurchased) {
                throw new CourseAlreadyPurchasedException("Course '" + course.getTitle() + "' has already been purchased.");
            }

            Map<String, Object> item = new HashMap<>();
            item.put("id", UUID.randomUUID().toString());
            item.put("courseId", courseId);
            item.put("price", course.getPrice());
            item.put("courseTitle", course.getTitle());
            items.add(item);
            total++;
            wishlist.put("items", items);
            wishlist.put("total", total);
            return true; // Indicates addition
        }
    }
}
