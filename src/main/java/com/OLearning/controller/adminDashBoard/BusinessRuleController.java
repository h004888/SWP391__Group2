package com.OLearning.controller.adminDashBoard;

import com.OLearning.service.termsAndCondition.TermsAndConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/businessRule")
public class BusinessRuleController {
    @Autowired
    private TermsAndConditionService termsAndConditionService;

    @RequestMapping
    public String getBusinessRulePage(Model model) {
        model.addAttribute("terms", termsAndConditionService.getAll());
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/businessRuleContent :: businessRuleContent");
        return "adminDashBoard/index";
    }
}
