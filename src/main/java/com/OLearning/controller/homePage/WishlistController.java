package com.OLearning.controller.homePage;

import com.OLearning.repository.UserRepository;
import com.OLearning.service.payment.CartServiceUtil;
import com.OLearning.service.wishlist.WishlistService;
import com.OLearning.service.wishlist.impl.WishlistServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;

@Controller
@RequestMapping("/home/wishlist")
public class WishlistController {
    
    @Autowired
    private WishlistService wishlistService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getWishlist(@AuthenticationPrincipal UserDetails userDetails,
                              HttpServletRequest request,
                              Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "4") int size) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = CartServiceUtil.getUserIdFromUserDetails(userDetails, userRepository);
        String encodedWishlistJson = CartServiceUtil.getWishlistCookie(request, userId);
        Map<String, Object> wishlist = wishlistService.getWishlistDetails(encodedWishlistJson, userDetails.getUsername());
        
        List<Map<String, Object>> allItems = (List<Map<String, Object>>) wishlist.getOrDefault("items", List.of());
        Long totalItems = CartServiceUtil.getLongValue(wishlist.getOrDefault("total", 0L));

        int totalPages = (int) Math.ceil((double) totalItems / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allItems.size());
        List<Map<String, Object>> pageItems = allItems.subList(startIndex, endIndex);
        
        model.addAttribute("wishlistItems", pageItems);
        model.addAttribute("wishlistTotal", totalItems);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("hasNext", page < totalPages - 1);
        model.addAttribute("hasPrevious", page > 0);
        model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
        model.addAttribute("fragmentContent", "homePage/fragments/wishlistContent :: wishlistContent");
        return "homePage/index";
    }

    @GetMapping("/total")
    @ResponseBody
    public Map<String, Long> getWishlistTotal(HttpServletRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Long> response = new HashMap<>();
        
        if (userDetails == null) {
            response.put("total", 0L);
            return response;
        }
        
        try {
            Long userId = CartServiceUtil.getUserIdFromUserDetails(userDetails, userRepository);
            String encodedWishlistJson = CartServiceUtil.getWishlistCookie(request, userId);
            Map<String, Object> wishlist = wishlistService.getWishlistDetails(encodedWishlistJson, userDetails.getUsername());
            response.put("total", CartServiceUtil.getLongValue(wishlist.getOrDefault("total", 0L)));
        } catch (Exception e) {
            response.put("total", 0L);
        }
        
        return response;
    }

    @PostMapping("/toggle/{courseId}")
    @ResponseBody
    public Map<String, Object> toggleWishlist(@PathVariable Long courseId,
                                              @AuthenticationPrincipal UserDetails userDetails,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            result.put("success", false);
            result.put("error", "You need to login!");
            return result;
        }
        
        Long userId = CartServiceUtil.getUserIdFromUserDetails(userDetails, userRepository);
        String encodedWishlistJson = CartServiceUtil.getWishlistCookie(request, userId);
        
        try {
            boolean isInWishlist = wishlistService.isCourseInWishlist(encodedWishlistJson, courseId, userDetails.getUsername());
            
            if (isInWishlist) {
                // Remove from wishlist
                Map<String, Object> wishlist = wishlistService.removeCourseFromWishlist(encodedWishlistJson, courseId, userDetails.getUsername());
                CartServiceUtil.updateWishlistCookie(wishlist, response, userId);
                result.put("success", true);
                result.put("action", "removed");
                result.put("message", "Course removed from wishlist");
            } else {
                Map<String, Object> wishlist = wishlistService.addCourseToWishlist(encodedWishlistJson, courseId, userDetails.getUsername());
                ObjectMapper objectMapper = new ObjectMapper();
                String wishlistJson = objectMapper.writeValueAsString(wishlist);
                String encodedWishlistJsonNew = Base64.getEncoder().encodeToString(wishlistJson.getBytes(StandardCharsets.UTF_8));
                
                if (encodedWishlistJsonNew.length() > 8000) {
                    result.put("success", false);
                    result.put("message", "Wishlist is too large. Please remove some items first.");
                    return result;
                }
                
                CartServiceUtil.updateWishlistCookie(wishlist, response, userId);
                result.put("success", true);
                result.put("action", "added");
                result.put("message", "Course added to wishlist");
            }
            
            result.put("total", CartServiceUtil.getLongValue(wishlistService.getWishlistDetails(CartServiceUtil.getWishlistCookie(request, userId), userDetails.getUsername()).getOrDefault("total", 0L)));
            
        } catch (WishlistServiceImpl.CourseAlreadyInWishlistException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Failed to update wishlist: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/clear")
    public String clearWishlist(@AuthenticationPrincipal UserDetails userDetails,
                                HttpServletResponse response,
                                Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = CartServiceUtil.getUserIdFromUserDetails(userDetails, userRepository);
        try {
            Map<String, Object> emptyWishlist = wishlistService.clearWishlist(userDetails.getUsername());
            CartServiceUtil.updateWishlistCookie(emptyWishlist, response, userId);
            model.addAttribute("message", "Wishlist cleared successfully!");
        } catch (Exception e) {
            model.addAttribute("message", "Failed to clear wishlist");
        }
        return "redirect:/home/wishlist";
    }

    @GetMapping("/check/{courseId}")
    @ResponseBody
    public Map<String, Object> checkWishlistStatus(@PathVariable Long courseId,
                                                   @AuthenticationPrincipal UserDetails userDetails,
                                                   HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            result.put("inWishlist", false);
            return result;
        }
        
        Long userId = CartServiceUtil.getUserIdFromUserDetails(userDetails, userRepository);
        String encodedWishlistJson = CartServiceUtil.getWishlistCookie(request, userId);
        boolean isInWishlist = wishlistService.isCourseInWishlist(encodedWishlistJson, courseId, userDetails.getUsername());
        
        result.put("inWishlist", isInWishlist);
        return result;
    }
}
