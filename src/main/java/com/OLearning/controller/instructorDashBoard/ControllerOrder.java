package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.order.InstructorOrderDTO;
import com.OLearning.entity.OrderDetail;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.order.OrdersService;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import com.OLearning.entity.CourseMaintenance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.OLearning.service.payment.VietQRService;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import com.OLearning.dto.course.CourseSalesDTO;
import com.OLearning.dto.order.OrderStatsDTO;

@Controller
@RequestMapping("/instructor/orders")
public class ControllerOrder {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private CourseMaintenanceService courseMaintenanceService;
    @Autowired
    private VietQRService vietQRService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @GetMapping
    public String getAllOrders(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size,
                               @RequestParam(value = "username", required = false) String username,
                               @RequestParam(value = "amountDirection", required = false) String amountDirection,
                               @RequestParam(value = "orderType", required = false) String orderType,
                               @RequestParam(value = "startDate", required = false) String startDate,
                               @RequestParam(value = "endDate", required = false) String endDate,
                               @RequestParam(value = "error", required = false) String error,
                               @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();
        Page<InstructorOrderDTO> ordersPage = ordersService.filterAndSortInstructorOrders(
                username, amountDirection, orderType, startDate, endDate, "PAID", page, size, instructorId);
        model.addAttribute("accNamePage", "Management Orders");
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("totalItems", ordersPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("username", username);
        model.addAttribute("amountDirection", amountDirection);
        model.addAttribute("orderType", orderType);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        if ("XMLHttpRequest".equals(requestedWith)) {
            model.addAttribute("errorMessage", error);
            return "instructorDashboard/fragments/orderContent :: orderList";
        }
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/orderContent :: orderPage");
        return "instructorDashboard/indexUpdate";
    }
    
    @GetMapping("/filter")
    @ResponseBody
    public Map<String, String> filterOrders(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "amountDirection", required = false) String amountDirection,
            @RequestParam(value = "orderType", required = false) String orderType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();
        
        Page<InstructorOrderDTO> ordersPage = ordersService.filterAndSortInstructorOrders(
                username, amountDirection, orderType, startDate, endDate, status, page, size, instructorId);

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("totalItems", ordersPage.getTotalElements());
        model.addAttribute("pageSize", size);

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(model.asMap());

        String tableHtml = templateEngine.process("instructorDashboard/fragments/ordersTableRowContent :: ordersTableRowContent", thymeleafContext);
        String paginationHtml = templateEngine.process("instructorDashboard/fragments/ordersTableRowContent :: ordersPagination", thymeleafContext);

        Map<String, String> result = new HashMap<>();
        result.put("table", tableHtml);
        result.put("pagination", paginationHtml);
        return result;
    }

    // New endpoint for pagination only
    @GetMapping("/pagination")
    public String getPagination(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "amountDirection", required = false) String amountDirection,
            @RequestParam(value = "orderType", required = false) String orderType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();
        
        Page<InstructorOrderDTO> ordersPage = ordersService.filterAndSortInstructorOrders(
                username, amountDirection, orderType, startDate, endDate, status, page, size, instructorId);

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("totalItems", ordersPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "instructorDashBoard/fragments/ordersTableRowContent :: ordersPagination";
    }

    @GetMapping("/count")
    @ResponseBody
    public long getOrderCount(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "amountDirection", required = false) String amountDirection,
            @RequestParam(value = "orderType", required = false) String orderType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", required = false) String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();

        Page<InstructorOrderDTO> ordersPage = ordersService.filterAndSortInstructorOrders(
                username, amountDirection, orderType, startDate, endDate, status, 0, 1, instructorId); // Get only 1 item to check total

        return ordersPage.getTotalElements();
    }

    @GetMapping("/view/{orderId}")
    public String viewOrderDetails(@PathVariable("orderId") Long orderId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();

        List<OrderDetail> orderDetails = ordersService.getInstructorOrderDetailsByOrderId(orderId, instructorId);

        // Check if instructor has any courses in this order
        if (orderDetails.isEmpty()) {
            return "redirect:/instructor/orders?error=access_denied";
        }

        // TÍNH TỔNG DOANH THU CHO INSTRUCTOR
        double totalAmount = orderDetails.stream()
                .mapToDouble(detail -> {
                    double unitPrice = detail.getUnitPrice() != null ? detail.getUnitPrice() : 0;
                    double discount = detail.getCourse().getDiscount() != null ? detail.getCourse().getDiscount() : 0;
                    return unitPrice * (1 - discount / 100.0);
                }).sum();

        model.addAttribute("accNamePage", "Order Details");
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("orderId", orderId);
        model.addAttribute("totalAmount", totalAmount); // <-- TRUYỀN VÀO MODEL
        model.addAttribute("fragmentContent", "instructorDashBoard/fragments/orderDetailsContent :: contentOrderDetails");
        return "instructorDashboard/indexUpdate";
    }

    @GetMapping("/statistics")
    @ResponseBody
    public List<OrderStatsDTO> getOrderStatistics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();
        return ordersService.getStatsForInstructor(instructorId);
    }
}
