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
import java.util.Base64;
import java.nio.charset.StandardCharsets;

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
    public Map<String, Object> getCartDetails(String encodedCartJson, String userEmail) {
        try {
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                    .getUserId();

            String cartJson;
            if (encodedCartJson == null || encodedCartJson.trim().isEmpty()) {
                Map<String, Object> emptyCart = new HashMap<>();
                emptyCart.put("userId", userId);
                emptyCart.put("total", 0L);
                emptyCart.put("items", new ArrayList<>());
                return emptyCart;
            } else {
                byte[] decodedBytes = Base64.getDecoder().decode(encodedCartJson);
                cartJson = new String(decodedBytes, StandardCharsets.UTF_8);
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
    public Map<String, Object> addCourseToCart(String encodedCartJson, Long courseId, String userEmail) {
        Map<String, Object> cart = getCartDetails(encodedCartJson, userEmail);
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());
        Long total = getLongValue(cart.getOrDefault("total", 0L));
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                .getUserId();

        cart.put("userId", userId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

        boolean courseInCart = items.stream()
                .anyMatch(item -> {
                    Object itemCourseId = item.get("courseId");
                    return itemCourseId instanceof Number && courseId.equals(((Number) itemCourseId).longValue());
                });
        if (courseInCart) {
            throw new CourseAlreadyInCartException("Course '" + course.getTitle() + "' is already in your cart.");
        }

        checkCoursePurchaseStatus(userEmail, courseId, course.getTitle());

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
    public Map<String, Object> removeCartDetail(String encodedCartJson, String cartDetailId, String userEmail) {
        Map<String, Object> cart = getCartDetails(encodedCartJson, userEmail);
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());

        items = items.stream()
                .filter(item -> !cartDetailId.equals(item.get("id")))
                .collect(Collectors.toList());

        cart.put("items", items);
        cart.put("total", (long) items.size());
        return cart;
    }

    @Override
    public boolean isCourseInCart(String encodedCartJson, Long courseId, String userEmail) {
        Map<String, Object> cart = getCartDetails(encodedCartJson, userEmail);
        if (cart.get("items") instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) cart.get("items");
            return items.stream()
                    .anyMatch(item -> item.get("courseId") != null && ((Number) item.get("courseId")).longValue() == courseId);
        }
        return false;
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
    public String processCheckout(String encodedCartJson, String ipAddr, String userEmail) {
        Map<String, Object> cart = getCartDetails(encodedCartJson, userEmail);
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", new ArrayList<>());
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        for (Map<String, Object> item : items) {
            Object courseIdObj = item.get("courseId");
            Long courseId = getLongValue(courseIdObj);
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));
            checkCoursePurchaseStatus(userEmail, courseId, course.getTitle());
        }

        return null;
    }

    @Override
    @Transactional
    public void completeCheckout(Map<String, Object> cart, Order order, boolean useCoins, String refCode) {
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

        order.setOrderType("course_purchase");
        order.setStatus("completed");
        order.setOrderDate(LocalDateTime.now());
        order.setRefCode(refCode != null ? refCode : UUID.randomUUID().toString());
        order.setAmount(totalPrice.doubleValue());

        ordersRepository.save(order);

        if (!useCoins) {
            CoinTransaction transaction = new CoinTransaction();
            transaction.setUser(user);
            transaction.setAmount(totalPrice);
            transaction.setStatus("completed");
            transaction.setTransactionType("top_up");
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setNote("VNPay payment received - Order: " + order.getRefCode());
            coinTransactionRepository.save(transaction);
            user.setCoin(user.getCoin() + totalPrice.longValue());
        }

        for (Map<String, Object> item : items) {
            Object courseIdObj = item.get("courseId");
            Long courseId = getLongValue(courseIdObj);
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
            enrollment.setProgress(BigDecimal.ZERO.doubleValue());
            enrollment.setStatus("onGoing");
            enrollment.setOrder(order);
            enrollmentRepository.save(enrollment);

            BigDecimal coursePrice = BigDecimal.valueOf(((Number) item.get("price")).doubleValue());

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

            user.setCoin(user.getCoin() - coursePrice.longValue());

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

                instructor.setCoin(instructor.getCoin() + coursePrice.longValue());
                userRepository.save(instructor);
            }
        }

        userRepository.save(user);
    }

    public void checkCoursePurchaseStatus(String userEmail, Long courseId, String courseTitle) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));
        List<Order> completedOrders = ordersRepository.findByUserAndStatus(user, "completed");

        boolean coursePurchased = completedOrders.stream()
                .flatMap(order -> orderDetailRepository.findByOrder(order).stream())
                .anyMatch(orderDetail -> orderDetail.getCourse().getCourseId().equals(courseId));
        if (coursePurchased) {
            throw new CourseAlreadyPurchasedException("Course '" + courseTitle + "' has already been purchased.");
        }
    }

    private Long getLongValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalStateException("Value is not a number: " + (value != null ? value.getClass().getName() : "null"));
    }
}
