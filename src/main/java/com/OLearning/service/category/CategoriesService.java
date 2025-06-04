package com.OLearning.service.category;

import com.OLearning.entity.Categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoriesService {

    Categories findByName(String name);

    Categories findById(int id);

    boolean existsByName(String name);

    boolean existsById(int id);

    List<Categories> findAll();

    Categories save(Categories categories);

    void deleteById(int id);

    void delete(Categories categories);

    void updateCategory(int id, String name);

    List<Categories> findByNameContaining(String name);

    List<Categories> getListCategories();

    List<Categories> filterCategories(String name, String select);

    Page<Categories> findByNameContaining(String name, Pageable pageable);

    int countNumberCoursesByCategoryId(int categoryId);
    int countNumberEnrollmentsByCategoryId(int categoryId);
}
