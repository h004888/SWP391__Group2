package com.OLearning.mapper.course;

import com.OLearning.dto.course.CourseDTO;
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
                course.getCreatedAt(),
                course.getUpdatedAt(),
                course.getCourseLevel(),
                course.getAverageRating(),  // Tính từ method trong entity
                course.getReviewCount(),    // Tính từ method trong entity
                course.getDuration()        // Tính từ method trong entity
        );
    }

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
        course.setCreatedAt(dto.getCreatedAt());
        course.setUpdatedAt(dto.getUpdatedAt());
        course.setCourseLevel(dto.getCourseLevel());
        // Không set averageRating, reviewCount, duration vì tính tự động
        return course;
    }
}
