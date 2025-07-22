package com.OLearning.controller.adminDashBoard;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private static final String ERROR_CATEGORY_NOT_FOUND = "Category not found";
    private static final String ERROR_CATEGORY_NAME_EXISTS = "Category name already exists";
    private static final String SUCCESS_CATEGORY_UPDATED = "Category updated successfully";
    private static final String ERROR_CATEGORY_UPDATE = "Error updating category: ";
    private static final String SUCCESS_CATEGORY_ADDED = "Category added successfully";
    private static final String ERROR_CATEGORY_ADD = "Error adding category: ";

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
        
        //ensure empty string is treated as null for sort
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

    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Long id,Model model) {
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/category :: categoryList");
        categoryService.deleteById(id);
        return "adminDashBoard/index";
    }

    @PostMapping("/category/edit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCategory(@RequestParam Long id,
                                                             @RequestParam String name) {
        try {
            Category category = categoryService.findById(id).orElse(null);
            if (category == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ERROR_CATEGORY_NOT_FOUND));
            }
            
            // Check if name already exists for other categories
            if (categoryService.existsByNameAndIdNot(name, id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", ERROR_CATEGORY_NAME_EXISTS));
            }
            
            category.setName(name);
            categoryService.save(category);
            
            return ResponseEntity.ok(Map.of("success", SUCCESS_CATEGORY_UPDATED));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ERROR_CATEGORY_UPDATE + e.getMessage()));
        }
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
                redirectAttributes.addFlashAttribute("errorMessage", ERROR_CATEGORY_NAME_EXISTS);
                return "redirect:/admin/category?page=" + page + "&sort=" + sort + "&name=" + search;
            }
            
            Category category = new Category();
            category.setName(name);
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_CATEGORY_ADDED);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_CATEGORY_ADD + e.getMessage());
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