package com.OLearning.controller.adminDashBoard;

import java.util.Comparator;
import java.util.List;
import java.util.Locale.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Import Model

import com.OLearning.dto.adminDashBoard.UserDTO;
import com.OLearning.entity.Categories;
import com.OLearning.service.adminDashBoard.CategoriesService;

import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
            List<Categories> categories = categoriesService.findAll();

            model.addAttribute("categories", categories);
            return "adminDashboard/fragments/category :: categoryList";
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/search")
    public String searchCategories(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false) String sort,
            Model model) {

        List<Categories> categories;

        if (name != null && !name.trim().isEmpty()) {
            categories = categoriesService.findByNameContaining(name);
        } else {
            categories = categoriesService.findAll();
        }

        if ("asc".equalsIgnoreCase(sort)) {
            categories.sort(Comparator.comparing(Categories::getName));
        } else if ("desc".equalsIgnoreCase(sort)) {
            categories.sort(Comparator.comparing(Categories::getName).reversed());
        }

        model.addAttribute("categories", categories);
        return "adminDashboard/fragments/category :: categoryList";
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute("") Categories category) {
        categoriesService.save(category);
        return "redirect:/admin/categories";
    }

}
