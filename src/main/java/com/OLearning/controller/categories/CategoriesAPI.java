package com.OLearning.controller.categories;

import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.OLearning.entity.Categories;
import com.OLearning.service.categories.CategoriesService;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesAPI {
    private final CategoriesService categoriesService;

    public CategoriesAPI(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping
    public ResponseEntity<List<Categories>> getAllCategories() {
        List<Categories> categories = categoriesService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categories> getCategoryById(@PathVariable int id) {
        Categories category = categoriesService.findById(id);
        System.out.println(category);
        if (category != null) {
            return ResponseEntity.ok(category);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Categories> createCategory(@RequestBody Categories category) {
        if (categoriesService.existsByName(category.getName())) {
            return ResponseEntity.status(Response.SC_BAD_REQUEST).body(null);
        }
        Categories createdCategory = categoriesService.save(category);
        return ResponseEntity.status(Response.SC_CREATED).body(createdCategory);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        if (categoriesService.existsById(id)) {
            categoriesService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
