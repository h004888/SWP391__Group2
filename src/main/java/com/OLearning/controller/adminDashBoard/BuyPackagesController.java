package com.OLearning.controller.adminDashBoard;

import com.OLearning.entity.BuyPackages;
import com.OLearning.service.adminDashBoard.BuyPackagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/buyPackages")
public class BuyPackagesController {

    @Autowired
    private BuyPackagesService buyPackagesService;

    @GetMapping
    public String getAllBuyPackages(Model model) {
        List<BuyPackages> buyPackages = buyPackagesService.getAllBuyPackages();
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/buyPackagesContent :: contentBuyPackages");
        model.addAttribute("buyPackages", buyPackages);
        return "adminDashBoard/index";

    }

}