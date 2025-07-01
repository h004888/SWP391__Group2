package com.OLearning.controller.homePage;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.OLearning.dto.category.CategoryDTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Order;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.cart.CartService;
import com.OLearning.service.cart.impl.CartServiceImpl;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.course.CourseService;

import com.OLearning.service.vnpay.VNPayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.repository.VoucherRepository;
import com.OLearning.service.voucher.VoucherService;
import com.OLearning.service.wishlist.WishlistService;

@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CartService cartService;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartServiceImpl cartServiceImpl;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private WishlistService wishlistService;

    @GetMapping()
    public String getMethodName(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        // chia làm 2 danh sách:
        List<CategoryDTO> firstFive = categoryService.getAllCategory().stream().limit(5).toList();
        List<CategoryDTO> nextFive = categoryService.getAllCategory().stream().skip(5).limit(5).toList();
        List<Course> topCourses = courseService.getTopCourses().stream().limit(5).collect(Collectors.toList());
        model.addAttribute("topCourses", topCourses);
        model.addAttribute("topCategories", categoryService.findTop5ByOrderByIdAsc());
        model.addAttribute("firstFive", firstFive);
        model.addAttribute("nextFive", nextFive);
        
        // Add wishlist total
        if (userDetails != null) {
            Long userId = getUserIdFromUserDetails(userDetails);
            String encodedWishlistJson = getWishlistCookie(request, userId);
            Map<String, Object> wishlist = wishlistService.getWishlistDetails(encodedWishlistJson, userDetails.getUsername());
            model.addAttribute("wishlistTotal", getLongValue(wishlist.getOrDefault("total", 0L)));
        } else {
            model.addAttribute("wishlistTotal", 0L);
        }
        
        // Thêm map courseId -> isEnrolled
        Map<Long, Boolean> topCoursesEnrolledMap = new HashMap<>();
        if (userDetails != null) {
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                for (Course c : topCourses) {
                    boolean enrolled = enrollmentService.hasEnrolled(user.getUserId(), c.getCourseId());
                    topCoursesEnrolledMap.put(c.getCourseId(), enrolled);
                }
            }
        }
        model.addAttribute("topCoursesEnrolledMap", topCoursesEnrolledMap);
        return "homePage/index";
    }

    @GetMapping("/coursesGrid")
    public String coursesGrid(Model model, @RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String keyword,
                            @RequestParam(required = false) List<Long> categoryIds,
                            @RequestParam(required = false) List<String> priceFilters,
                            @RequestParam(required = false) List<String> levels,
                            @RequestParam(defaultValue = "Newest") String sortBy,
                            @RequestParam(defaultValue = "9") int size,
                            @AuthenticationPrincipal UserDetails userDetails,
                            HttpServletRequest request) {
        Page<CourseDTO> courses = courseService.searchCoursesGrid(categoryIds, priceFilters, levels, sortBy,
                        keyword,
                        page, size); // lưu ý trả về Page<CourseDTO>
        model.addAttribute("categories", categoryService.getListCategories());
        model.addAttribute("courses", courses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        model.addAttribute("totalItems", courses.getTotalElements());
        model.addAttribute("categoryIds", categoryIds);
        
        // Add wishlist total
        if (userDetails != null) {
            Long userId = getUserIdFromUserDetails(userDetails);
            String encodedWishlistJson = getWishlistCookie(request, userId);
            Map<String, Object> wishlist = wishlistService.getWishlistDetails(encodedWishlistJson, userDetails.getUsername());
            model.addAttribute("wishlistTotal", getLongValue(wishlist.getOrDefault("total", 0L)));
        } else {
            model.addAttribute("wishlistTotal", 0L);
        }
        
        return "homePage/course-grid";
    }

    @GetMapping("/course-detail")
    public String courseDetail(@RequestParam("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        Course course = courseService.getCourseById(id);

        int totalStudents = course.getInstructor().getCourses()
                        .stream()
                        .mapToInt(c -> c.getEnrollments().size())
                        .sum();
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("courseByInstructor",
                        course.getInstructor().getCourses().stream().limit(2).collect(Collectors.toList()));
        model.addAttribute("courseByCategory",
                        course.getCategory().getCourses().stream().limit(5).collect(Collectors.toList()));
        model.addAttribute("course", course);

        // Add wishlist total
        if (userDetails != null) {
            Long userId = getUserIdFromUserDetails(userDetails);
            String encodedWishlistJson = getWishlistCookie(request, userId);
            Map<String, Object> wishlist = wishlistService.getWishlistDetails(encodedWishlistJson, userDetails.getUsername());
            model.addAttribute("wishlistTotal", getLongValue(wishlist.getOrDefault("total", 0L)));
        } else {
            model.addAttribute("wishlistTotal", 0L);
        }

        boolean isEnrolled = false;
        if (userDetails != null) {
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                isEnrolled = enrollmentService.hasEnrolled(user.getUserId(), id);
            }
        }
        model.addAttribute("isEnrolled", isEnrolled);

        return "homePage/course-detail";
    }

    @PostMapping("/buy-now")
    public String buyNow(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam("courseId") Long courseId,
                         @RequestParam(value = "voucherId", required = false) Long voucherId,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String mainCartEncoded = getCartCookie(request, getUserIdFromUserDetails(userDetails));
        if (cartService.isCourseInCart(mainCartEncoded, courseId, userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("info", "This course is already in your cart. Please proceed to checkout from your cart.");
            return "redirect:/home/course-detail?id=" + courseId;
        }

        try {
            Long userId = getUserIdFromUserDetails(userDetails);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

            cartServiceImpl.checkCoursePurchaseStatus(userDetails.getUsername(), courseId, course.getTitle());

            Map<String, Object> cart = new HashMap<>();
            cart.put("userId", userId);
            List<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("id", UUID.randomUUID().toString());
            item.put("courseId", courseId);
            item.put("courseTitle", course.getTitle());
            double price = course.getPrice().doubleValue();
            if (voucherId != null) {
                var voucherOpt = voucherRepository.findById(voucherId);
                if (voucherOpt.isPresent()) {
                    double discount = voucherOpt.get().getDiscount() != null ? voucherOpt.get().getDiscount() : 0.0;
                    price = Math.round(price * (1 - discount / 100.0));
                    item.put("appliedVoucherId", voucherId);
                }
            }
            item.put("price", price);
            items.add(item);
            cart.put("items", items);
            cart.put("total", 1L);

            String cartJson = objectMapper.writeValueAsString(cart);
            double totalAmount = price;

            if (user.getCoin() >= totalAmount) {
                Order order = new Order();
                order.setUser(user);
                order.setAmount(totalAmount);
                cartService.processCheckout(cartJson, request.getRemoteAddr(), userDetails.getUsername());
                cartService.completeCheckout(cart, order, true, null);
                // Update voucher usage if applied
                if (voucherId != null) {
                    voucherService.useVoucherForUserAndCourse(voucherId, userId);
                }
                redirectAttributes.addFlashAttribute("message", "Purchase completed using wallet!");
                return "redirect:/home/course-detail?id=" + courseId;
            } else {
                String encodedCartJson = Base64.getEncoder().encodeToString(cartJson.getBytes(StandardCharsets.UTF_8));
                Cookie buyNowCookie = new Cookie("buy_now", encodedCartJson);
                buyNowCookie.setPath("/");
                buyNowCookie.setMaxAge(10 * 60);
                buyNowCookie.setHttpOnly(true);
                response.addCookie(buyNowCookie);
                request.setAttribute("amount", (int) (totalAmount * 100));
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
            redirectAttributes.addFlashAttribute("error", "Purchase error: " + e.getMessage());
            return "redirect:/home/course-detail?id=" + courseId;
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
        String encodedBuyNowJson = getBuyNowCookie(request);
        String encodedCartJson = getCartCookie(request, userId);
        Long courseId = null;
        Map<String, Object> cart = null;
        try {
            if (encodedBuyNowJson != null) {
                byte[] decodedBytes = Base64.getDecoder().decode(encodedBuyNowJson);
                String cartJson = new String(decodedBytes, StandardCharsets.UTF_8);
                cart = objectMapper.readValue(cartJson, Map.class);
            } else if (encodedCartJson != null && !encodedCartJson.isEmpty()) {
                cart = cartService.getCartDetails(encodedCartJson, userDetails.getUsername());
            }
            if (cart != null) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) cart.get("items");
                if (items != null && !items.isEmpty()) {
                    courseId = Long.valueOf(items.get(0).get("courseId").toString());
                }
            }
        } catch (Exception ignore) {}

        int paymentStatus = vnPayService.orderReturn(request);

        if (paymentStatus == 1) {
            try {
                String transactionId = request.getParameter("vnp_TransactionNo");
                String ipAddr = request.getRemoteAddr();
                String amount = request.getParameter("vnp_Amount");
                double totalAmount = Double.parseDouble(amount) / 100;

                Order order = new Order();
                order.setUser(userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found")));
                order.setAmount(totalAmount);
                order.setRefCode(transactionId);

                if (encodedBuyNowJson != null) {
                    clearBuyNowCookie(response);
                } else if (encodedCartJson != null && !encodedCartJson.isEmpty()) {
                    cartService.processCheckout(encodedCartJson, ipAddr, userDetails.getUsername());
                    updateCartCookie(cartService.clearCart(userDetails.getUsername()), response, userId);
                }

                cartService.completeCheckout(cart, order, false, transactionId);

                // Update voucher usage if applied (for buy now)
                if (cart != null) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) cart.get("items");
                    if (items != null && !items.isEmpty()) {
                        Object vId = items.get(0).get("appliedVoucherId");
                        if (vId != null) {
                            Long voucherId = Long.valueOf(vId.toString());
                            voucherService.useVoucherForUserAndCourse(voucherId, userId);
                        }
                    }
                }

                redirectAttributes.addFlashAttribute("message", "VNPay payment successful!");
                if (courseId != null) {
                    return "redirect:/home/course-detail?id=" + courseId;
                } else {
                    return "redirect:/home";
                }
            } catch (CartServiceImpl.CourseAlreadyPurchasedException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                if (courseId != null) {
                    return "redirect:/home/course-detail?id=" + courseId;
                } else {
                    return "redirect:/home";
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "VNPay success but internal error: " + e.getMessage());
                if (courseId != null) {
                    return "redirect:/home/course-detail?id=" + courseId;
                } else {
                    return "redirect:/home";
                }
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "VNPay payment failed.");
            if (courseId != null) {
                return "redirect:/home/course-detail?id=" + courseId;
            } else {
                return "redirect:/home";
            }
        }
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

    private String getBuyNowCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("buy_now")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void clearBuyNowCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("buy_now", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
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