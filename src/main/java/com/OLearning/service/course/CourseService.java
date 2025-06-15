package com.OLearning.service.course;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseMediaDTO;
import com.OLearning.entity.Course;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CourseService {
    List<CourseDTO> findCourseByUserId(Long userId);
    void deleteCourse(Long courseId);
    Course findCourseById(Long courseId);
    Course createCourseStep1(Long courseId, AddCourseStep1DTO addCourseStep1DTO);
    AddCourseStep1DTO draftCourseStep1(Course course);
    Course submitCourse(Long courseId, String status);
    Page<CourseDTO> findCourseByUserId(Long userId, int page, int size);
    Course createCourseMedia(Long courseId, CourseMediaDTO CourseMediaDTO);
    void saveCourse(Long courseId);
}
