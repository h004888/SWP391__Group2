package com.OLearning.service.category.impl;

import com.OLearning.entity.Category;
import com.OLearning.repository.CategoriesRepository;
import com.OLearning.service.category.CategoryService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoriesRepository categoriesRepository;

    public List<Category> getListCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public void delete(Category categories) {
        categoriesRepository.delete(categories);
    }

    @Override
    public void deleteById(int id) {
        categoriesRepository.deleteById(id);
    }

    @Override
    public boolean existsById(int id) {
        return categoriesRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return categoriesRepository.existsByName(name);
    }

    @Override
    public List<Category> findAll() {
        return categoriesRepository.findAll();
    }

    @Override
    public Category findById(int id) {
        return categoriesRepository.findById(id);
    }

    @Override
    public Category findByName(String name) {
        return categoriesRepository.findByName(name);
    }

    @Override
    public Category save(Category categories) {
        return categoriesRepository.save(categories);
    }

    @Transactional
    @Override
    public void updateCategory(int id, String name) {
        categoriesRepository.updateCategory(id, name);
    }

    @Override
    public List<Category> findByNameContaining(String name) {
        return categoriesRepository.findByNameContaining(name);
    }

    @Override
    public List<Category> filterCategories(String name, String select) {
        List<Category> categories;

        if (name == null || name.isEmpty()) {
            categories = categoriesRepository.findAll();
        } else {
            categories = categoriesRepository.findByNameContaining(name);
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
        return categoriesRepository.findByNameContaining(name, pageable);
    }

    @Override
    public List<Category> findTop5ByOrderByIdAsc() {
        return categoriesRepository.findTop5ByOrderByIdAsc();
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoriesRepository.findAll(pageable);
    }
}