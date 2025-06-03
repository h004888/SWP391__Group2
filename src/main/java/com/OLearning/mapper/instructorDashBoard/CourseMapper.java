package com.OLearning.mapper.instructorDashBoard;

import com.OLearning.dto.instructorDashboard.*;
import com.OLearning.entity.Categories;
import com.OLearning.entity.Course;
import com.OLearning.repository.instructorDashBoard.InstructorCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CourseMapper {
    // Mapper ve de save course
    public Course MapCourseAdd(CourseAddDTO dto) {
        Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setPrice(dto.getPrice());
        course.setTotalStudentEnrolled(0);
        course.setDiscount(dto.getDiscount());
        course.setTotalLessons(0);
        course.setTotalRatings(0);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        course.setDuration(0);
        return course;
    }
    public CourseDTO MapCourseDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(course.getCourseId());
        courseDTO.setCourseImg(course.getCourseImg());
        courseDTO.setTitle(course.getTitle());
        courseDTO.setCreatedAt(course.getCreatedAt());
        courseDTO.setPrice(course.getPrice());
        courseDTO.setDuration(course.getDuration());
        courseDTO.setDiscount(course.getDiscount());
        courseDTO.setTotalLessons(course.getTotalLessons());
        courseDTO.setStatus(course.getStatus());
        return courseDTO;
    }
    public Course Step1(AddCourseStep1DTO dto, Course course) {
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCreatedAt(LocalDateTime.now());
        return course;
    }
    public Course Step2(Course course) {
        course.setUpdatedAt(LocalDateTime.now());
        return course;
    }
    public Course Step3(AddCourseStep3DTO dto, Course course) {
        course.setPrice(dto.getPrice());
        course.setUpdatedAt(LocalDateTime.now());
        return course;
    }
    public AddCourseStep1DTO DraftStep1(Course course) {
        AddCourseStep1DTO dto = new AddCourseStep1DTO();
        dto.setId(course.getCourseId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategoryName(course.getCategory().getName());
        return dto;
    }
}
