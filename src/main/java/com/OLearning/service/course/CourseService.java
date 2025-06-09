package com.OLearning.service.course;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.AddCourseStep3DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.Course;

import java.util.List;

public interface CourseService {
    List<CourseDTO> findCourseByUserId(Long userId);
    void deleteCourse(Long courseId);
    Course findCourseById(Long courseId);
    Course createCourseStep1(Long courseId, AddCourseStep1DTO addCourseStep1DTO);
    Course createCourseStep2(Long courseId);
    AddCourseStep1DTO draftCourseStep1(Course course);
    Course createCourseStep3(Long courseId, AddCourseStep3DTO addCourseStep3DTO);
    Course createCourseStep1Demo(Long courseId, AddCourseStep1DTO addCourseStep1DTO);
    Course submitCourse(Long courseId, String status);
}
