package com.OLearning.service.instructorDashboard;

import com.OLearning.dto.instructorDashboard.CategoryDTO;
import com.OLearning.dto.instructorDashboard.CourseAddDTO;
import com.OLearning.dto.instructorDashboard.CourseDTO;
import com.OLearning.entity.Course;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> findAll();

}
