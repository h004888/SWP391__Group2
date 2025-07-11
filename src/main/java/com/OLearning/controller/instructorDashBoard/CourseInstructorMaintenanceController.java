package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.order.InstructorOrderDTO;
import com.OLearning.entity.CourseMaintenance;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import com.OLearning.service.order.OrdersService;
import com.OLearning.service.payment.VietQRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/instructor/maintenance")
public class CourseInstructorMaintenanceController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private CourseMaintenanceService courseMaintenanceService;
    @Autowired
    private VietQRService vietQRService;

    @GetMapping
    public String getAllMaintenances(Model model,
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
        // Fetch maintenance payments for this instructor
        List<CourseMaintenance> maintenancePayments = courseMaintenanceService.getMaintenancesByInstructorId(instructorId);
        // Tách thành 2 list theo trạng thái
        List<CourseMaintenance> pendingPayments = maintenancePayments.stream()
                .filter(m -> !"PAID".equalsIgnoreCase(m.getStatus()))
                .toList();
        List<CourseMaintenance> paidPayments = maintenancePayments.stream()
                .filter(m -> "PAID".equalsIgnoreCase(m.getStatus()))
                .toList();
        model.addAttribute("pendingPayments", pendingPayments);
        model.addAttribute("paidPayments", paidPayments);
        model.addAttribute("maintenancePayments", maintenancePayments); // giữ lại nếu cần
        //ok
        model.addAttribute("accNamePage", "Management Orders");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("totalItems", ordersPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("username", username);
        model.addAttribute("amountDirection", amountDirection);
        model.addAttribute("orderType", orderType);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("fragmentContent", "instructorDashBoard/fragments/courseInstructorMaintainceContent :: maintenanceContent");

        // Add error message if present
        if ("access_denied".equals(error)) {
            model.addAttribute("errorMessage", "You don't have permission to view this order.");
        }
        return "instructorDashboard/indexUpdate";
    }


    @GetMapping("/pay/{maintenanceId}")
    public String payMaintenance(@PathVariable("maintenanceId") Long maintenanceId, Model model, RedirectAttributes redirectAttributes) {
        CourseMaintenance maintenance = courseMaintenanceService.getAllCourseMaintenances().stream()
                .filter(cm -> cm.getMaintenanceId().equals(maintenanceId))
                .findFirst().orElse(null);
        if (maintenance == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Maintenance fee not found!");
            return "redirect:/instructor/orders";
        }
        String description = "thanh toan bao tri thang "+ maintenance.getMonthYear() + "MAINTENANCE" + maintenance.getMaintenanceId();
        double amount = maintenance.getFee() != null ? maintenance.getFee().getMaintenanceFee() : 0.0;
        String qrUrl = vietQRService.generateSePayQRUrl(amount, description);
        model.addAttribute("maintenanceId", maintenance.getMaintenanceId());
        model.addAttribute("isMaintenance", true);
        model.addAttribute("amount", amount);
        model.addAttribute("description", description);
        model.addAttribute("qrUrl", qrUrl);
        return "homePage/qr_checkout";
    }
}
