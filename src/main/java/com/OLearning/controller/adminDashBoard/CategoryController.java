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
        Pageable pageable = PageRequest.of(page, size,
                sort.equals("asc") ? Sort.by("name").ascending()
                        : sort.equals("desc") ? Sort.by("name").descending()
                        : Sort.unsorted());

        Page<Category> categoriesPage = categoryService.findByNameContaining(name, pageable);

        model.addAttribute("category", categoriesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoriesPage.getTotalPages());
        model.addAttribute("totalItems", categoriesPage.getTotalElements());

    }

    @GetMapping("/category/showmore")
    public String showmore(@RequestParam("id") int id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        model.addAttribute("coursesList", category.getCourses());
        model.addAttribute(fragmentContent, "adminDashBoard/fragments/showMore :: showMore");
        return "adminDashBoard/index";

    }

    @GetMapping("/category")
    public String getAllCategories(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model) {

        loadPagedCategories(model, name, sort, page, size);

        if ("XMLHttpRequest".equals(requestedWith)) {
            // AJAX request → trả về fragment

            return categoryPagination;
        } else {
            // Request thường → load layout chính
            model.addAttribute(fragmentContent, categoryList);
            return adminDashBoardPath;
        }
    }

    @GetMapping("/category/delete")
    public String deleteCategory(@RequestParam("id") int id, Model model) {
        if (categoryService.existsById(id) ) {
            categoryService.deleteById(id);
        }
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return categoryPagination;
    }

    @PostMapping("/category/add")
    public String addCategory(@RequestParam("name") String name,
                              @RequestParam(defaultValue = "") String sort,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
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
        model.addAttribute(fragmentContent, categoryList);
        return adminDashBoardPath;
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
                               Model model) {
        Category category = categoryService.findById(id);

        if (category == null) {
            model.addAttribute("errorMessage", "Category not found");
        } else if (categoryService.existsByName(name) && !category.getName().equals(name)) {
            model.addAttribute("errorMessage", "Another category with this name already exists");
        } else if (category.getName().equalsIgnoreCase(name.trim())) {
            model.addAttribute("errorMessage", "Không có thay đổi nào được thực hiện");
        } else {
            categoryService.updateCategory(id, name.trim());
            model.addAttribute("successMessage", "Category updated successfully");
        }

        loadPagedCategories(model, search, sort, page, 5);
        model.addAttribute(fragmentContent, categoryList);
        return adminDashBoardPath;
    }

}