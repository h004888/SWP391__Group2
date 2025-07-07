package com.OLearning.controller.homePage;

import com.OLearning.entity.Order;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.VoucherRepository;
import com.OLearning.repository.UserVoucherRepository;
import com.OLearning.service.cart.CartService;
import com.OLearning.service.cart.impl.CartServiceImpl;
import com.OLearning.service.order.OrdersService;
import com.OLearning.service.payment.VNPayService;
import com.OLearning.service.payment.VietQRService;
import com.OLearning.service.voucher.VoucherService;
import com.OLearning.service.wishlist.WishlistService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

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
    @Autowired
    private UserVoucherRepository userVoucherRepository;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private WishlistService wishlistService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private VietQRService vietQRService;

    @Value("${sepay.account_number}")
    private String sepayAccountNumber;

    @Value("${sepay.bank_code}")
    private String sepayBankCode;


    @GetMapping
    public String getCart(@AuthenticationPrincipal UserDetails userDetails,
                          HttpServletRequest request,
                          Model model,
                          @RequestParam(value = "message", required = false) String message) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedCartJson = getCartCookie(request, userId);
        Map<String, Object> cart = cartService.getCartDetails(encodedCartJson, userDetails.getUsername());
        model.addAttribute("cartItems", cart.getOrDefault("items", List.of()));
        model.addAttribute("totalPrice", calculateTotalPrice(cart));
        model.addAttribute("cartTotal", getLongValue(cart.getOrDefault("total", 0L)));
        model.addAttribute("currentUserId", userId);

        // Add wishlist total
        String encodedWishlistJson = getWishlistCookie(request, userId);
        Map<String, Object> wishlist = wishlistService.getWishlistDetails(encodedWishlistJson, userDetails.getUsername());
        model.addAttribute("wishlistTotal", getLongValue(wishlist.getOrDefault("total", 0L)));
        if ("qr_success".equals(message)) {
            model.addAttribute("message", "Thanh toán thành công bằng QR!");
        }
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
    @ResponseBody
    public Object addToCart(@PathVariable Long courseId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            Model model) {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            if (isAjax) {
                result.put("success", false);
                result.put("error", "Bạn cần đăng nhập!");
                return result;
            } else {
                return "redirect:/login";
            }
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedCartJson = getCartCookie(request, userId);
        try {
            Map<String, Object> cart = cartService.addCourseToCart(encodedCartJson, courseId, userDetails.getUsername());
            updateCartCookie(cart, response, userId);
            if (isAjax) {
                result.put("success", true);
                return result;
            } else {
                model.addAttribute("message", "Course added to cart successfully!");
            }
        } catch (CartServiceImpl.CourseAlreadyPurchasedException | CartServiceImpl.CourseAlreadyInCartException e) {
            if (isAjax) {
                result.put("success", false);
                result.put("error", e.getMessage());
                return result;
            } else {
                model.addAttribute("error", e.getMessage());
            }
        } catch (Exception e) {
            if (isAjax) {
                result.put("success", false);
                result.put("error", "Failed to add course to cart");
                return result;
            } else {
                model.addAttribute("error", "Failed to add course to cart");
            }
        }
        if (isAjax) {
            if (!result.containsKey("success")) {
                result.put("success", false);
                result.put("error", "Unknown error");
            }
            return result;
        } else {
            String referer = request.getHeader("Referer");
            if (referer != null) {
                return "redirect:" + referer;
            }
            return "redirect:/home";
        }
    }

    @GetMapping("/remove/{cartDetailId}")
    public String removeFromCart(@PathVariable String cartDetailId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        String encodedCartJson = getCartCookie(request, userId);
        try {
            Map<String, Object> cart = cartService.removeCartDetail(encodedCartJson, cartDetailId, userDetails.getUsername());
            updateCartCookie(cart, response, userId);
            model.addAttribute("message", "Item removed from cart successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to remove item from cart");
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails,
                            HttpServletResponse response,
                            Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        try {
            Map<String, Object> emptyCart = cartService.clearCart(userDetails.getUsername());
            updateCartCookie(emptyCart, response, userId);
            model.addAttribute("message", "Cart cleared successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to clear cart");
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam(value = "voucherMapping", required = false) String voucherMappingJson,
                           @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                           @AuthenticationPrincipal UserDetails userDetails,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {
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
                voucherMapping = objectMapper.readValue(voucherMappingJson, new TypeReference<Map<Long, Long>>() {});
            }
            double totalAmount = 0;
            for (Map<String, Object> item : items) {
                Long courseId = Long.valueOf(item.get("courseId").toString());
                double price = Double.valueOf(item.get("price").toString());
                if (voucherMapping.containsKey(courseId)) {
                    Long voucherId = voucherMapping.get(courseId);
                    double discount = voucherRepository.findById(voucherId).map(v -> v.getDiscount()).orElse(0.0);
                    price = Math.round(price * (1 - discount / 100.0));
                    item.put("price", price);
                    item.put("appliedVoucherId", voucherId);
                }
                totalAmount += price;
            }
            cart.put("items", items);
            updateCartCookie(cart, response, userId);
            String ipAddr = request.getRemoteAddr();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            if (user.getCoin() >= totalAmount) {
                Order order = new Order();
                order.setUser(user);
                order.setAmount(totalAmount);
                cartService.processCheckout(encodedCartJson, ipAddr, userDetails.getUsername());
                cartService.completeCheckout(cart, order, true, null);
                for (Map<String, Object> item : items) {
                    if (item.containsKey("appliedVoucherId")) {
                        Long voucherId = Long.valueOf(item.get("appliedVoucherId").toString());
                        voucherService.useVoucherForUserAndCourse(voucherId, userId);
                    }
                }
                updateCartCookie(cartService.clearCart(userDetails.getUsername()), response, userId);
                model.addAttribute("message", "Checkout completed using wallet!");
                return "redirect:/cart";
            } else if ("qr".equalsIgnoreCase(paymentMethod)) {
                Order order = ordersService.createOrder(user, totalAmount, "course_purchase", "temp_description");
                String description = "Mua khóa học OLearning - ORDER" + order.getOrderId();
                order.setDescription(description);
                ordersService.saveOrder(order);

                for (Map<String, Object> item : items) {
                    Long courseId = Long.valueOf(item.get("courseId").toString());
                    double price = Double.valueOf(item.get("price").toString());
                    com.OLearning.entity.Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));
                    com.OLearning.entity.OrderDetail orderDetail = new com.OLearning.entity.OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setCourse(course);
                    orderDetail.setUnitPrice(price);
                    ordersService.saveOrderDetail(orderDetail);
                }

                String qrUrl = vietQRService.generateSePayQRUrl(order.getAmount(), order.getDescription());
                model.addAttribute("orderId", order.getOrderId());
                model.addAttribute("amount", order.getAmount());
                model.addAttribute("description", order.getDescription());
                model.addAttribute("qrUrl", qrUrl);
                return "homePage/qr_checkout";
            } else if ("vnpay".equalsIgnoreCase(paymentMethod)) {
                request.setAttribute("amount", (int) (totalAmount)*100);
                request.setAttribute("cart", encodedCartJson);
                String currentPath = request.getRequestURI();
                String basePath = currentPath.substring(0, currentPath.lastIndexOf('/'));
                String returnUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(basePath)
                        .build()
                        .toUriString();
                request.setAttribute("urlReturn", returnUrl);
                return "redirect:" + vnPayService.createOrder(request);
            } else {
                model.addAttribute("error", "Vui lòng chọn phương thức thanh toán!");
                return "redirect:/cart";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Checkout error: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model,
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
                
                // Xử lý voucher sau khi thanh toán thành công
                List<Map<String, Object>> items = (List<Map<String, Object>>) cart.getOrDefault("items", List.of());
                for (Map<String, Object> item : items) {
                    if (item.containsKey("appliedVoucherId")) {
                        Long voucherId = Long.valueOf(item.get("appliedVoucherId").toString());
                        voucherService.useVoucherForUserAndCourse(voucherId, userId);
                    }
                }
                updateCartCookie(cartService.clearCart(userDetails.getUsername()), response, userId);
                model.addAttribute("message", "VNPay payment successful!");
                return "redirect:/cart";
            } catch (Exception e) {
                model.addAttribute("error", "VNPay success but internal error: " + e.getMessage());
                return "redirect:/cart";
            }
        } else {
            model.addAttribute("error", "VNPay payment failed.");
            return "redirect:/cart";
        }
    }

    @PostMapping("/apply-voucher")
    @ResponseBody
    public Map<String, Object> applyVoucherToCourse(@RequestBody Map<String, Object> req) {
        Long userId = Long.valueOf(req.get("userId").toString());
        Long courseId = Long.valueOf(req.get("courseId").toString());
        Long voucherId = Long.valueOf(req.get("voucherId").toString());
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            var userVoucherOpt = userVoucherRepository.findByUser_UserIdAndVoucher_VoucherId(userId, voucherId);
            if (userVoucherOpt.isEmpty()) {
                result.put("error", "User does not have this voucher");
                return result;
            }
            
            var userVoucher = userVoucherOpt.get();
            if (Boolean.TRUE.equals(userVoucher.getIsUsed())) {
                return result;
            }

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
            
            result.put("voucherId", voucherId);
            result.put("voucherCode", voucherCode);
            result.put("discountedPrice", (long) discountedPrice);
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/clear-cookie")
    @ResponseBody
    public String clearCartCookie(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
        if (userDetails == null) return "not_logged_in";
        Long userId = getUserIdFromUserDetails(userDetails);
        Cookie cartCookie = new Cookie("cart_" + userId, null);
        cartCookie.setPath("/");
        cartCookie.setMaxAge(0);
        cartCookie.setHttpOnly(true);
        response.addCookie(cartCookie);
        return "cleared";
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
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else {
            return 0L;
        }
    }

    private String getWishlistCookie(HttpServletRequest request, Long userId) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("wishlist_" + userId)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}