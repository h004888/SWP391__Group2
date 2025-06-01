package com.OLearning.service.instructorDashBoard;

import com.OLearning.dto.instructorDashboard.CourseAddDTO;
import com.OLearning.dto.instructorDashboard.CourseDTO;
import com.OLearning.entity.Course;

import java.util.List;

public interface CourseService {
    List<CourseDTO> findCourseByUserId(Long userId);
    //boolean canCreateCourse(Long userId);
    void createCourse(CourseAddDTO courseAddDTO);
}
