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
    public Map<String, Object> getCartDetails(String cartJson, String userEmail) {
        try {
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                    .getUserId();
            if (cartJson == null || cartJson.trim().isEmpty()) {
                Map<String, Object> emptyCart = new HashMap<>();
                emptyCart.put("userId", userId);
                emptyCart.put("total", 0L);
                emptyCart.put("items", new ArrayList<>());
                return emptyCart;
            }
            Map<String, Object> cart = objectMapper.readValue(cartJson, Map.class);

            cart.computeIfAbsent("items", k -> new ArrayList<>());
            if (!cart.containsKey("total")) {
                cart.put("total", 0L);
            } else {
                cart.put("total", getLongValue(cart.get("total")));
            }
            Long cartUserId = getLongValue(cart.getOrDefault("userId", -1L));
            if (!userId.equals(cartUserId)) {
                // If cart belongs to another user, return an empty cart for the current user
                Map<String, Object> emptyCart = new HashMap<>();
                emptyCart.put("userId", userId);
                emptyCart.put("total", 0L);
                emptyCart.put("items", new ArrayList<>());
                return emptyCart;
            }
            return cart;
        } catch (Exception e) {
            System.err.println("Error parsing cart JSON: " + e.getMessage());
            Map<String, Object> emptyCart = new HashMap<>();
            emptyCart.put("userId", userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                    .getUserId());
            emptyCart.put("total", 0L);
            emptyCart.put("items", new ArrayList<>());
            return emptyCart;
        }
    }

    @Override
    public Map<String, Object> addCourseToCart(String cartJson, Long courseId, String userEmail) {
        Map<String, Object> cart = getCartDetails(cartJson, userEmail);
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());
        Long total = getLongValue(cart.getOrDefault("total", 0L));
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                .getUserId();

        // Set userId in cart if not already set
        cart.put("userId", userId);

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

        // Check if course already purchased
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));
        List<Orders> completedOrders = ordersRepository.findByUserAndStatus(user, "completed");

        boolean coursePurchased = completedOrders.stream()
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
    public Map<String, Object> removeCartDetail(String cartJson, String cartDetailId, String userEmail) {
        Map<String, Object> cart = getCartDetails(cartJson, userEmail);
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());

        items = items.stream()
                .filter(item -> !cartDetailId.equals(item.get("id")))
                .collect(Collectors.toList());

        cart.put("items", items);
        cart.put("total", (long) items.size());
        return cart;
    }

    @Override
    public Map<String, Object> clearCart(String userEmail) {
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                .getUserId();
        Map<String, Object> emptyCart = new HashMap<>();
        emptyCart.put("userId", userId);
        emptyCart.put("total", 0L);
        emptyCart.put("items", new ArrayList<>());
        return emptyCart;
    }

    @Override
    public String processCheckout(String cartJson, String ipAddr, String userEmail) {
        Map<String, Object> cart = getCartDetails(cartJson, userEmail);
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }
        return null; // Handled in controller
    }

    @Override
    @Transactional
    public void completeCheckout(Map<String, Object> cart, Orders order, boolean useCoins, String refCode) {
        User user = userRepository.findById(getLongValue(order.getUser().getUserId()))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());

        BigDecimal totalPrice = items.stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    Object price = item.get("price");
                    return price != null ? BigDecimal.valueOf(((Number) price).doubleValue()) : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Set order details
        order.setOrderType("course_purchase");
        order.setStatus("completed");
        order.setOrderDate(LocalDateTime.now());
        order.setRefCode(refCode != null ? refCode : UUID.randomUUID().toString());
        order.setAmount(totalPrice.doubleValue());

        ordersRepository.save(order);

        // 1. VNPay payment: Add money to system first (coin maintenance transaction)
        if (!useCoins) {
            CoinTransaction transaction = new CoinTransaction();
            transaction.setUser(user); // System transaction
            transaction.setAmount(totalPrice);
            transaction.setStatus("completed");
            transaction.setTransactionType("top_up");
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setNote("VNPay payment received - Order: " + order.getRefCode());
            coinTransactionRepository.save(transaction);
            user.setCoin(user.getCoin() + totalPrice.longValue());
        }

        // Process each item in the cart
        for (Map<String, Object> item : items) {
            Object courseIdObj = item.get("courseId");
            Long courseId = getLongValue(courseIdObj);
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

            // Create order detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setCourse(course);
            orderDetail.setUnitPrice(((Number) item.get("price")).doubleValue());
            orderDetailRepository.save(orderDetail);

            // Create enrollment
            Enrollment enrollment = new Enrollment();
            enrollment.setUser(user);
            enrollment.setCourse(course);
            enrollment.setEnrollmentDate(new Date());
            enrollment.setProgress(BigDecimal.ZERO);
            enrollment.setStatus("onGoing");
            enrollment.setOrder(order);
            enrollmentRepository.save(enrollment);

            BigDecimal coursePrice = BigDecimal.valueOf(((Number) item.get("price")).doubleValue());

            // 2. Deduct money from user (for both payment methods)
            CoinTransaction studentTransaction = new CoinTransaction();
            studentTransaction.setUser(user);
            studentTransaction.setAmount(coursePrice.negate());
            studentTransaction.setStatus("completed");
            studentTransaction.setCreatedAt(LocalDateTime.now());

            if (useCoins) {
                studentTransaction.setTransactionType("course_purchase");
                studentTransaction.setNote("Purchase course with coins: " + course.getTitle());
            } else {
                studentTransaction.setTransactionType("course_purchase");
                studentTransaction.setNote("Purchase course via VNPay: " + course.getTitle() + " - Order: " + order.getRefCode());
            }

            coinTransactionRepository.save(studentTransaction);

            // Deduct coins from user
            user.setCoin(user.getCoin() - coursePrice.longValue());

            // 3. Add money to instructor (for both payment methods)
            User instructor = course.getInstructor();
            if (instructor != null) {
                CoinTransaction instructorTransaction = new CoinTransaction();
                instructorTransaction.setUser(instructor);
                instructorTransaction.setAmount(coursePrice);
                instructorTransaction.setStatus("completed");
                instructorTransaction.setCreatedAt(LocalDateTime.now());

                if (useCoins) {
                    instructorTransaction.setTransactionType("course_purchase");
                    instructorTransaction.setNote("Earned from coin purchase: " + course.getTitle());
                } else {
                    instructorTransaction.setTransactionType("course_purchase");
                    instructorTransaction.setNote("Earned from VNPay purchase: " + course.getTitle() + " - Order: " + order.getRefCode());
                }

                coinTransactionRepository.save(instructorTransaction);

                // Add coins to instructor
                instructor.setCoin(instructor.getCoin() + coursePrice.longValue());
                userRepository.save(instructor);
            }
        }

        // Save user changes (coin deduction)
        userRepository.save(user);
    }

    private Long getLongValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalStateException("Value is not a number: " + (value != null ? value.getClass().getName() : "null"));
    }
}