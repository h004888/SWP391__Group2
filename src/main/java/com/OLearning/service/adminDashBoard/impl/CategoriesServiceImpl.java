package com.OLearning.service.adminDashBoard.impl;

import com.OLearning.entity.Categories;
import com.OLearning.repository.adminDashBoard.CategoriesRepository;
import com.OLearning.service.adminDashBoard.CategoriesService;
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

    @Override
    public List<Categories> filterCategories(String name, String select) {
        List<Categories> categories;

        if (name == null || name.isEmpty()) {
            categories = categoriesRepository.findAll();
        } else {
            categories = categoriesRepository.findByNameContaining(name);
        }

        if (select != null) {
            if (select.equals("1")) {
                categories.sort(Comparator.comparing(Categories::getName, String.CASE_INSENSITIVE_ORDER));
            } else if (select.equals("2")) {
                categories.sort(Comparator.comparing(Categories::getName, String.CASE_INSENSITIVE_ORDER).reversed());
            }
        }

        return categories;
    }

    @Override
    public Page<Categories> findAllCategory(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(sortBy != null ? sortBy : "id");
        sort = "desc".equalsIgnoreCase(sortDirection) ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoriesRepository.findAll(pageable);
    }

    @Override
    public Page<Categories> findByNameContaining(String keyword, int page, int size, String sortBy,
            String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoriesRepository.findAll(pageable);

        }
        return categoriesRepository.findByNameContaining(keyword, pageable);
    }

}
