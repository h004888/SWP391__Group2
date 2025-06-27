package com.OLearning.mapper.course;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseAddDTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.Course;
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
        course.setDiscount(dto.getDiscount());
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return course;
    }
    //hien thi course
    public CourseDTO MapCourseDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(course.getCourseId());
        courseDTO.setCourseImg(course.getCourseImg());
        courseDTO.setTitle(course.getTitle());
        courseDTO.setCreatedAt(course.getCreatedAt());
        courseDTO.setUpdatedAt(course.getUpdatedAt());
        courseDTO.setPrice(course.getPrice());
        courseDTO.setDescription(course.getDescription());
        courseDTO.setCourseLevel(course.getCourseLevel());
        courseDTO.setDiscount(course.getDiscount());
        courseDTO.setStatus(course.getStatus());
        courseDTO.setIsFree(course.getIsFree());
        courseDTO.setVideoUrlPreview(course.getVideoUrlPreview());
        return courseDTO;
    }
    //save course basic
    public Course CourseBasic(AddCourseStep1DTO dto, Course course) {
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCreatedAt(LocalDateTime.now());
        course.setPrice(dto.getPrice());
        course.setCourseLevel(dto.getCourseLevel());
        return course;
    }
    //lay ve thong tin course basic khi previous step
    public AddCourseStep1DTO DraftStep1(Course course) {
        AddCourseStep1DTO dto = new AddCourseStep1DTO();
        dto.setId(course.getCourseId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategoryName(course.getCategory().getName());
        dto.setPrice(course.getPrice());
        dto.setCourseLevel(course.getCourseLevel());
        return dto;
    }
}
