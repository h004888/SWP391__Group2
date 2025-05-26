package com.OLearning.service.adminDashBoard.impl;

import com.OLearning.entity.Categories;
import com.OLearning.repository.adminDashBoard.CategoriesRepository;
import com.OLearning.service.adminDashBoard.CategoriesService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesServiceImpl implements CategoriesService {
    @Autowired
    private CategoriesRepository categoriesRepository;

    public List<Categories> getListCategories() {
        return categoriesRepository.findAll();
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

    @Override
    public List<Categories> findByNameContaining(String name) {
        return categoriesRepository.findByNameContaining(name);
    }

 
}
