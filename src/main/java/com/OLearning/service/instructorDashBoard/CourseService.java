package com.OLearning.service.instructorDashBoard;

import com.OLearning.dto.instructorDashboard.CourseAddDTO;
import com.OLearning.dto.instructorDashboard.CourseDTO;
import com.OLearning.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<CourseDTO> findCourseByUserId(Long userId);
    boolean canCreateCourse(Long userId);
    void createCourse(CourseAddDTO courseAddDTO);
    void deleteCourse(Long courseId);
    Course findCourseById(Long courseId);
}
