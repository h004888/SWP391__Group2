package com.OLearning.mapper.course;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.Course;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("courseMapper")
public class CourseMapper {

    public static CourseDTO toDTO(Course course) {
        if (course == null)
            return null;

        return new CourseDTO(
                course.getCourseId(),
                course.getTitle(),
                course.getDuration(),
                course.getPrice().doubleValue(),
                course.getTotalLessons(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                course.getStatus(),
                course.getCategory(),
                course.getDescription() // Giả sử bạn có trường mô tả trong Course
        );
    }

    // Nếu cần từ DTO → Entity
    public static Course toEntity(CourseDTO dto) {
        if (dto == null)
            return null;

        Course course = new Course();
        course.setCourseId(dto.getCourseId());
        course.setTitle(dto.getTitle());
        course.setDuration(dto.getDuration());
        course.setPrice(BigDecimal.valueOf(dto.getPrice()));
        course.setTotalLessons(dto.getTotalLessons());
        course.setCreatedAt(dto.getCreatedAt());
        course.setUpdatedAt(dto.getUpdatedAt());
        course.setStatus(dto.getStatus());
        course.setCategory(dto.getCategory());
        return course;
    }
}