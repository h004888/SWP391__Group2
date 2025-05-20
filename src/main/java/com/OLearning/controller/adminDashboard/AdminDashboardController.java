package com.OLearning.controller.adminDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    @GetMapping("/")
    public String getAdminDashboardPAge(Model model){
        model.addAttribute("textDemo","Test Thymleaf");
        return "adminDashboard/index";
    }
}
