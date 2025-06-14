package com.OLearning.service.cart.impl;

import com.OLearning.entity.*;
import com.OLearning.repository.*;
import com.OLearning.service.cart.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CoinTransactionRepository coinTransactionRepository;

    // Custom exceptions for cart operations
    public static class CourseAlreadyPurchasedException extends RuntimeException {
        public CourseAlreadyPurchasedException(String message) {
            super(message);
        }
    }

    public static class CourseAlreadyInCartException extends RuntimeException {
        public CourseAlreadyInCartException(String message) {
            super(message);
        }
    }

    @Override
    public Map<String, Object> getCartDetails(String cartJson) {
        try {
            if (cartJson == null || cartJson.isEmpty()) {
                Map<String, Object> emptyCart = new HashMap<>();
                emptyCart.put("total", 0L);
                emptyCart.put("items", new ArrayList<>());
                return emptyCart;
            }
            Map<String, Object> cart = objectMapper.readValue(cartJson, Map.class);
            cart.computeIfAbsent("items", k -> new ArrayList<>());
            if (!cart.containsKey("total")) {
                cart.put("total", 0L);
            } else {
                Object total = cart.get("total");
                if (total instanceof Number) {
                    cart.put("total", ((Number) total).longValue());
                } else {
                    cart.put("total", 0L);
                }
            }
            return cart;
        } catch (Exception e) {
            Map<String, Object> emptyCart = new HashMap<>();
            emptyCart.put("total", 0L);
            emptyCart.put("items", new ArrayList<>());
            return emptyCart;
        }
    }

    @Override
    public Map<String, Object> addCourseToCart(String cartJson, Long courseId, String userEmail) {
        Map<String, Object> cart = getCartDetails(cartJson);
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());
        Object totalObj = cart.getOrDefault("total", 0L);
        Long total = totalObj instanceof Number ? ((Number) totalObj).longValue() : 0L;

        // Fetch course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

        // Check if course is already in cart
        boolean courseInCart = items.stream()
                .anyMatch(item -> {
                    Object itemCourseId = item.get("courseId");
                    return itemCourseId instanceof Number && courseId.equals(((Number) itemCourseId).longValue());
                });
        if (courseInCart) {
            throw new CourseAlreadyInCartException("Course '" + course.getTitle() + "' is already in your cart.");
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

        // Add course to cart
        Map<String, Object> item = new HashMap<>();
        item.put("id", UUID.randomUUID().toString());
        item.put("courseId", courseId);
        item.put("price", course.getPrice());
        item.put("courseTitle", course.getTitle());
        items.add(item);
        total++;

        cart.put("items", items);
        cart.put("total", total);
        return cart;
    }

    @Override
    public Map<String, Object> removeCartDetail(String cartJson, String cartDetailId) {
        Map<String, Object> cart = getCartDetails(cartJson);
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());

        items = items.stream()
                .filter(item -> !cartDetailId.equals(item.get("id")))
                .collect(Collectors.toList());

        cart.put("items", items);
        cart.put("total", (long) items.size());
        return cart;
    }

    @Override
    public Map<String, Object> clearCart() {
        Map<String, Object> emptyCart = new HashMap<>();
        emptyCart.put("total", 0L);
        emptyCart.put("items", new ArrayList<>());
        return emptyCart;
    }

    @Override
    @Transactional
    public String processCheckout(String cartJson, String ipAddr) {
        Map<String, Object> cart = getCartDetails(cartJson);
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }
        return null; // Handled in controller
    }

    @Transactional
    public void completeCheckout(Map<String, Object> cart, Orders order, boolean useCoins) {
        User user = userRepository.findById(order.getUser().getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());
        double totalPrice = items.stream()
                .filter(Objects::nonNull)
                .mapToDouble(item -> {
                    Object price = item.get("price");
                    return price != null ? ((Number) price).doubleValue() : 0.0;
                })
                .sum();
        order.setOrderType("course_purchase");
        order.setStatus("completed");
        order.setOrderDate(LocalDateTime.now());
        order.setNote(null);
        order.setAmount(totalPrice);
        ordersRepository.save(order);

        for (Map<String, Object> item : items) {
            Object courseIdObj = item.get("courseId");
            Long courseId = courseIdObj instanceof Number ? ((Number) courseIdObj).longValue() : 0L;
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setCourse(course);
            orderDetail.setUnitPrice(((Number) item.get("price")).doubleValue());
            orderDetailRepository.save(orderDetail);

            Enrollment enrollment = new Enrollment();
            enrollment.setUser(user);
            enrollment.setCourse(course);
            enrollment.setEnrollmentDate(new Date());
            enrollment.setProgress(BigDecimal.ZERO);
            enrollment.setStatus("completed");
            enrollment.setOrder(order);
            enrollmentRepository.save(enrollment);

            BigDecimal coursePrice = BigDecimal.valueOf(((Number) item.get("price")).doubleValue());

            if (useCoins) {
                CoinTransaction studentTx = new CoinTransaction();
                studentTx.setUser(user);
                studentTx.setAmount(coursePrice.negate());
                studentTx.setTransactionType("course_purchase");
                studentTx.setStatus("completed");
                studentTx.setRefCode("ORDER_" + order.getOrderId());
                studentTx.setNote("Purchase course ID: " + course.getCourseId());
                studentTx.setCreatedAt(LocalDateTime.now());
                coinTransactionRepository.save(studentTx);

                user.setCoin(user.getCoin() - coursePrice.longValue());
                userRepository.save(user);
            }

            User instructor = course.getInstructor();
            if (instructor != null) {
                CoinTransaction instructorTx = new CoinTransaction();
                instructorTx.setUser(instructor);
                instructorTx.setAmount(coursePrice);
                instructorTx.setTransactionType("course_purchase");
                instructorTx.setStatus("completed");
                instructorTx.setRefCode("ORDER_" + order.getOrderId());
                instructorTx.setNote("Earned from course ID: " + course.getCourseId());
                instructorTx.setCreatedAt(LocalDateTime.now());
                coinTransactionRepository.save(instructorTx);

                instructor.setCoin(instructor.getCoin() + coursePrice.longValue());
                userRepository.save(instructor);
            }
        }
    }
}