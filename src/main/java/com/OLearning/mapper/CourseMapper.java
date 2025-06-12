package com.OLearning.mapper;

import com.OLearning.dto.CourseDTO;
import com.OLearning.entity.Course;

import org.springframework.stereotype.Component;

@Component("courseMapper")
public class CourseMapper {

    public static CourseDTO toDTO(Course course) {
        if (course == null)
            return null;

        return new CourseDTO(
                course.getCourseId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.getDiscount(),
                course.getCourseImg(),
                course.getDuration(),
                course.getTotalLessons(),
                course.getTotalRatings(),
                course.getTotalStudentEnrolled(),
                course.getCreatedAt(),
                course.getUpdatedAt());
    }

    // Nếu cần từ DTO → Entity
    public static Course toEntity(CourseDTO dto) {
        if (dto == null)
            return null;

        Course course = new Course();
        course.setCourseId(dto.getCourseId());
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setPrice(dto.getPrice());
        course.setDiscount(dto.getDiscount());
        course.setCourseImg(dto.getCourseImg());
        course.setDuration(dto.getDuration());
        course.setTotalLessons(dto.getTotalLessons());
        course.setTotalRatings(dto.getTotalRatings());
        course.setTotalStudentEnrolled(dto.getTotalStudentEnrolled());
        course.setCreatedAt(dto.getCreatedAt());
        course.setUpdatedAt(dto.getUpdatedAt());
        return course;
    }
}
