package com.OLearning.controller.home;

import com.OLearning.dto.cart.CartDTO;
import com.OLearning.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/add")
    public String addToCart(@RequestParam("userId") Long userId,
                            @RequestParam("courseId") Long courseId,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            // Verify authenticated user matches the provided userId
            if (authentication == null || !authentication.isAuthenticated()) {
                redirectAttributes.addFlashAttribute("error", "Please login to add courses to cart");
                return "redirect:/login";
            }
            // Assuming the principal contains userId as part of authentication
            Long authenticatedUserId = ((com.OLearning.entity.User) authentication.getPrincipal()).getUserId();
            if (!authenticatedUserId.equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access");
                return "redirect:/courses";
            }

            CartDTO cartDTO = cartService.addToCart(userId, courseId);
            redirectAttributes.addFlashAttribute("success", "Course added to cart successfully");
            return "redirect:/courses";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/courses";
        }
    }

    @GetMapping
    public String viewCart(@RequestParam("userId") Long userId,
                           Authentication authentication,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            // Verify authenticated user
            if (authentication == null || !authentication.isAuthenticated()) {
                redirectAttributes.addFlashAttribute("error", "Please login to view your cart");
                return "redirect:/login";
            }

            Long authenticatedUserId = ((com.OLearning.entity.User) authentication.getPrincipal()).getUserId();
            if (!authenticatedUserId.equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access");
                return "redirect:/courses";
            }

            CartDTO cartDTO = cartService.getCartByUserId(userId);
            model.addAttribute("cart", cartDTO);
            return "cart/view"; // Assumes you have a Thymeleaf template at templates/cart/view.html
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/courses";
        }
    }
}