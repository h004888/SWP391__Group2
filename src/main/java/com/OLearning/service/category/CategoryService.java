package com.OLearning.service.category;

import com.OLearning.entity.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.OLearning.dto.category.CategoryDTO;

import java.util.List;

@Service
public interface CategoryService {

    Category findByName(String name);

    Category findById(int id);

    boolean existsByName(String name);

    boolean existsById(int id);

    List<Category> findAll();

    Page<Category> findAll(Pageable pageable);

    List<Category> findTop5ByOrderByIdAsc();

    Category save(Category categories);

    void deleteById(int id);

    void delete(Category categories);

    void updateCategory(int id, String name);

    List<Category> findByNameContaining(String name);

    List<Category> getListCategories();

    List<Category> filterCategories(String name, String select);

    Page<Category> findByNameContaining(String name, Pageable pageable);

    Page<Category> filterCategories(String name, String sort, Pageable pageable);
}