package com.OLearning.mapper;

import com.OLearning.dto.CourseDTO;
import com.OLearning.entity.Course;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("adminCourseMapper")
public class CourseMapper {
    public CourseDTO toDTO(Course course) {
        if (course == null) {
            return null;
        }
        CourseDTO courseDTO = new CourseDTO(
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

        return courseDTO;
    }

}
