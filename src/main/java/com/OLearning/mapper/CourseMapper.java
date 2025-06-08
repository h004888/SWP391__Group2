package com.OLearning.mapper;

import com.OLearning.dto.CourseDTO;
import com.OLearning.entity.Course;
import org.springframework.stereotype.Component;

@Component("adminCourseMapper")
public class CourseMapper {
    public CourseDTO toDTO(Course course) {
        if (course == null)
            return null;

        return new CourseDTO(
                course.getCourseId(),
                course.getTitle(),
                course.getDuration(),
                course.getPrice(),
                course.getTotalLessons(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                course.getCategory());
    }

}
