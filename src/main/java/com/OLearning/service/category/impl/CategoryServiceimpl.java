package com.OLearning.service.category.impl;

import com.OLearning.dto.instructorDashBoard.CategoryDTO;
import com.OLearning.entity.Categories;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.InstructorCategoryRepo;
import com.OLearning.repository.InstructorCourseRepo;
import com.OLearning.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceimpl implements CategoryService {
    @Autowired
    private InstructorCategoryRepo instructorCategoryRepo;
    @Autowired
    private InstructorCourseRepo instructorCourseRepo;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<CategoryDTO> findAll() {
        List<Categories> listOfCategories = instructorCategoryRepo.findAll();
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        for (Categories category : listOfCategories) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryName(category.getName());
            categoryDTO.setId(category.getId());
            categoryDTOList.add(categoryDTO);
        }
        return categoryDTOList;
    }

}
