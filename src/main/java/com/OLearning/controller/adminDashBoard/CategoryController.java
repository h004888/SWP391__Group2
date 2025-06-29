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
import com.OLearning.entity.Course;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.course.CourseService;
import com.OLearning.dto.course.CourseDTO;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CourseService courseService;

    @GetMapping("/category")
    public String getAllCategories(Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "5") int size,
                                   @RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "sort", required = false) String sort) {
        
        // Clean up parameters - ensure empty string is treated as null for sort
        String cleanName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        String cleanSort = (sort != null && !sort.trim().isEmpty()) ? sort.trim() : null;
        
        Page<Category> categoryPage = categoryService.filterAndSortCategories(cleanName, cleanSort, page, size);
        
        model.addAttribute("category", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("name", cleanName);
        model.addAttribute("sort", cleanSort);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/category :: categoryList");
        return "adminDashBoard/index";
    }

    // Filter with ajax for pagination functionality
    @GetMapping("/category/filter")
    public String filterCategories(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {
        
        // Clean up parameters
        String cleanName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        String cleanSort = (sort != null && !sort.trim().isEmpty()) ? sort.trim() : null;
        
        Page<Category> categoryPage = categoryService.filterAndSortCategories(cleanName, cleanSort, page, size);
        model.addAttribute("category", categoryPage.getContent());
        return "adminDashBoard/fragments/category :: categoryTableRowContent";
    }

    // New endpoint for pagination only
    @GetMapping("/category/pagination")
    public String getPagination(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {
        
        // Clean up parameters
        String cleanName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        String cleanSort = (sort != null && !sort.trim().isEmpty()) ? sort.trim() : null;
        
        Page<Category> categoryPage = categoryService.filterAndSortCategories(cleanName, cleanSort, page, size);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "adminDashBoard/fragments/category :: categoryPagination";
    }

    @GetMapping("/category/count")
    @ResponseBody
    public long getCategoryCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sort", required = false) String sort) {
        
        Page<Category> categoryPage = categoryService.filterAndSortCategories(name, sort, 0, 1); // Get only 1 item to check total
        return categoryPage.getTotalElements();
    }

    @DeleteMapping("/category/delete/{id}")
    @ResponseBody
    public String deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteById(id);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    // Edit category functionality
    @GetMapping("/category/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "sort", required = false) String sort,
                              @RequestParam(value = "name", required = false) String search) {
        
        Category category = categoryService.findById(id).orElse(null);
        if (category == null) {
            return "redirect:/admin/category";
        }
        
        model.addAttribute("category", category);
        model.addAttribute("currentPage", page);
        model.addAttribute("sort", sort);
        model.addAttribute("name", search);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/category :: editCategory");
        return "adminDashBoard/index";
    }

    @PostMapping("/category/edit")
    public String updateCategory(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "sort", required = false) String sort,
                                @RequestParam(value = "search", required = false) String search,
                                RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.findById(id).orElse(null);
            if (category == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Category not found");
                return "redirect:/admin/category";
            }
            
            // Check if name already exists for other categories
            if (categoryService.existsByNameAndIdNot(name, id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Category name already exists");
                return "redirect:/admin/category/edit/" + id + "?page=" + page + "&sort=" + sort + "&name=" + search;
            }
            
            category.setName(name);
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating category: " + e.getMessage());
        }
        
        return "redirect:/admin/category?page=" + page + "&sort=" + sort + "&name=" + search;
    }

    // Add category functionality
    @PostMapping("/category/add")
    public String addCategory(@RequestParam String name,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "sort", required = false) String sort,
                             @RequestParam(value = "name", required = false) String search,
                             RedirectAttributes redirectAttributes) {
        try {
            if (categoryService.existsByName(name)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Category name already exists");
                return "redirect:/admin/category?page=" + page + "&sort=" + sort + "&name=" + search;
            }
            
            Category category = new Category();
            category.setName(name);
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category added successfully");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding category: " + e.getMessage());
        }
        
        return "redirect:/admin/category?page=" + page + "&sort=" + sort + "&name=" + search;
    }

    @GetMapping("/category/{id}/courses")
    public String getCoursesByCategory(@PathVariable Long id, Model model) {
        List<CourseDTO> courses = courseService.getCourseDTOsByCategoryId(id);
        Category category = categoryService.findById(id).orElse(null);
        
        model.addAttribute("courses", courses);
        model.addAttribute("category", category);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/category :: courseListContent");
        return "adminDashBoard/index";
    }
}