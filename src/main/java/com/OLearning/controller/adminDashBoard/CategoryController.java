package com.OLearning.controller.adminDashBoard;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.OLearning.entity.Categories;
import com.OLearning.service.adminDashBoard.CategoriesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin")
public class CategoryController {
    private static final String adminDashboardPath = "adminDashboard/index";
    private static final String fragmentContent = "fragmentContent";
    private static final String categoryList = "adminDashboard/fragments/category :: categoryList";
    private static final String categoryTable = "adminDashboard/fragments/category :: categoryTable";
    @Autowired
    private CategoriesService categoriesService;

    @GetMapping("/categories/fragment")
    public String getAllCategories(Model model) {
        List<Categories> categories = categoriesService.findAll();

        model.addAttribute("categories", categories);
        return "adminDashboard/fragments/category :: categoryList";
    }

    // @GetMapping("/categories")
    // public String getAll(Model model) {
    // List<Categories> categories = categoriesService.findAll();
    // model.addAttribute(fragmentContent, "adminDashboard/fragments/category ::
    // categoryList");
    // model.addAttribute("categories", categories);
    // return adminDashboardPath;
    // }

    @GetMapping("/categories")
    public String getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            Model model) {

        Page<Categories> categoriesPage = categoriesService.findAllCategory(page, size, sortBy, sortDirection);

        model.addAttribute("categories", categoriesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoriesPage.getTotalPages());
        model.addAttribute("totalItems", categoriesPage.getTotalElements());
        model.addAttribute(fragmentContent, categoryList);

        return "adminDashboard/index";
    }

    @GetMapping("/categories/delete")
    public String deleteCategory(@RequestParam("id") int id, Model model) {
        if (categoriesService.existsById(id)) {
            categoriesService.deleteById(id);
        }
        List<Categories> categories = categoriesService.findAll();
        model.addAttribute("categories", categories);
        return categoryTable;
    }

    @GetMapping("/categories/search")
    public String searchCategory(@RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "select", required = false) String select,
            Model model) {
        List<Categories> categories = categoriesService.filterCategories(name, select);
        System.out.println("list categories: " + categories);
        model.addAttribute("categories", categories);
        return categoryTable;

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
        model.addAttribute(fragmentContent, categoryList);
        model.addAttribute("categories", categories);
        return adminDashboardPath;
    }

    @GetMapping("/categories/edit")
    public String editCategory(@RequestParam("id") int id, Model model) {
        Categories category = categoriesService.findById(id);
        if (category != null) {
            model.addAttribute("category", category);

            model.addAttribute(fragmentContent, "adminDashboard/fragments/category :: editCategory");
        } else {
            model.addAttribute("errorMessage", "Category not found");
        }
        return adminDashboardPath;
    }

    @PostMapping("/categories/edit")
    public String editCategory(
            @RequestParam("id") int id,
            @RequestParam("name") String name,
            Model model) {

        Categories category = categoriesService.findById(id);

        if (category == null) {
            model.addAttribute("errorMessage", "Category not found");
        } else if (categoriesService.existsByName(name) && !category.getName().equals(name)) {
            model.addAttribute("errorMessage", "Another category with this name already exists");
        } else if (category.getName().equalsIgnoreCase(name.trim())) {
            model.addAttribute("errorMessage", "Không có thay đổi nào được thực hiện");
        } else {
            categoriesService.updateCategory(id, name.trim());
            model.addAttribute("successMessage", "Category updated successfully");
        }

        List<Categories> categories = categoriesService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute(fragmentContent, categoryList);

        return adminDashboardPath;
    }

}
