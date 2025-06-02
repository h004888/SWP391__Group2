package com.OLearning.service.instructorDashboard.impl;

import com.OLearning.dto.instructorDashboard.CategoryDTO;
import com.OLearning.dto.instructorDashboard.CourseAddDTO;
import com.OLearning.dto.instructorDashboard.CourseDTO;
import com.OLearning.entity.Categories;
import com.OLearning.entity.Course;
import com.OLearning.mapper.instructorDashBoard.CourseMapper;
import com.OLearning.repository.instructorDashBoard.InstructorCategoryRepo;
import com.OLearning.repository.instructorDashBoard.InstructorCourseRepo;
import com.OLearning.service.instructorDashboard.CategoryService;
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
