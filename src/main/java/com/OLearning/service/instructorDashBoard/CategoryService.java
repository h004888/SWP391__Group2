package com.OLearning.service.instructorDashBoard;

import com.OLearning.dto.instructorDashboard.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> findAll();
}
