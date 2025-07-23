package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.order.InstructorOrderDTO;
import com.OLearning.entity.CourseMaintenance;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import com.OLearning.service.order.OrdersService;
import com.OLearning.service.payment.VietQRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
                                     @RequestParam(value = "courseName", required = false) String courseName,
                                     @RequestParam(value = "monthYear", required = false) String monthYear,
                                     @RequestParam(value = "error", required = false) String error) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();

        LocalDate monthYearDate = null;
        if (monthYear != null && !monthYear.isEmpty()) {
            try {
                YearMonth ym = YearMonth.parse(monthYear);
                monthYearDate = ym.atEndOfMonth();
            } catch (Exception e) {
                monthYearDate = null;
            }
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<CourseMaintenance> maintenancePage = courseMaintenanceService.filterMaintenancesByInstructor(
                instructorId, courseName, monthYearDate, pageable);

        List<CourseMaintenance> pendingPayments = maintenancePage.getContent().stream()
                .filter(m -> !"PAID".equalsIgnoreCase(m.getStatus()))
                .toList();
        List<CourseMaintenance> paidPayments = maintenancePage.getContent().stream()
                .filter(m -> "PAID".equalsIgnoreCase(m.getStatus()))
                .toList();
        
        model.addAttribute("pendingPayments", pendingPayments);
        model.addAttribute("paidPayments", paidPayments);
        model.addAttribute("maintenancePayments", maintenancePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", maintenancePage.getTotalPages());
        model.addAttribute("totalItems", maintenancePage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("courseName", courseName);
        model.addAttribute("monthYear", monthYear);
        model.addAttribute("accNamePage", "My Maintenance");
        model.addAttribute("fragmentContent", "instructorDashBoard/fragments/courseInstructorMaintainceContent :: maintenanceContent");

        if ("access_denied".equals(error)) {
            model.addAttribute("errorMessage", "You don't have permission to view this order.");
        }
        return "instructorDashboard/indexUpdate";
    }

    @GetMapping("/pay/{maintenanceId}")
    public String payMaintenance(@PathVariable("maintenanceId") Long maintenanceId, Model model, RedirectAttributes redirectAttributes) {
        try {
            CourseMaintenance maintenance = courseMaintenanceService.getAllCourseMaintenances().stream()
                    .filter(cm -> cm.getMaintenanceId().equals(maintenanceId))
                    .findFirst().orElse(null);
            if (maintenance == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Maintenance fee not found!");
                return "redirect:/instructor/orders";
            }
            String monthYearStr = maintenance.getMonthYear().format(DateTimeFormatter.ofPattern("MM/yyyy"));
            String description = "thanh toan bao tri thang " + monthYearStr + " MAINTENANCE" + maintenance.getMaintenanceId();
            double amount = maintenance.getFee() != null ? maintenance.getFee().getMaintenanceFee() : 0.0;
            String qrUrl = vietQRService.generateSePayQRUrl(amount, description);
            model.addAttribute("maintenanceId", maintenance.getMaintenanceId());
            model.addAttribute("isMaintenance", true);
            model.addAttribute("amount", amount);
            model.addAttribute("description", description);
            model.addAttribute("qrUrl", qrUrl);
            return "homePage/qr_checkout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing maintenance payment: " + e.getMessage());
            return "redirect:/instructor/maintenance";
        }
    }
}
