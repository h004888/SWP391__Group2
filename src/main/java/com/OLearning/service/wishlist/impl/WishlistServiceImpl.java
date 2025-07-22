package com.OLearning.service.wishlist.impl;

import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.wishlist.WishlistService;
import com.OLearning.service.enrollment.EnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import com.OLearning.repository.LessonRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.CourseReview;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseReviewRepository courseReviewRepository;

    public static class CourseAlreadyInWishlistException extends RuntimeException {
        public CourseAlreadyInWishlistException(String message) {
            super(message);
        }
    }

    @Override
    public Map<String, Object> getWishlistDetails(String encodedWishlistJson, String userEmail) {
        try {
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                    .getUserId();

            String wishlistJson;
            if (encodedWishlistJson == null || encodedWishlistJson.trim().isEmpty()) {
                Map<String, Object> emptyWishlist = new HashMap<>();
                emptyWishlist.put("userId", userId);
                emptyWishlist.put("total", 0L);
                emptyWishlist.put("items", new ArrayList<>());
                return emptyWishlist;
            } else {
                byte[] decodedBytes = Base64.getDecoder().decode(encodedWishlistJson);
                wishlistJson = new String(decodedBytes, StandardCharsets.UTF_8);
            }
            Map<String, Object> wishlist = objectMapper.readValue(wishlistJson, Map.class);

            wishlist.computeIfAbsent("items", k -> new ArrayList<>());
            if (!wishlist.containsKey("total")) {
                wishlist.put("total", 0L);
            } else {
                wishlist.put("total", getLongValue(wishlist.get("total")));
            }
            Long wishlistUserId = getLongValue(wishlist.getOrDefault("userId", -1L));
            if (!userId.equals(wishlistUserId)) {
                Map<String, Object> emptyWishlist = new HashMap<>();
                emptyWishlist.put("userId", userId);
                emptyWishlist.put("total", 0L);
                emptyWishlist.put("items", new ArrayList<>());
                return emptyWishlist;
            }
            // Add enrolled status and extra info for each item
            List<Map<String, Object>> items = (List<Map<String, Object>>) wishlist.get("items");
            for (Map<String, Object> item : items) {
                Object courseIdObj = item.get("courseId");
                Long courseId = null;
                if (courseIdObj instanceof Number) {
                    courseId = ((Number) courseIdObj).longValue();
                }
                if (courseId != null) {
                    boolean enrolled = enrollmentService.hasEnrolled(userId, courseId);
                    item.put("enrolled", enrolled);
                    // Bổ sung các trường mới
                    Course course = courseRepository.findById(courseId).orElse(null);
                    if (course != null) {
                        item.put("courseLevel", course.getCourseLevel());
                        item.put("description", course.getDescription());
                        // Rating
                        List<CourseReview> reviews = course.getCourseReviews();
                        int ratingCount = 0;
                        double averageRating = 0;
                        if (reviews != null && !reviews.isEmpty()) {
                            ratingCount = (int) reviews.stream().filter(r -> r.getRating() != null).count();
                            averageRating = reviews.stream().filter(r -> r.getRating() != null).mapToInt(CourseReview::getRating).average().orElse(0);
                        }
                        item.put("ratingCount", ratingCount);
                        item.put("averageRating", averageRating);
                        // Duration & Lessons
                        List<Lesson> lessons = lessonRepository.findLessonsByCourseId(courseId);
                        int totalLessons = lessons != null ? lessons.size() : 0;
                        int duration = lessons != null ? lessons.stream().filter(l -> l.getDuration() != null).mapToInt(Lesson::getDuration).sum() : 0;
                        item.put("totalLessons", totalLessons);
                        item.put("duration", duration);
                    }
                } else {
                    item.put("enrolled", false);
                }
            }
            return wishlist;
        } catch (Exception e) {
            System.err.println("Error parsing wishlist JSON: " + e.getMessage());
            Map<String, Object> emptyWishlist = new HashMap<>();
            emptyWishlist.put("userId", userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                    .getUserId());
            emptyWishlist.put("total", 0L);
            emptyWishlist.put("items", new ArrayList<>());
            return emptyWishlist;
        }
    }

    @Override
    public Map<String, Object> addCourseToWishlist(String encodedWishlistJson, Long courseId, String userEmail) {
        Map<String, Object> wishlist = getWishlistDetails(encodedWishlistJson, userEmail);
        List<Map<String, Object>> items = (List<Map<String, Object>>) wishlist.getOrDefault("items", new ArrayList<>());
        Long total = getLongValue(wishlist.getOrDefault("total", 0L));
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                .getUserId();

        wishlist.put("userId", userId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

        boolean courseInWishlist = items.stream()
                .anyMatch(item -> {
                    Object itemCourseId = item.get("courseId");
                    return itemCourseId instanceof Number && courseId.equals(((Number) itemCourseId).longValue());
                });
        if (courseInWishlist) {
            throw new CourseAlreadyInWishlistException("Course '" + course.getTitle() + "' is already in your wishlist.");
        }

        Map<String, Object> item = new HashMap<>();
        item.put("id", UUID.randomUUID().toString());
        item.put("courseId", courseId);
        item.put("courseTitle", course.getTitle());
        item.put("courseImage", course.getCourseImg());
        item.put("coursePrice", course.getPrice());
        items.add(item);
        total++;

        wishlist.put("items", items);
        wishlist.put("total", total);
        return wishlist;
    }

    @Override
    public Map<String, Object> removeCourseFromWishlist(String encodedWishlistJson, Long courseId, String userEmail) {
        Map<String, Object> wishlist = getWishlistDetails(encodedWishlistJson, userEmail);
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
    public boolean isCourseInWishlist(String encodedWishlistJson, Long courseId, String userEmail) {
        Map<String, Object> wishlist = getWishlistDetails(encodedWishlistJson, userEmail);
        if (wishlist.get("items") instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) wishlist.get("items");
            return items.stream()
                    .anyMatch(item -> item.get("courseId") != null && ((Number) item.get("courseId")).longValue() == courseId);
        }
        return false;
    }

    @Override
    public Map<String, Object> clearWishlist(String userEmail) {
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                .getUserId();
        Map<String, Object> emptyWishlist = new HashMap<>();
        emptyWishlist.put("userId", userId);
        emptyWishlist.put("total", 0L);
        emptyWishlist.put("items", new ArrayList<>());
        return emptyWishlist;
    }

    private Long getLongValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalStateException("Value is not a number: " + (value != null ? value.getClass().getName() : "null"));
    }
}
