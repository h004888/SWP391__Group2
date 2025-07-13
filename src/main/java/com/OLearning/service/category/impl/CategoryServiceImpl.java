package com.OLearning.service.category.impl;

import com.OLearning.dto.category.CategoryDTO;
import com.OLearning.entity.Category;
import com.OLearning.repository.CategoryRepository;
import com.OLearning.service.category.CategoryService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getListCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<Category> findTop5ByOrderByIdAsc() {
        return categoryRepository.findTop5ByOrderByIdAsc();
    }


    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }


    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Page<Category> filterAndSortCategories(String name, String sort, int page, int size) {
        Pageable pageable;
        
        // Only apply sorting if user explicitly chooses a sort option
        if (sort != null && !sort.trim().isEmpty()) {
            if (sort.equalsIgnoreCase("desc")) {
                pageable = PageRequest.of(page, size, Sort.by("name").descending());
            } else if (sort.equalsIgnoreCase("asc")) {
                pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            } else {
                // Invalid sort parameter, use default (no sorting)
                pageable = PageRequest.of(page, size);
            }
        } else {
            // No sorting applied, use default order (by ID)
            pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        }
        
        Page<Category> categoryPage;
        if (name != null && !name.trim().isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }
        
        // Ensure no duplicates and load courses properly
        List<Category> uniqueCategories = categoryPage.getContent().stream()
            .distinct()
            .toList();
        
        // Force loading of courses for each category to avoid lazy loading issues
        uniqueCategories.forEach(category -> {
            if (category.getCourses() != null) {
                category.getCourses().size(); // Force loading
            }
        });
        
        // Create a new Page with unique content
        return new PageImpl<>(uniqueCategories, pageable, categoryPage.getTotalElements());
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return categoryRepository.existsByNameAndIdNot(name, id);
    }
}