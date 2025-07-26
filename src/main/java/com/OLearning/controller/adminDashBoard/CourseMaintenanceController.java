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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/courseMaintenance")
public class CourseMaintenanceController {
    private static final String ACC_NAME_PAGE_MANAGEMENT_FEE = "Management Maintenance Fee";
    private static final String ACC_NAME_PAGE_MANAGEMENT = "Management Maintenance";
    private static final String SUCCESS_FEE_UPDATED = "Fee rule updated successfully";
    private static final String ERROR_FEE_UPDATE = "Error updating fee rule: ";
    private static final String SUCCESS_FEE_DELETED = "Fee rule deleted successfully";
    private static final String ERROR_FEE_DELETE = "Error deleting fee rule: ";
    private static final String SUCCESS_FEE_ADDED = "New fee rule added successfully";
    private static final String ERROR_FEE_ADD = "Error adding new fee rule: ";
    private static final String SUCCESS_MONTHLY_PROCESS = "Monthly maintenance process completed successfully";
    private static final String SUCCESS_OVERDUE_CHECK = "Overdue maintenance check completed successfully";

    @Autowired
    private CourseMaintenanceService courseMaintenanceService;

    @GetMapping
    public String getAllCourseMaintenances(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseMaintenance> courseMaintenances = courseMaintenanceService.filterMaintenances(
            null, "pending", null, pageable);
        
        model.addAttribute("accNamePage", ACC_NAME_PAGE_MANAGEMENT_FEE);
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
        Page<CourseMaintenance> courseMaintenances = courseMaintenanceService.filterMaintenances(username, "pending", null, pageable);
        
        model.addAttribute("accNamePage", ACC_NAME_PAGE_MANAGEMENT);
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

        return "adminDashBoard/fragments/courseMaintenanceTableRowContent :: maintenanceTableRowContent";
    }


