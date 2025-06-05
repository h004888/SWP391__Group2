package com.OLearning.service.category;

import com.OLearning.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoriesService {

    Category findByName(String name);

    Category findById(int id);

    boolean existsByName(String name);

    boolean existsById(int id);

    List<Category> findAll();

    Category save(Category category);

    void deleteById(int id);

    void delete(Category category);

    void updateCategory(int id, String name);

    List<Category> getListCategories();
}
