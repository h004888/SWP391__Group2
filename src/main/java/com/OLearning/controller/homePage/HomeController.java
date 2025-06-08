package com.OLearning.controller.homePage;

import com.OLearning.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("categories", categoryService.findTop5ByOrderByIdAsc());
        model.addAttribute("allCategories", categoryService.findAll());
        model.addAttribute("message", "Hello World");
        return "homePage/index";
    }

    @GetMapping("/about")
    public String about() {
        return "homePage/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "homePage/contact";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "homePage/privacyPolicy";
    }
}
