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
        model.addAttribute("allCategories", categoryService.getAllCategory());

        return "homePage/index";
    }

}
