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
    public void delete(Category categories) {
        categoryRepository.delete(categories);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
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
    public Optional<Category> findById(Long id) {
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
    public void updateCategory(Long id, String name) {
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
    public Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Page<Category> filterCategories(String name, String sort, Pageable pageable) {
        if (name == null || name.isEmpty()) {
            return categoryRepository.findAll(pageable);
        } else {
            return categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        }
    }
}