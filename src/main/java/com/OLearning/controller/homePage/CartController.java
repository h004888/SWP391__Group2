package com.OLearning.controller.homePage;

import com.OLearning.entity.Orders;
import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.cart.CartService;
import com.OLearning.service.cart.impl.CartServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getCart(@AuthenticationPrincipal UserDetails userDetails,
                          @CookieValue(value = "cart", defaultValue = "") String encodedCartJson,
                          Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Map<String, Object> cart = decodeCartFromCookie(encodedCartJson);
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", List.of());
        double totalPrice = items.stream()
                .filter(Objects::nonNull)
                .mapToDouble(item -> {
                    Object price = item.get("price");
                    return price != null ? ((Number) price).doubleValue() : 0.0;
                })
                .sum();

        model.addAttribute("cartItems", items);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cartTotal", cart.getOrDefault("total", 0L));
        return "homePage/cart";
    }

    @GetMapping("/total")
    @ResponseBody
    public Map<String, Long> getCartTotal(@CookieValue(value = "cart", defaultValue = "") String encodedCartJson) {
        Map<String, Object> cart = decodeCartFromCookie(encodedCartJson);
        Map<String, Long> response = new HashMap<>();
        response.put("total", (Long) cart.getOrDefault("total", 0L));
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
            Map<String, Object> cart = decodeCartFromCookie(encodedCartJson);
            String cartJson = objectMapper.writeValueAsString(cart);
            cart = cartService.addCourseToCart(cartJson, courseId, userDetails.getUsername());
            updateCartCookie(cart, response);
            redirectAttributes.addFlashAttribute("message", "Course added to cart successfully!");
        } catch (CartServiceImpl.CourseAlreadyPurchasedException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (CartServiceImpl.CourseAlreadyInCartException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add course to cart: " + e.getMessage());
        }

        return "redirect:/courses";
    }


    @GetMapping("/remove/{cartDetailId}")
    public String removeFromCart(@PathVariable String cartDetailId,
                                 @CookieValue(value = "cart", defaultValue = "") String encodedCartJson,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        try {
            Map<String, Object> cart = decodeCartFromCookie(encodedCartJson);
            String cartJson = objectMapper.writeValueAsString(cart);
            cart = cartService.removeCartDetail(cartJson, cartDetailId);
            updateCartCookie(cart, response);
            redirectAttributes.addFlashAttribute("message", "Item removed from cart successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove item: " + e.getMessage());
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
            Map<String, Object> emptyCart = cartService.clearCart();
            updateCartCookie(emptyCart, response);
            redirectAttributes.addFlashAttribute("message", "Cart cleared successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to clear cart: " + e.getMessage());
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
            Map<String, Object> cart = decodeCartFromCookie(encodedCartJson);
            String cartJson = objectMapper.writeValueAsString(cart);
            String ipAddr = request.getRemoteAddr();
            Long userId = getUserIdFromUserDetails(userDetails);
            Orders order = new Orders();
            order.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found")));
            order.setAmount(((List<Map<String, Object>>) cart.getOrDefault("items", List.of())).stream()
                    .mapToDouble(item -> {
                        Object price = item.get("price");
                        return price != null ? ((Number) price).doubleValue() : 0.0;
                    })
                    .sum());
            cartService.processCheckout(cartJson, ipAddr);
            cartService.completeCheckout(cart, order, true);
            updateCartCookie(cartService.clearCart(), response);
            redirectAttributes.addFlashAttribute("message", "Checkout completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to process checkout: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    private void updateCartCookie(Map<String, Object> cart, HttpServletResponse response) throws Exception {
        String cartJson = objectMapper.writeValueAsString(cart);
        String encodedCartJson = Base64.getEncoder().encodeToString(cartJson.getBytes(StandardCharsets.UTF_8));

        Cookie cartCookie = new Cookie("cart", encodedCartJson);
        cartCookie.setPath("/");
        cartCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        cartCookie.setHttpOnly(true);
        response.addCookie(cartCookie);
    }

    private Map<String, Object> decodeCartFromCookie(String encodedCartJson) {
        try {
            if (encodedCartJson == null || encodedCartJson.isEmpty()) {
                Map<String, Object> emptyCart = new HashMap<>();
                emptyCart.put("total", 0L);
                emptyCart.put("items", new ArrayList<>());
                return emptyCart;
            }
            byte[] decodedBytes = Base64.getDecoder().decode(encodedCartJson);
            String decodedJson = new String(decodedBytes, StandardCharsets.UTF_8);
            return cartService.getCartDetails(decodedJson);
        } catch (Exception e) {
            Map<String, Object> emptyCart = new HashMap<>();
            emptyCart.put("total", 0L);
            emptyCart.put("items", new ArrayList<>());
            return emptyCart;
        }
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return user.getUserId();
    }
}