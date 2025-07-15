package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.order.OrdersDTO;
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

import java.util.List;

@Controller
@RequestMapping("/instructor/orders")
public class ControllerOrder {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private CourseMaintenanceService courseMaintenanceService;
    @Autowired
    private VietQRService vietQRService;
    
    @GetMapping
    public String getAllOrders(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               @RequestParam(value = "username", required = false) String username,
                               @RequestParam(value = "amountDirection", required = false) String amountDirection,
                               @RequestParam(value = "orderType", required = false) String orderType,
                               @RequestParam(value = "startDate", required = false) String startDate,
                               @RequestParam(value = "endDate", required = false) String endDate,
                               @RequestParam(value = "error", required = false) String error) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();
        // Default to PAID status for initial load
        Page<InstructorOrderDTO> ordersPage = ordersService.filterAndSortInstructorOrders(
                username, amountDirection, orderType, startDate, endDate, "PAID", page, size, instructorId);

        //ok
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
        model.addAttribute("fragmentContent", "instructorDashBoard/fragments/orderContent :: contentOrders");
        
        // Add error message if present
        if ("access_denied".equals(error)) {
            model.addAttribute("errorMessage", "You don't have permission to view this order.");
        }
        
        return "instructorDashboard/indexUpdate";
    }
    
    @GetMapping("/filter")
    public String filterOrders(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "amountDirection", required = false) String amountDirection,
            @RequestParam(value = "orderType", required = false) String orderType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();
        
        Page<InstructorOrderDTO> ordersPage = ordersService.filterAndSortInstructorOrders(
                username, amountDirection, orderType, startDate, endDate, status, page, size, instructorId);
        model.addAttribute("orders", ordersPage.getContent());

        return "instructorDashBoard/fragments/ordersTableRowContent :: ordersTableRowContent";
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
            @RequestParam(value = "size", defaultValue = "10") int size,
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


}
