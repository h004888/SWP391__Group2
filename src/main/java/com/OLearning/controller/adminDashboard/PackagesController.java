package com.OLearning.controller.adminDashboard;

import com.OLearning.entity.Packages;
import com.OLearning.service.adminDashBoard.PackagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/packages")
public class PackagesController {

    @Autowired
    private PackagesService packagesService;

    @GetMapping
    public String getAllPackages(Model model) {
        List<Packages> packages = packagesService.getAllPackages();
        model.addAttribute("packages", packages);
        return "adminDashboard/packages";
    }
}
