package com.OLearning.service.category;

import com.OLearning.dto.category.CategoryDTO;
import com.OLearning.entity.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CategoryService {

    Category findByName(String name);

    Optional<Category> findById(Long id);

    boolean existsByName(String name);

    boolean existsById(Long id);

    List<Category> findAll();

    Page<Category> findAll(Pageable pageable);


    List<Category> findTop5ByOrderByIdAsc();

    Category save(Category categories);

    void deleteById(Long id);

    void delete(Category categories);

    void updateCategory(Long id, String name);

    List<Category> findByNameContaining(String name);

    List<Category> getListCategories();

    List<Category> filterCategories(String name, String select);

    Page<Category> findByNameContaining(String name, Pageable pageable);

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Category> filterCategories(String name, String sort, Pageable pageable);

}