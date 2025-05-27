package com.OLearning.controller.adminDashBoard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Import Model

import com.OLearning.entity.Categories;
import com.OLearning.service.adminDashBoard.CategoriesService;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    private CategoriesService categoriesService;

    @GetMapping("/categories/fragment")
    public String getAllCategories(Model model) {
        List<Categories> categories = categoriesService.findAll();

        model.addAttribute("categories", categories);
        return "adminDashboard/fragments/category :: categoryList";
    }

    @GetMapping("/categories")
    public String getAll(Model model) {
        List<Categories> categories = categoriesService.findAll();
        model.addAttribute("fragmentContent", "adminDashboard/fragments/category :: categoryList");
        model.addAttribute("categories", categories);
        return "adminDashboard/index";
    }

    @GetMapping("/categories/delete")
    public String deleteCategory(@RequestParam("id") int id, Model model) {
        if (categoriesService.existsById(id)) {
            categoriesService.deleteById(id);
        }
        List<Categories> categories = categoriesService.findAll();
        model.addAttribute("categories", categories);
        return "adminDashboard/fragments/category :: categoryTable";
    }

    @GetMapping("/categories/search")
    public String searchCategory(@RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "select", required = false) String select,
            Model model) {
        List<Categories> categories = categoriesService.filterCategories(name, select);
        System.out.println("list categories: " + categories);
        model.addAttribute("categories", categories);
        return "adminDashboard/fragments/category :: categoryTable";

    }

    @PostMapping("/categories/add")
    public String addCategory(@RequestParam("name") String name, Model model) {
        if (categoriesService.existsByName(name)) {
            model.addAttribute("errorMessage", "Category already exists");
        } else {
            Categories newCategory = new Categories();
            newCategory.setName(name);
            categoriesService.save(newCategory);
            model.addAttribute("successMessage", "Category added successfully");
        }
        List<Categories> categories = categoriesService.findAll();
        model.addAttribute("categories", categories);
        return "adminDashboard/fragments/category :: categoryTable";
    }

}
