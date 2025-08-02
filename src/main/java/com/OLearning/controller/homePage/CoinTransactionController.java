package com.OLearning.controller.homePage;

import com.OLearning.dto.order.OrderHistoryDTO;
import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
import com.OLearning.security.CustomUserDetails;

import com.OLearning.service.order.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home/history")
public class CoinTransactionController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getMyCoursePurchaseHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(value = "courseName", required = false) String courseName,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model, HttpServletRequest request) {
        if (userDetails == null || !(userDetails instanceof CustomUserDetails)) {
            return "redirect:/login";
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUserId();
        Page<OrderHistoryDTO> orderPage = ordersService.getUserCoursePurchaseOrders(userId, courseName, status, startDate, endDate, page, size);
        User user = userRepository.findById(userId).orElse(null);

        
        model.addAttribute("transactions", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("pageSize", size);

        model.addAttribute("currentBalance", user != null ? user.getCoin() : 0);
        model.addAttribute("totalSpent", ordersService.getTotalSpent(userId));
        model.addAttribute("totalCoursesPurchased", ordersService.getTotalCoursesPurchased(userId));
        model.addAttribute("courseName", courseName);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
        model.addAttribute("fragmentContent", "homePage/fragments/historyContent :: historyContent");
        String requestedWith = request.getHeader("X-Requested-With");
        if (requestedWith != null && requestedWith.equalsIgnoreCase("XMLHttpRequest")) {
            return "homePage/fragments/historyContent :: historyContent";
        }
        return "homePage/index";
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public OrderHistoryDTO getOrderDetail(@PathVariable("id") Long orderId) {
        return ordersService.getOrderDetail(orderId);
    }
} 