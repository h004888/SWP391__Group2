package com.OLearning.mapper.adminDashBoard;

import com.OLearning.dto.adminDashBoard.CourseDTO;
import com.OLearning.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
    public CourseDTO toDTO(Course course) {
        if (course == null) return null;

        return new CourseDTO(
                course.getCourseId(),
                course.getTitle(),
                course.getDuration(),
                course.getPrice(),
                course.getTotalLessons(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                course.getStatus(),
                course.getCategory()
        );
    }

}
