package com.OLearning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String showAdminDashboard(Model model) {
        model.addAttribute("content", "adminDashboard/fragments/content :: content");
        return "adminDashboard/index";
    }

}
