package com.OLearning.service.instructorDashBoard;

import com.OLearning.dto.instructorDashBoard.CategoryDTO;
import com.OLearning.dto.instructorDashBoard.CourseAddDTO;
import com.OLearning.dto.instructorDashBoard.CourseDTO;
import com.OLearning.entity.Course;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> findAll();

}
