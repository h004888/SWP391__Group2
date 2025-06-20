package com.OLearning.controller.adminDashBoard;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.OLearning.entity.Category;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.course.CourseService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class CategoryController {
    private static final String adminDashBoardPath = "adminDashBoard/index";
    private static final String fragmentContent = "fragmentContent";
    private static final String categoryList = "adminDashBoard/fragments/category :: categoryList";
    private static final String categoryPagination = "adminDashBoard/fragments/category :: categoryPage";

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CourseService courseService;

    private void loadPagedCategories(Model model, String name, String sort, int page, int size) {
        // Handle null or empty name parameter
        String searchName = (name != null && !name.trim().isEmpty()) ? name.trim() : "";

        Pageable pageable = PageRequest.of(page, size,
                sort != null && sort.equals("asc") ? Sort.by("name").ascending()
                        : sort != null && sort.equals("desc") ? Sort.by("name").descending()
                                : Sort.unsorted());

        Page<Category> categoriesPage;

        if (searchName.isEmpty()) {
            categoriesPage = categoryService.findAll(pageable);
        } else {
            categoriesPage = categoryService.findByNameContaining(searchName, pageable);
        }

        model.addAttribute("category", categoriesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoriesPage.getTotalPages());
        model.addAttribute("totalItems", categoriesPage.getTotalElements());
        model.addAttribute("sort", sort != null ? sort : "");
        model.addAttribute("name", searchName);
        model.addAttribute("pageSize", size);
    }

    @GetMapping("/category")
    public String getAllCategories(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryService.filterCategories(name, sort, pageable);

        model.addAttribute("category", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/category :: categoryList");
        return "adminDashBoard/index";

    }

    @GetMapping("/category/delete")
    @ResponseBody
    public String deleteCategory(@RequestParam("id") int id, Model model) {
        try {
            if (categoryService.existsById(id)) {
                categoryService.deleteById(id);
                return "success";
            }
            return "error";
        } catch (Exception e) {
            return "error";
        }
    }

    @PostMapping("/category/add")
    public String addCategory(@RequestParam("name") String name,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {

        if (categoryService.existsByName(name)) {
            model.addAttribute("errorMessage", "Category already exists");
        } else {
            Category newCategory = new Category();
            newCategory.setName(name.trim());
            categoryService.save(newCategory);
            model.addAttribute("successMessage", "Category added successfully");
        }

        loadPagedCategories(model, search, sort, page, size);

        if ("XMLHttpRequest".equals(requestedWith)) {
            return categoryPagination;
        } else {
            model.addAttribute(fragmentContent, categoryList);
            return adminDashBoardPath;
        }
    }

    @GetMapping("/category/edit")
    public String editCategory(@RequestParam("id") int id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "") String name,
            Model model) {
        Category category = categoryService.findById(id);
        if (category != null) {
            model.addAttribute("category", category);
            model.addAttribute("currentPage", page);
            model.addAttribute("sort", sort);
            model.addAttribute("name", name);
            model.addAttribute(fragmentContent, "adminDashBoard/fragments/category :: editCategory");
        } else {
            model.addAttribute("errorMessage", "Category not found");
        }
        return adminDashBoardPath;
    }

    @PostMapping("/category/edit")
    public String editCategory(@RequestParam("id") int id,
            @RequestParam("name") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "") String search,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {
        Category category = categoryService.findById(id);

        if (category == null) {
            model.addAttribute("errorMessage", "Category not found");
        } else if (categoryService.existsByName(name) && !category.getName().equals(name)) {
            model.addAttribute("errorMessage", "Another category with this name already exists");
        } else if (category.getName().equalsIgnoreCase(name.trim())) {
            model.addAttribute("errorMessage", "No changes were made");
        } else {
            categoryService.updateCategory(id, name.trim());
            model.addAttribute("successMessage", "Category updated successfully");
        }

        loadPagedCategories(model, search, sort, page, 5);

        if ("XMLHttpRequest".equals(requestedWith)) {
            return categoryPagination;
        } else {
            model.addAttribute(fragmentContent, categoryList);
            return adminDashBoardPath;
        }
    }

    @GetMapping("/category/filter")
    public String filterCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryService.filterCategories(name, sort, pageable);

        model.addAttribute("category", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "adminDashBoard/fragments/category :: categoryTableFragment";
    }
}