package com.OLearning.controller.adminDashBoard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/businessRule")
public class BusinessRuleController {
    @RequestMapping
    public String getBusinessRulePage(Model model) {
        model.addAttribute("fragmentContent", "adminDashboard/fragments/businessRuleContent :: businessRuleContent");
        return "adminDashboard/index";
    }
}
