package com.OLearning.service.instructorDashBoard;

import com.OLearning.dto.instructorDashboard.*;
import com.OLearning.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<CourseDTO> findCourseByUserId(Long userId);
    void deleteCourse(Long courseId);
    Course findCourseById(Long courseId);
    Course createCourseStep1(Long courseId, AddCourseStep1DTO addCourseStep1DTO);
    Course createCourseStep2(Long courseId);
    AddCourseStep1DTO draftCourseStep1(Course course);
    Course createCourseStep3(Long courseId, AddCourseStep3DTO addCourseStep3DTO);
    Course submitCourse(Long courseId, String status);
}
