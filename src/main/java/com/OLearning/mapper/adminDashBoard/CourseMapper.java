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
                course.getTotalStudentEnrolled(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                course.getIsChecked()
        );
    }

    public void updateEntityFromDTO(CourseDTO dto, Course course) {
        if (dto == null || course == null) return;

        course.setTitle(dto.getTitle());
        course.setDuration(dto.getDuration());
        course.setTotalStudentEnrolled(dto.getTotalStudentEnrolled());
        course.setIsChecked(dto.getIsChecked());
        course.setUpdatedAt(java.time.LocalDateTime.now());
    }
}
