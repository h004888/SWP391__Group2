package com.OLearning.service.instructorDashBoard.impl;

import com.OLearning.dto.instructorDashboard.CategoryDTO;
import com.OLearning.entity.Categories;
import com.OLearning.repository.instructorDashBoard.InstructorCategoryRepo;
import com.OLearning.service.instructorDashBoard.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceimpl implements CategoryService {
    @Autowired
    private InstructorCategoryRepo instructorCategoryRepo;


    @Override
    public List<CategoryDTO> findAll() {
        List<Categories> listOfCategories = instructorCategoryRepo.findAll();
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        for (Categories category : listOfCategories) {
                CategoryDTO categoryDTO = new CategoryDTO();
                categoryDTO.setCategoryName(category.getName());
                categoryDTOList.add(categoryDTO);
        }
        return categoryDTOList;
    }
}
