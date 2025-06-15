package com.OLearning.service.category.impl;

import com.OLearning.dto.category.CategoryDTO;
import com.OLearning.entity.Category;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.CategoryRepository;
import com.OLearning.repository.CourseRepository;
import com.OLearning.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceimpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<CategoryDTO> findAll() {
        List<Category> listOfCategories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        for (Category category : listOfCategories) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryName(category.getName());
            categoryDTO.setId(category.getId());
            categoryDTOList.add(categoryDTO);
        }
        return categoryDTOList;
    }

}
