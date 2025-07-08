package com.OLearning.controller.homePage;

import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.wishlist.WishlistService;
import com.OLearning.service.wishlist.impl.WishlistServiceImpl;
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

import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/wishlist")
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
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedWishlistJson = getWishlistCookie(request, userId);
        Map<String, Object> wishlist = wishlistService.getWishlistDetails(encodedWishlistJson, userDetails.getUsername());
        
        List<Map<String, Object>> allItems = (List<Map<String, Object>>) wishlist.getOrDefault("items", List.of());
        Long totalItems = getLongValue(wishlist.getOrDefault("total", 0L));
        
        // Calculate pagination
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allItems.size());
        
        // Get items for current page
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
        if (userDetails == null) {
            return Map.of("total", 0L);
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedWishlistJson = getWishlistCookie(request, userId);
        Map<String, Object> wishlist = wishlistService.getWishlistDetails(encodedWishlistJson, userDetails.getUsername());
        Map<String, Long> response = new HashMap<>();
        response.put("total", getLongValue(wishlist.getOrDefault("total", 0L)));
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
            result.put("error", "Bạn cần đăng nhập!");
            return result;
        }
        
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedWishlistJson = getWishlistCookie(request, userId);
        
        try {
            boolean isInWishlist = wishlistService.isCourseInWishlist(encodedWishlistJson, courseId, userDetails.getUsername());
            
            if (isInWishlist) {
                // Remove from wishlist
                Map<String, Object> wishlist = wishlistService.removeCourseFromWishlist(encodedWishlistJson, courseId, userDetails.getUsername());
                updateWishlistCookie(wishlist, response, userId);
                result.put("success", true);
                result.put("action", "removed");
                result.put("message", "Course removed from wishlist");
            } else {
                // Add to wishlist
                Map<String, Object> wishlist = wishlistService.addCourseToWishlist(encodedWishlistJson, courseId, userDetails.getUsername());
                updateWishlistCookie(wishlist, response, userId);
                result.put("success", true);
                result.put("action", "added");
                result.put("message", "Course added to wishlist");
            }
            
            result.put("total", getLongValue(wishlistService.getWishlistDetails(getWishlistCookie(request, userId), userDetails.getUsername()).getOrDefault("total", 0L)));
            
        } catch (WishlistServiceImpl.CourseAlreadyInWishlistException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Failed to update wishlist");
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
        Long userId = getUserIdFromUserDetails(userDetails);
        try {
            Map<String, Object> emptyWishlist = wishlistService.clearWishlist(userDetails.getUsername());
            updateWishlistCookie(emptyWishlist, response, userId);
            model.addAttribute("message", "Wishlist cleared successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to clear wishlist");
        }
        return "redirect:/wishlist";
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
        
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedWishlistJson = getWishlistCookie(request, userId);
        boolean isInWishlist = wishlistService.isCourseInWishlist(encodedWishlistJson, courseId, userDetails.getUsername());
        
        result.put("inWishlist", isInWishlist);
        return result;
    }

    private void updateWishlistCookie(Map<String, Object> wishlist, HttpServletResponse response, Long userId) throws Exception {
        String wishlistJson = objectMapper.writeValueAsString(wishlist);
        String encodedWishlistJson = Base64.getEncoder().encodeToString(wishlistJson.getBytes(StandardCharsets.UTF_8));
        Cookie wishlistCookie = new Cookie("wishlist_" + userId, encodedWishlistJson);
        wishlistCookie.setPath("/");
        wishlistCookie.setMaxAge(30 * 24 * 60 * 60);
        wishlistCookie.setHttpOnly(true);
        response.addCookie(wishlistCookie);
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return user.getUserId();
    }

    private String getWishlistCookie(HttpServletRequest request, Long userId) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("wishlist_" + userId)) {
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
