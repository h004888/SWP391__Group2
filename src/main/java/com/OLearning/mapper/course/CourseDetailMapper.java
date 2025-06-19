package com.OLearning.mapper.course;

import com.OLearning.dto.course.CourseDetailDTO;
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
        dto.setPrice(course.getPrice().doubleValue());
        dto.setDiscount(course.getDiscount().doubleValue());
        dto.setCourseImg(course.getCourseImg());
        dto.setDuration(course.getDuration());
        dto.setTotalLessons(course.getTotalLessons());
        dto.setTotalRatings(course.getTotalRatings());
        dto.setTotalStudentEnrolled(course.getTotalStudentEnrolled());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        dto.setInstructor(course.getInstructor());
        dto.setCategory(course.getCategory());
        dto.setStatus(course.getStatus());
        dto.setCanResubmit(course.getCanResubmit());
        return dto;
    }


}