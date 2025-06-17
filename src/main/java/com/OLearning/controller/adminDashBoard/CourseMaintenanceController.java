package com.OLearning.controller.adminDashBoard;

import com.OLearning.entity.CourseMaintenance;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/courseMaintenance")
public class CourseMaintenanceController {
    @Autowired
    private CourseMaintenanceService courseMaintenanceService;

    @GetMapping
    public String getAllCourseMaintenances(Model model) {
        model.addAttribute("accNamePage", "Management Maintenance");
        List<CourseMaintenance> courseMaintenances = courseMaintenanceService.getAllCourseMaintenances();
        model.addAttribute("courseMaintenances", courseMaintenances);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseMaintenanceContent :: contentCourseMaintenances");
        return "adminDashBoard/index";
    }

    @GetMapping("/search")
    public String searchByUsername(Model model, @RequestParam(value = "username", required = false) String username) {
        model.addAttribute("accNamePage", "Management Maintenance");
        List<CourseMaintenance> courseMaintenances = courseMaintenanceService.searchByUsername(username);
        model.addAttribute("courseMaintenances", courseMaintenances);
        model.addAttribute("selectedUsername", username);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseMaintenanceContent :: contentCourseMaintenances");
        return "adminDashBoard/index";
    }
}
