package com.OLearning.controller.adminDashboard;

import com.OLearning.entity.BuyPackages;
import com.OLearning.service.adminDashBoard.BuyPackagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/buypackages")
public class BuyPackagesController {

    @Autowired
    private BuyPackagesService buyPackagesService;

    @GetMapping
    public String getAllBuyPackages(Model model) {
        List<BuyPackages> buyPackages = buyPackagesService.getAllBuyPackages();
        model.addAttribute("buyPackages", buyPackages);
        return "adminDashboard/buyPackages";
    }

}