package com.OLearning.controller.homePage;

import com.OLearning.config.VNPayConfig;
import com.OLearning.entity.Order;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.OrdersRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.VoucherRepository;
import com.OLearning.service.cart.CartService;
import com.OLearning.service.cart.impl.CartServiceImpl;
import com.OLearning.service.vnpay.VNPayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private CartService cartService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    @GetMapping
    public String getCart(@AuthenticationPrincipal UserDetails userDetails,
                          HttpServletRequest request,
                          Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedCartJson = getCartCookie(request, userId);
        Map<String, Object> cart = cartService.getCartDetails(encodedCartJson, userDetails.getUsername());
        model.addAttribute("cartItems", cart.getOrDefault("items", List.of()));
        model.addAttribute("totalPrice", calculateTotalPrice(cart));
        model.addAttribute("cartTotal", getLongValue(cart.getOrDefault("total", 0L)));
        return "homePage/cart";
    }

    @GetMapping("/total")
    @ResponseBody
    public Map<String, Long> getCartTotal(HttpServletRequest request,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return Map.of("total", 0L);
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedCartJson = getCartCookie(request, userId);
        Map<String, Object> cart = cartService.getCartDetails(encodedCartJson, userDetails.getUsername());
        Map<String, Long> response = new HashMap<>();
        response.put("total", getLongValue(cart.getOrDefault("total", 0L)));
        return response;
    }

    @PostMapping("/add/{courseId}")
    public String addToCart(@PathVariable Long courseId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedCartJson = getCartCookie(request, userId);
        try {
            Map<String, Object> cart = cartService.addCourseToCart(encodedCartJson, courseId, userDetails.getUsername());
            updateCartCookie(cart, response, userId);
            redirectAttributes.addFlashAttribute("message", "Course added to cart successfully!");
        } catch (CartServiceImpl.CourseAlreadyPurchasedException | CartServiceImpl.CourseAlreadyInCartException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add course to cart");
        }
        return "redirect:/home/coursesGrid";
    }

    @GetMapping("/remove/{cartDetailId}")
    public String removeFromCart(@PathVariable String cartDetailId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedCartJson = getCartCookie(request, userId);
        try {
            Map<String, Object> cart = cartService.removeCartDetail(encodedCartJson, cartDetailId, userDetails.getUsername());
            updateCartCookie(cart, response, userId);
            redirectAttributes.addFlashAttribute("message", "Item removed from cart successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove item from cart");
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails,
                            HttpServletResponse response,
                            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        try {
            Map<String, Object> emptyCart = cartService.clearCart(userDetails.getUsername());
            updateCartCookie(emptyCart, response, userId);
            redirectAttributes.addFlashAttribute("message", "Cart cleared successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to clear cart");
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam(value = "voucherMapping", required = false) String voucherMappingJson,
                           @AuthenticationPrincipal UserDetails userDetails,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedCartJson = getCartCookie(request, userId);
        try {
            Map<String, Object> cart = cartService.getCartDetails(encodedCartJson, userDetails.getUsername());
            List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", List.of());
            Map<Long, Long> voucherMapping = new HashMap<>();
            if (voucherMappingJson != null && !voucherMappingJson.isEmpty()) {
                voucherMapping = objectMapper.readValue(voucherMappingJson, new com.fasterxml.jackson.core.type.TypeReference<Map<Long, Long>>() {});
            }
            double totalAmount = 0;
            for (Map<String, Object> item : items) {
                Long courseId = Long.valueOf(item.get("courseId").toString());
                double price = Double.valueOf(item.get("price").toString());
                if (voucherMapping.containsKey(courseId)) {
                    Long voucherId = voucherMapping.get(courseId);
                    double discount = voucherRepository.findById(voucherId).map(v -> v.getDiscount()).orElse(0.0);
                    price = Math.round(price * (1 - discount / 100.0));
                }
                totalAmount += price;
            }
            String ipAddr = request.getRemoteAddr();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            if (user.getCoin() >= totalAmount) {
                Order order = new Order();
                order.setUser(user);
                order.setAmount(totalAmount);
                cartService.processCheckout(encodedCartJson, ipAddr, userDetails.getUsername());
                cartService.completeCheckout(cart, order, true, null);
                updateCartCookie(cartService.clearCart(userDetails.getUsername()), response, userId);
                redirectAttributes.addFlashAttribute("message", "Checkout completed using wallet!");
                return "redirect:/cart";
            } else {
                request.setAttribute("amount", (int) (totalAmount)*100);
                request.setAttribute("cart", encodedCartJson);
                String currentPath = request.getRequestURI();
                String basePath = currentPath.substring(0, currentPath.lastIndexOf('/'));
                String returnUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(basePath)
                        .build()
                        .toUriString();
                request.setAttribute("urlReturn", returnUrl);
                return "redirect:" + vnPayService.createOrder(request);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Checkout error: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request,
                                   HttpServletResponse response,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedCartJson = getCartCookie(request, userId);
        int paymentStatus = vnPayService.orderReturn(request);
        if (paymentStatus == 1) {
            try {
                Map<String, Object> cart = cartService.getCartDetails(encodedCartJson, userDetails.getUsername());
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));
                String transactionId = request.getParameter("vnp_TransactionNo");
                String ipAddr = request.getRemoteAddr();
                double totalAmount = calculateTotalPrice(cart);
                Order order = new Order();
                order.setUser(user);
                order.setAmount(totalAmount);
                order.setRefCode(transactionId);
                cartService.processCheckout(encodedCartJson, ipAddr, userDetails.getUsername());
                cartService.completeCheckout(cart, order, false, transactionId);
                updateCartCookie(cartService.clearCart(userDetails.getUsername()), response, userId);
                redirectAttributes.addFlashAttribute("message", "VNPay payment successful!");
                return "redirect:/cart";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "VNPay success but internal error: " + e.getMessage());
                return "redirect:/cart";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "VNPay payment failed.");
            return "redirect:/cart";
        }
    }

    @PostMapping("/apply-voucher")
    @ResponseBody
    public Map<String, Object> applyVoucherToCourse(@RequestBody Map<String, Object> req) {
        Long userId = Long.valueOf(req.get("userId").toString());
        Long courseId = Long.valueOf(req.get("courseId").toString());
        Long voucherId = Long.valueOf(req.get("voucherId").toString());
        
        double originalPrice = courseRepository.findById(courseId)
            .map(course -> course.getPrice().doubleValue())
            .orElse(0.0);
        double discount = voucherRepository.findById(voucherId)
            .map(voucher -> voucher.getDiscount())
            .orElse(0.0);
        String voucherCode = voucherRepository.findById(voucherId)
            .map(voucher -> voucher.getCode())
            .orElse("");
        double discountedPrice = Math.round(originalPrice * (1 - discount / 100.0));
        Map<String, Object> result = new HashMap<>();
        result.put("voucherId", voucherId);
        result.put("voucherCode", voucherCode);
        result.put("discountedPrice", (long) discountedPrice);
        return result;
    }

    private double calculateTotalPrice(Map<String, Object> cart) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", List.of());
        return items.stream()
                .filter(Objects::nonNull)
                .mapToDouble(item -> {
                    Object price = item.get("price");
                    return price != null ? ((Number) price).doubleValue() : 0.0;
                })
                .sum();
    }

    private void updateCartCookie(Map<String, Object> cart, HttpServletResponse response, Long userId) throws Exception {
        String cartJson = objectMapper.writeValueAsString(cart);
        String encodedCartJson = Base64.getEncoder().encodeToString(cartJson.getBytes(StandardCharsets.UTF_8));
        Cookie cartCookie = new Cookie("cart_" + userId, encodedCartJson);
        cartCookie.setPath("/");
        cartCookie.setMaxAge(7 * 24 * 60 * 60);
        cartCookie.setHttpOnly(true);
        response.addCookie(cartCookie);
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return user.getUserId();
    }

    private String getCartCookie(HttpServletRequest request, Long userId) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("cart_" + userId)) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    private Long getLongValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalStateException("Value is not a number: " + (value != null ? value.getClass().getName() : "null"));
    }
}