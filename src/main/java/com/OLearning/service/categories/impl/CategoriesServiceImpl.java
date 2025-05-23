package com.OLearning.service.categories.impl;

import java.util.*;

import org.springframework.stereotype.Service;

import com.OLearning.entity.Categories;
import com.OLearning.repository.categories.CategoriesRepository;
import com.OLearning.service.categories.CategoriesService;

import jakarta.transaction.Transactional;

@Service
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;

    public CategoriesServiceImpl(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public void delete(Categories categories) {
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
    public List<Categories> findAll() {
        return categoriesRepository.findAll();
    }

    @Override
    public Categories findById(int id) {
        return categoriesRepository.findById(id);
    }

    @Override
    public Categories findByName(String name) {
        return categoriesRepository.findByName(name);
    }

    @Override
    public Categories save(Categories categories) {
        return categoriesRepository.save(categories);
    }

    @Transactional
    @Override
    public void updateCategory(int id, String name) {
        categoriesRepository.updateCategory(id, name);
    }
}