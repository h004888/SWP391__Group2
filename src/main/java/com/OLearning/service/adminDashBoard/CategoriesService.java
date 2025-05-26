package com.OLearning.service.adminDashBoard;

import com.OLearning.entity.Categories;
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
}
