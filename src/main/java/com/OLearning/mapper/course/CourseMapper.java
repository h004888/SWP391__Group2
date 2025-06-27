package com.OLearning.mapper.course;

import com.OLearning.dto.course.CourseViewDTO;
import com.OLearning.entity.Course;
import org.springframework.stereotype.Component;

@Component("courseMapper")
public class CourseMapper {
    // course to CourseViewDTO
    public static CourseViewDTO toCourseViewDTO(Course course) {
        if (course == null) {
            return null;
        }
        CourseViewDTO dto = new CourseViewDTO();
        dto.setCourseId(course.getCourseId());
        dto.setIsFree(course.getIsFree());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setPrice(course.getPrice());
        dto.setDiscount(course.getDiscount());
        dto.setCourseImg(course.getCourseImg());
        dto.setCourseLevel(course.getCourseLevel());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        dto.setStatus(course.getStatus());
        dto.setCanResubmit(course.getCanResubmit());
        dto.setVideoUrlPreview(course.getVideoUrlPreview());
        dto.setInstructor(course.getInstructor());
        dto.setCategory(course.getCategory());
        dto.setListOfChapters(course.getListOfChapters());
        dto.setEnrollments(course.getEnrollments());
        dto.setCourseReviews(course.getCourseReviews());
        // Calculate average rating
        dto.setAverageRating(course.getCourseReviews().stream()
                .mapToDouble(review -> review.getRating())
                .average()
                .orElse(0.0));
        // Calculate total duration
        dto.setDuration(course.getListOfChapters().stream()
                .flatMap(chapter -> chapter.getLessons().stream())
                .mapToInt(lesson -> lesson.getDuration())
                .sum());
        // Calculate total lessons
        dto.setTotalLessons(course.getListOfChapters().stream()
                .flatMap(chapter -> chapter.getLessons().stream())
                .count());
        return dto;
    }

}
