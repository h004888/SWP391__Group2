package com.OLearning.controller.adminDashBoard;

import com.OLearning.entity.CourseMaintenance;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/courseMaintenance")
public class CourseMaintenanceController {
    @Autowired
    private CourseMaintenanceService courseMaintenanceService;

    @GetMapping
    public String getAllCourseMaintenances(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseMaintenance> courseMaintenances = courseMaintenanceService.getAllCourseMaintenances(pageable);
        
        model.addAttribute("accNamePage", "Management Maintenance");
        model.addAttribute("courseMaintenances", courseMaintenances.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courseMaintenances.getTotalPages());
        model.addAttribute("totalItems", courseMaintenances.getTotalElements());
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseMaintenanceContent :: contentCourseMaintenances");
        model.addAttribute("fees", courseMaintenanceService.getListFees());
        return "adminDashBoard/index";
    }

    @GetMapping("/search")
    public String searchByUsername(
            Model model,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseMaintenance> courseMaintenances = courseMaintenanceService.filterMaintenances(username, null, null, pageable);
        
        model.addAttribute("accNamePage", "Management Maintenance");
        model.addAttribute("courseMaintenances", courseMaintenances.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courseMaintenances.getTotalPages());
        model.addAttribute("totalItems", courseMaintenances.getTotalElements());
        model.addAttribute("selectedUsername", username);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseMaintenanceContent :: contentCourseMaintenances");
        return "adminDashBoard/index";
    }

    @GetMapping("/filter")
    public String filterMaintenances(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String monthYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        // Parse monthYear string to LocalDate if provided
        LocalDate monthYearDate = null;
        if (monthYear != null && !monthYear.isEmpty()) {
            try {
                YearMonth yearMonth = YearMonth.parse(monthYear, DateTimeFormatter.ofPattern("yyyy-MM"));
                // Set to first day of month to ensure consistent comparison
                monthYearDate = yearMonth.atDay(1);
            } catch (Exception e) {
                // Handle parsing error if needed
                System.err.println("Error parsing monthYear: " + e.getMessage());
            }
        }
        
        Page<CourseMaintenance> courseMaintenances = courseMaintenanceService.filterMaintenances(
            username, status, monthYearDate, pageable);
        
        model.addAttribute("courseMaintenances", courseMaintenances.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courseMaintenances.getTotalPages());
        model.addAttribute("totalItems", courseMaintenances.getTotalElements());
        
        return "adminDashBoard/fragments/courseMaintenanceContent :: maintenanceTableFragment";
    }

    @PostMapping("/fees/update")
    public String updateFee(
            @RequestParam Long feeId,
            @RequestParam Long minEnrollments,
            @RequestParam Long maxEnrollments,
            @RequestParam Long maintenanceFee,
            RedirectAttributes redirectAttributes) {
        try {
            courseMaintenanceService.updateFee(feeId, minEnrollments, maxEnrollments, maintenanceFee);
            redirectAttributes.addFlashAttribute("successMessage", "Fee rule updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating fee rule: " + e.getMessage());
        }
        return "redirect:/admin/courseMaintenance";
    }

    @PostMapping("/fees/delete")
    public String deleteFee(
            @RequestParam Long feeId,
            RedirectAttributes redirectAttributes) {
        try {
            courseMaintenanceService.deleteFee(feeId);
            redirectAttributes.addFlashAttribute("successMessage", "Fee rule deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting fee rule: " + e.getMessage());
        }
        return "redirect:/admin/courseMaintenance";
    }

    @PostMapping("/fees/add")
    public String addFee(
            @RequestParam Long minEnrollments,
            @RequestParam Long maxEnrollments,
            @RequestParam Long maintenanceFee,
            RedirectAttributes redirectAttributes) {
        try {
            courseMaintenanceService.addFee(minEnrollments, maxEnrollments, maintenanceFee);
            redirectAttributes.addFlashAttribute("successMessage", "New fee rule added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding new fee rule: " + e.getMessage());
        }
        return "redirect:/admin/courseMaintenance";
    }

    @GetMapping("/process-monthly")
    public String processMonthlyMaintenance(Model model, RedirectAttributes redirectAttributes) {
        courseMaintenanceService.processMonthlyMaintenance();
        redirectAttributes.addFlashAttribute("successMessage", "Monthly maintenance process completed successfully");
        return "redirect:/admin/courseMaintenance";
    }

    @GetMapping("/check-overdue")
    public String checkOverdueMaintenance(Model model, RedirectAttributes redirectAttributes) {
        courseMaintenanceService.checkOverdueMaintenance();
        redirectAttributes.addFlashAttribute("successMessage", "Overdue maintenance check completed successfully");
        return "redirect:/admin/courseMaintenance";
    }
}
