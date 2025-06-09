package com.OLearning.controller.home;

import com.OLearning.dto.cart.CartDTO;
import com.OLearning.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public String addToCart(@RequestParam Long userId, @RequestParam Long courseId, Model model) {
        try {
            CartDTO cartDTO = cartService.addToCart(userId, courseId);
            model.addAttribute("cart", cartDTO);
            model.addAttribute("successMessage", "Course added to cart successfully!");
            return "redirect:/cart/view?userId=" + userId; // Redirect to cart view page
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "cart/error"; // Render error page or fragment
        }
    }

    @GetMapping("/view")
    public String viewCart(@RequestParam Long userId, Model model) {
        try {
            CartDTO cartDTO = cartService.getCartByUserId(userId); // Assume this method exists in CartService
            model.addAttribute("accNamePage", "Your Cart");
            model.addAttribute("fragmentContent", "cart/fragments/cartContent :: cartContent");
            model.addAttribute("cart", cartDTO);
            return "cart/index"; // Render cart view page
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "cart/error"; // Render error page or fragment
        }
    }
}