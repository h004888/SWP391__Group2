package com.OLearning.controller.homePage;

import com.OLearning.config.VNPayConfig;
import com.OLearning.entity.Orders;
import com.OLearning.entity.User;
import com.OLearning.repository.OrdersRepository;
import com.OLearning.repository.UserRepository;
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
    private OrdersRepository ordersRepository;

    @GetMapping
    public String getCart(@AuthenticationPrincipal UserDetails userDetails,
                          @CookieValue(value = "cart", defaultValue = "") String encodedCartJson,
                          Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Map<String, Object> cart = decodeCartFromCookie(encodedCartJson, userDetails.getUsername());

        model.addAttribute("cartItems", cart.getOrDefault("items", List.of()));
        model.addAttribute("totalPrice", calculateTotalPrice(cart));
        model.addAttribute("cartTotal", getLongValue(cart.getOrDefault("total", 0L)));
        return "homePage/cart";
    }

    @GetMapping("/total")
    @ResponseBody
    public Map<String, Long> getCartTotal(@CookieValue(value = "cart", defaultValue = "") String encodedCartJson,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return Map.of("total", 0L);
        }
        Map<String, Object> cart = decodeCartFromCookie(encodedCartJson, userDetails.getUsername());
        Map<String, Long> response = new HashMap<>();
        response.put("total", getLongValue(cart.getOrDefault("total", 0L)));
        return response;
    }

    @PostMapping("/add/{courseId}")
    public String addToCart(@PathVariable Long courseId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            @CookieValue(value = "cart", defaultValue = "") String encodedCartJson,
                            HttpServletResponse response,
                            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> cart = decodeCartFromCookie(encodedCartJson, userDetails.getUsername());
            String cartJson = objectMapper.writeValueAsString(cart);
            cart = cartService.addCourseToCart(cartJson, courseId, userDetails.getUsername());
            updateCartCookie(cart, response);
            redirectAttributes.addFlashAttribute("message", "Course added to cart successfully!");
        } catch (CartServiceImpl.CourseAlreadyPurchasedException | CartServiceImpl.CourseAlreadyInCartException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add course to cart");
        }

        return "redirect:/courses";
    }

    @GetMapping("/remove/{cartDetailId}")
    public String removeFromCart(@PathVariable String cartDetailId,
                                 @CookieValue(value = "cart", defaultValue = "") String encodedCartJson,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> cart = decodeCartFromCookie(encodedCartJson, userDetails.getUsername());
            String cartJson = objectMapper.writeValueAsString(cart);
            cart = cartService.removeCartDetail(cartJson, cartDetailId, userDetails.getUsername());
            updateCartCookie(cart, response);
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

        try {
            Map<String, Object> emptyCart = cartService.clearCart(userDetails.getUsername());
            updateCartCookie(emptyCart, response);
            redirectAttributes.addFlashAttribute("message", "Cart cleared successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to clear cart");
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails,
                           @CookieValue(value = "cart", defaultValue = "") String encodedCartJson,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> cart = decodeCartFromCookie(encodedCartJson, userDetails.getUsername());
            String cartJson = objectMapper.writeValueAsString(cart);
            String ipAddr = request.getRemoteAddr();
            Long userId = getUserIdFromUserDetails(userDetails);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            double totalAmount = calculateTotalPrice(cart);

            if (user.getCoin() >= totalAmount) {
                Orders order = new Orders();
                order.setUser(user);
                order.setAmount(totalAmount);
                cartService.processCheckout(cartJson, ipAddr, userDetails.getUsername());
                cartService.completeCheckout(cart, order, true, null);
                updateCartCookie(cartService.clearCart(userDetails.getUsername()), response);
                redirectAttributes.addFlashAttribute("message", "Checkout completed using wallet!");
                return "redirect:/cart";
            } else {
                String txnRef = VNPayConfig.getRandomNumber(8);
                request.setAttribute("amount", (int) (totalAmount * 100));
                request.setAttribute("txnRef", txnRef);
                request.setAttribute("cart", cartJson);
                return "redirect:" + vnPayService.createOrder(request);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Checkout error: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request,
                                   @CookieValue(value = "cart", defaultValue = "") String encodedCartJson,
                                   HttpServletResponse response,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        int paymentStatus = vnPayService.orderReturn(request);

        if (paymentStatus == 1) {
            try {
                Map<String, Object> cart = decodeCartFromCookie(encodedCartJson, userDetails.getUsername());
                String cartJson = objectMapper.writeValueAsString(cart);
                Long userId = getUserIdFromUserDetails(userDetails);
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));

                String transactionId = request.getParameter("vnp_TransactionNo");
                String ipAddr = request.getRemoteAddr();
                double totalAmount = calculateTotalPrice(cart);

                Orders order = new Orders();
                order.setUser(user);
                order.setAmount(totalAmount);
                order.setRefCode(transactionId);
                cartService.processCheckout(cartJson, ipAddr, userDetails.getUsername());
                cartService.completeCheckout(cart, order, false, transactionId);
                updateCartCookie(cartService.clearCart(userDetails.getUsername()), response);

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

    private void updateCartCookie(Map<String, Object> cart, HttpServletResponse response) throws Exception {
        String cartJson = objectMapper.writeValueAsString(cart);
        String encodedCartJson = Base64.getEncoder().encodeToString(cartJson.getBytes(StandardCharsets.UTF_8));

        Cookie cartCookie = new Cookie("cart", encodedCartJson);
        cartCookie.setPath("/");
        cartCookie.setMaxAge(7 * 24 * 60 * 60);
        cartCookie.setHttpOnly(true);
        response.addCookie(cartCookie);
    }

    private Map<String, Object> decodeCartFromCookie(String encodedCartJson, String userEmail) {
        try {
            if (encodedCartJson == null || encodedCartJson.isEmpty()) {
                Map<String, Object> emptyCart = new HashMap<>();
                Long userId = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                        .getUserId();
                emptyCart.put("userId", userId);
                emptyCart.put("total", 0L);
                emptyCart.put("items", new ArrayList<>());
                return emptyCart;
            }
            byte[] decodedBytes = Base64.getDecoder().decode(encodedCartJson);
            String decodedJson = new String(decodedBytes, StandardCharsets.UTF_8);
            Map<String, Object> cart = cartService.getCartDetails(decodedJson, userEmail);
            return cart;
        } catch (Exception e) {
            Map<String, Object> emptyCart = new HashMap<>();
            Long userId = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail))
                    .getUserId();
            emptyCart.put("userId", userId);
            emptyCart.put("total", 0L);
            emptyCart.put("items", new ArrayList<>());
            return emptyCart;
        }
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        Object userId = user.getUserId();
        return getLongValue(userId);
    }

    private Long getLongValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        throw new IllegalStateException("Value is not a number: " + (value != null ? value.getClass().getName() : "null"));
    }
}