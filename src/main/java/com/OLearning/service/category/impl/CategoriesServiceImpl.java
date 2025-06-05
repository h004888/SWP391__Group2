package com.OLearning.service.category.impl;

import com.OLearning.entity.Category;
import com.OLearning.repository.CategoriesRepository;
import com.OLearning.service.category.CategoriesService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesServiceImpl implements CategoriesService {
    @Autowired
    private CategoriesRepository categoriesRepository;

    public List<Category> getListCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public void delete(Category category) {
        categoriesRepository.delete(category);
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
    public Category save(Category category) {
        return categoriesRepository.save(category);
    }

    @Transactional
    @Override
    public void updateCategory(int id, String name) {
        categoriesRepository.updateCategory(id, name);
    }
}