    @GetMapping("/pagination")
    public String getPagination(
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
                monthYearDate = yearMonth.atDay(1);
            } catch (Exception e) {
                System.err.println("Error parsing monthYear: " + e.getMessage());
            }
        }
        
        Page<CourseMaintenance> courseMaintenances = courseMaintenanceService.filterMaintenances(
            username, status, monthYearDate, pageable);
        
        model.addAttribute("courseMaintenances", courseMaintenances.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courseMaintenances.getTotalPages());
        model.addAttribute("totalItems", courseMaintenances.getTotalElements());
        model.addAttribute("pageSize", size);

        return "adminDashBoard/fragments/courseMaintenanceTableRowContent :: maintenancePagination";
    }

    @GetMapping("/count")
    @ResponseBody
    public long getMaintenanceCount(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String monthYear) {
        
        // Parse monthYear string to LocalDate if provided
        LocalDate monthYearDate = null;
        if (monthYear != null && !monthYear.isEmpty()) {
            try {
                YearMonth yearMonth = YearMonth.parse(monthYear, DateTimeFormatter.ofPattern("yyyy-MM"));
                monthYearDate = yearMonth.atDay(1);
            } catch (Exception e) {
                System.err.println("Error parsing monthYear: " + e.getMessage());
            }
        }
        
        Page<CourseMaintenance> courseMaintenances = courseMaintenanceService.filterMaintenances(
            username, status, monthYearDate, PageRequest.of(0, 1)); // Get only 1 item to check total

        return courseMaintenances.getTotalElements();
    }

    @PostMapping("/fees/update")
    public String updateFee(
            @RequestParam Long feeId,
            @RequestParam String minEnrollments,
            @RequestParam(required = false) String maxEnrollments,
            @RequestParam String maintenanceFee,
            RedirectAttributes redirectAttributes) {
        try {
            // Validate and convert parameters
            if (minEnrollments == null || minEnrollments.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Min Enrollments cannot be empty!");
                return "redirect:/admin/courseMaintenance";
            }
            
            Long minEnrollmentsLong;
            Long maxEnrollmentsLong = null;
            Long maintenanceFeeLong;
            
            try {
                minEnrollmentsLong = Long.parseLong(minEnrollments.trim());
                if (minEnrollmentsLong <= 0) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Min Enrollments must be greater than 0!");
                    return "redirect:/admin/courseMaintenance";
                }
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Min Enrollments must be a valid number!");
                return "redirect:/admin/courseMaintenance";
            }
            
            if (maxEnrollments != null && !maxEnrollments.trim().isEmpty()) {
                try {
                    maxEnrollmentsLong = Long.parseLong(maxEnrollments.trim());
                    if (maxEnrollmentsLong <= 0) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Max Enrollments must be greater than 0!");
                        return "redirect:/admin/courseMaintenance";
                    }
                    if (minEnrollmentsLong >= maxEnrollmentsLong) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Min Enrollments must be less than Max Enrollments!");
                        return "redirect:/admin/courseMaintenance";
                    }
                } catch (NumberFormatException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Max Enrollments must be a valid number!");
                    return "redirect:/admin/courseMaintenance";
                }
            }
            
            try {
                maintenanceFeeLong = Long.parseLong(maintenanceFee.trim());
                if (maintenanceFeeLong <= 0) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Maintenance Fee must be greater than 0!");
                    return "redirect:/admin/courseMaintenance";
                }
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Maintenance Fee must be a valid number!");
                return "redirect:/admin/courseMaintenance";
            }
            
            courseMaintenanceService.updateFee(feeId, minEnrollmentsLong, maxEnrollmentsLong, maintenanceFeeLong);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_FEE_UPDATED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_FEE_UPDATE + e.getMessage());
        }
        return "redirect:/admin/courseMaintenance";
    }

    @PostMapping("/fees/delete")
    public String deleteFee(
            @RequestParam Long feeId,
            RedirectAttributes redirectAttributes) {
        try {
            courseMaintenanceService.deleteFee(feeId);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_FEE_DELETED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_FEE_DELETE + e.getMessage());
        }
        return "redirect:/admin/courseMaintenance";
    }

    @PostMapping("/fees/add")
    public String addFee(
            @RequestParam String minEnrollments,
            @RequestParam(required = false) String maxEnrollments,
            @RequestParam String maintenanceFee,
            RedirectAttributes redirectAttributes) {
        try {
            // Validate and convert parameters
            if (minEnrollments == null || minEnrollments.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Min Enrollments cannot be empty!");
                return "redirect:/admin/courseMaintenance";
            }
            
            Long minEnrollmentsLong;
            Long maxEnrollmentsLong = null;
            Long maintenanceFeeLong;
            
            try {
                minEnrollmentsLong = Long.parseLong(minEnrollments.trim());
                if (minEnrollmentsLong <= 0) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Min Enrollments must be greater than 0!");
                    return "redirect:/admin/courseMaintenance";
                }
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Min Enrollments must be a valid number!");
                return "redirect:/admin/courseMaintenance";
            }
            
            if (maxEnrollments != null && !maxEnrollments.trim().isEmpty()) {
                try {
                    maxEnrollmentsLong = Long.parseLong(maxEnrollments.trim());
                    if (maxEnrollmentsLong <= 0) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Max Enrollments must be greater than 0!");
                        return "redirect:/admin/courseMaintenance";
                    }
                    if (minEnrollmentsLong >= maxEnrollmentsLong) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Min Enrollments must be less than Max Enrollments!");
                        return "redirect:/admin/courseMaintenance";
                    }
                } catch (NumberFormatException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Max Enrollments must be a valid number!");
                    return "redirect:/admin/courseMaintenance";
                }
            }
            
            try {
                maintenanceFeeLong = Long.parseLong(maintenanceFee.trim());
                if (maintenanceFeeLong <= 0) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Maintenance Fee must be greater than 0!");
                    return "redirect:/admin/courseMaintenance";
                }
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Maintenance Fee must be a valid number!");
                return "redirect:/admin/courseMaintenance";
            }
            
            courseMaintenanceService.addFee(minEnrollmentsLong, maxEnrollmentsLong, maintenanceFeeLong);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_FEE_ADDED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_FEE_ADD + e.getMessage());
        }
        return "redirect:/admin/courseMaintenance";
    }

    @GetMapping("/process-monthly")
    public String processMonthlyMaintenance(Model model, RedirectAttributes redirectAttributes) {
        courseMaintenanceService.processMonthlyMaintenance();
        redirectAttributes.addFlashAttribute("successMessage", SUCCESS_MONTHLY_PROCESS);
        return "redirect:/admin/courseMaintenance";
    }

    @GetMapping("/check-overdue")
    public String checkOverdueMaintenance(Model model, RedirectAttributes redirectAttributes) {
        courseMaintenanceService.checkOverdueMaintenance();
        redirectAttributes.addFlashAttribute("successMessage", SUCCESS_OVERDUE_CHECK);
        return "redirect:/admin/courseMaintenance";
    }
}
