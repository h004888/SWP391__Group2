package com.OLearning.controller.category;

import com.OLearning.dto.CategoryDTO;
import com.OLearning.entity.Category;
import com.OLearning.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/category")
public class CategoryApiController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategory();
    }
}
