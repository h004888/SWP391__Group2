package com.OLearning.controller.adminDashBoard;

import com.OLearning.entity.CourseMaintenance;
import com.OLearning.service.adminDashBoard.CourseMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/courseMaintenance")
public class CourseMaintenanceController {
    @Autowired
    private CourseMaintenanceService courseMaintenanceService;

    // Lấy toàn bộ danh sách
    @GetMapping
    public String getAllCourseMaintenances(Model model) {
        model.addAttribute("accNamePage", "Management Maintenance");
        List<CourseMaintenance> courseMaintenances = courseMaintenanceService.getAllCourseMaintenances();
        List<LocalDate> monthYears = courseMaintenanceService.getDistinctMonthYears();
        model.addAttribute("courseMaintenances", courseMaintenances);
        model.addAttribute("monthYears", monthYears);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseMaintenanceContent :: contentCourseMaintenances");
        return "adminDashBoard/index";
    }

    // Tìm kiếm theo username
    @GetMapping("/search")
    public String searchByUsername(
            Model model,
            @RequestParam(value = "username", required = false) String username) {
        model.addAttribute("accNamePage", "Management Maintenance");
        List<CourseMaintenance> courseMaintenances = courseMaintenanceService.searchByUsername(username);
        List<LocalDate> monthYears = courseMaintenanceService.getDistinctMonthYears();

        model.addAttribute("courseMaintenances", courseMaintenances);
        model.addAttribute("monthYears", monthYears);
        model.addAttribute("selectedUsername", username);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseMaintenanceContent :: contentCourseMaintenances");
        return "adminDashBoard/index";
    }

    // Lọc theo tháng-năm
    @GetMapping("/filter")
    public String filterByMonthYear(
            Model model,
            @RequestParam(value = "monthYear", required = false) String monthYear) {
        model.addAttribute("accNamePage", "Management Maintenance");
        List<CourseMaintenance> courseMaintenances = courseMaintenanceService.filterByMonthYear(monthYear);
        List<LocalDate> monthYears = courseMaintenanceService.getDistinctMonthYears();

        model.addAttribute("courseMaintenances", courseMaintenances);
        model.addAttribute("monthYears", monthYears);
        model.addAttribute("selectedMonthYear", monthYear);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseMaintenanceContent :: contentCourseMaintenances");
        return "adminDashBoard/index";
    }
}
