package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.order.InstructorOrderDTO;
import com.OLearning.entity.CourseMaintenance;
import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
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
import java.util.UUID;

@Controller
@RequestMapping("/instructor/maintenance")
public class CourseInstructorMaintenanceController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private CourseMaintenanceService courseMaintenanceService;
    @Autowired
    private VietQRService vietQRService;
    @Autowired
    private UserRepository userRepository;

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
                .filter(m -> "pending".equalsIgnoreCase(m.getStatus()))
                .toList();
        List<CourseMaintenance> paidPayments = maintenancePage.getContent().stream()
                .filter(m -> "PAID".equalsIgnoreCase(m.getStatus()))
                .toList();
        List<CourseMaintenance> overduePayments = maintenancePage.getContent().stream()
                .filter(m -> "overdue".equalsIgnoreCase(m.getStatus()))
                .toList();
        
        model.addAttribute("pendingPayments", pendingPayments);
        model.addAttribute("paidPayments", paidPayments);
        model.addAttribute("overduePayments", overduePayments);
        model.addAttribute("maintenancePayments", maintenancePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", maintenancePage.getTotalPages());
        model.addAttribute("totalItems", maintenancePage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("courseName", courseName);
        model.addAttribute("monthYear", monthYear);
        model.addAttribute("accNamePage", "My Maintenance");
        model.addAttribute("fragmentContent", "instructorDashBoard/fragments/courseInstructorMaintainceContent :: maintenanceContent");
        
        // Add instructor coin balance to model
        User instructor = userRepository.findById(instructorId).orElse(null);
        if (instructor != null) {
            model.addAttribute("instructorCoin", instructor.getCoin());
        }

        if ("access_denied".equals(error)) {
            model.addAttribute("errorMessage", "You don't have permission to view this order.");
        }
        return "instructorDashboard/indexUpdate";
    }

    @GetMapping("/pay/{maintenanceId}")
    public String payMaintenance(@PathVariable("maintenanceId") Long maintenanceId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User instructor = userRepository.findById(userDetails.getUserId()).orElse(null);
            
            if (instructor == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
                return "redirect:/instructor/maintenance";
            }

            CourseMaintenance maintenance = courseMaintenanceService.getAllCourseMaintenances().stream()
                    .filter(cm -> cm.getMaintenanceId().equals(maintenanceId))
                    .findFirst().orElse(null);
            if (maintenance == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Maintenance fee not found!");
                return "redirect:/instructor/maintenance";
            }

            if ("PAID".equalsIgnoreCase(maintenance.getStatus())) {
                redirectAttributes.addFlashAttribute("errorMessage", "This maintenance fee has already been paid!");
                return "redirect:/instructor/maintenance";
            }

            double amount = maintenance.getFee() != null ? maintenance.getFee().getMaintenanceFee() : 0.0;

            if (instructor.getCoin() >= amount) {
                String refCode = UUID.randomUUID().toString();
                boolean success = courseMaintenanceService.processMaintenancePayment(maintenanceId, refCode);
                
                if (success) {
                    redirectAttributes.addFlashAttribute("successMessage", "Payment successful! Maintenance fee has been paid using coin.");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Payment failed! Please try again.");
                }
                
                return "redirect:/instructor/maintenance";
            } else {
                String monthYearStr = maintenance.getMonthYear().format(DateTimeFormatter.ofPattern("MM/yyyy"));
                String description = "thanh toan bao tri thang " + monthYearStr + " MAINTENANCE" + maintenance.getMaintenanceId();
                String qrUrl = vietQRService.generateSePayQRUrl(amount, description);
                model.addAttribute("maintenanceId", maintenance.getMaintenanceId());
                model.addAttribute("isMaintenance", true);
                model.addAttribute("amount", amount);
                model.addAttribute("description", description);
                model.addAttribute("qrUrl", qrUrl);
                model.addAttribute("instructorCoin", instructor.getCoin());
                model.addAttribute("insufficientCoin", true);
                return "homePage/qr_checkout";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing maintenance payment: " + e.getMessage());
            return "redirect:/instructor/maintenance";
        }
    }
}
