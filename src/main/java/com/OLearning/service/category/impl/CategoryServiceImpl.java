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
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getListCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void delete(Category categories) {
        categoryRepository.delete(categories);
    }

    @Override
    public void deleteById(int id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(int id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
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
    public Category findById(int id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public Category save(Category categories) {
        return categoryRepository.save(categories);
    }

    @Transactional
    @Override
    public void updateCategory(int id, String name) {
        categoryRepository.updateCategory(id, name);
    }

    @Override
    public List<Category> findByNameContaining(String name) {
        return categoryRepository.findByNameContaining(name);
    }

    @Override
    public List<Category> filterCategories(String name, String select) {
        List<Category> categories;

        if (name == null || name.isEmpty()) {
            categories = categoryRepository.findAll();
        } else {
            categories = categoryRepository.findByNameContaining(name);
        }

        if (select != null) {
            if (select.equals("1")) {
                categories.sort(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER));
            } else if (select.equals("2")) {
                categories.sort(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER).reversed());
            }
        }

        return categories;
    }

    @Override
    public Page<Category> findByNameContaining(String name, Pageable pageable) {
        return categoryRepository.findByNameContaining(name, pageable);
    }

    @Override
    public List<Category> findTop5ByOrderByIdAsc() {
        return categoryRepository.findTop5ByOrderByIdAsc();
    }

    @Override
    public Page<Category> filterCategories(String name, String sort, Pageable pageable) {
        Sort sortObj = Sort.unsorted();
        if ("asc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by("name").ascending();
        } else if ("desc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by("name").descending();
        }
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);
        if (name != null && !name.trim().isEmpty()) {
            return categoryRepository.findByNameContainingIgnoreCase(name, sortedPageable);
        } else {
            return categoryRepository.findAll(sortedPageable);
        }
    }

}