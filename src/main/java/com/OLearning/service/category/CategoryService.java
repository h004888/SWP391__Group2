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

    Optional<Category> findById(Long id);

    List<Category> findAll();

    Page<Category> findAll(Pageable pageable);

    void deleteById(Long id);
    List<CategoryDTO> getAllCategory();

    List<Category> getListCategories();

    Page<Category> filterAndSortCategories(String name, String sort, int page, int size);

    List<Category> findTop5ByOrderByIdAsc();

    Category save(Category category);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Long id);
}