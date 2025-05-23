package com.OLearning.controller.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Import Model

import com.OLearning.entity.Categories;
import com.OLearning.service.categories.CategoriesService;

import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    private final CategoriesService categoriesService;

    public CategoryController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping("/categories/fragment")
    public String getAllCategories(Model model) {
        List<Categories> categories = categoriesService.findAll();

        model.addAttribute("categories", categories);
        return "adminDashboard/fragments/category :: categoryList";
    }

    @GetMapping("/categories")
    public String getAll(Model model) {
        List<Categories> categories = categoriesService.findAll();
        model.addAttribute("content", "adminDashboard/fragments/category :: categoryList");
        model.addAttribute("categories", categories);
        return "adminDashboard/index";
    }

    @GetMapping("/categories/{id}")
    public String getCategoryById(@PathParam("id") int id, Model model) {
        Categories categories = categoriesService.findById(id);
        System.out.println(categories);
        model.addAttribute("categories", categories);
        return "adminDashboard/fragments/category :: categoryDetails";
    }

}
