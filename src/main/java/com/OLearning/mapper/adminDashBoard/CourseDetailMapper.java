package com.OLearning.mapper.adminDashBoard;

import com.OLearning.dto.adminDashBoard.CourseDetailDTO;
import com.OLearning.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseDetailMapper {

    public CourseDetailDTO toDTO(Course course) {
        if (course == null) return null;

        CourseDetailDTO dto = new CourseDetailDTO();
        dto.setCourseId(course.getCourseId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setPrice(course.getPrice());
        dto.setDiscount(course.getDiscount());
        dto.setCourseImg(course.getCourseImg());
        dto.setDuration(course.getDuration());
        dto.setTotalLessons(course.getTotalLessons());
        dto.setTotalRatings(course.getTotalRatings());
        dto.setTotalStudentEnrolled(course.getTotalStudentEnrolled());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        dto.setStatus(course.getStatus());
        dto.setInstructor(course.getInstructor());
        dto.setCategory(course.getCategory());
        return dto;
    }


}
