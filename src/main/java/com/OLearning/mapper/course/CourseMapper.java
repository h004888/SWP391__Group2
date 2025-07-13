package com.OLearning.mapper.course;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseAddDTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseViewDTO;
import com.OLearning.entity.Course;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("courseMapper")
public class CourseMapper {
    // Mapper ve de save course
    public Course MapCourseAdd(CourseAddDTO dto) {
        Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setPrice(dto.getPrice() != null ? dto.getPrice() : null);
        course.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : null);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return course;
    }

    //hien thi course
    public CourseDTO MapCourseDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(course.getCourseId());
        courseDTO.setCourseImg(course.getCourseImg());
        courseDTO.setTitle(course.getTitle());
        courseDTO.setCreatedAt(course.getCreatedAt());
        courseDTO.setUpdatedAt(course.getUpdatedAt());
        courseDTO.setPrice(course.getPrice());
        courseDTO.setDescription(course.getDescription());
        courseDTO.setCourseLevel(course.getCourseLevel());
        courseDTO.setDiscount(course.getDiscount());
        courseDTO.setStatus(course.getStatus());
        if (course.getCategory() != null) {
            courseDTO.setCategoryName(course.getCategory().getName());
        } else {
            courseDTO.setCategoryName("N/A");
        }
        courseDTO.setInstructor(course.getInstructor());
        courseDTO.setTotalStudentEnrolled(course.getEnrollments() != null ? course.getEnrollments().size() : 0);
        courseDTO.setIsFree(course.getIsFree());
        courseDTO.setVideoUrlPreview(course.getVideoUrlPreview());
        return courseDTO;
    }
    //save course basic
    public Course CourseBasic(AddCourseStep1DTO dto, Course course) {
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCreatedAt(LocalDateTime.now());
        course.setPrice(dto.getPrice() != null ? (dto.getPrice()) : null);
        course.setCourseLevel(dto.getCourseLevel());
        return course;

    }

    // course to CourseViewDTO
    public static CourseViewDTO toCourseViewDTO(Course course) {
        if (course == null) {
            return null;
        }
        CourseViewDTO dtoView = new CourseViewDTO();
            dtoView.setCourseId(course.getCourseId());
            dtoView.setIsFree(course.getIsFree());
            dtoView.setTitle(course.getTitle());
        dtoView.setDescription(course.getDescription());
        dtoView.setPrice(course.getPrice());
        dtoView.setDiscount(course.getDiscount());
        dtoView.setCourseImg(course.getCourseImg());
        dtoView.setCourseLevel(course.getCourseLevel());
        dtoView.setCreatedAt(course.getCreatedAt());
        dtoView.setUpdatedAt(course.getUpdatedAt());
        dtoView.setStatus(course.getStatus());
        dtoView.setCanResubmit(course.getCanResubmit());
        dtoView.setVideoUrlPreview(course.getVideoUrlPreview());
        dtoView.setInstructor(course.getInstructor());
        dtoView.setCategory(course.getCategory());
        dtoView.setListOfChapters(course.getListOfChapters());
        dtoView.setEnrollments(course.getEnrollments());
        dtoView.setCourseReviews(course.getCourseReviews());
        // Calculate average rating
        dtoView.setAverageRating(course.getCourseReviews().stream()
                .filter(review -> review.getRating() != null)
                .mapToDouble(review -> review.getRating())
                .average()
                .orElse(0.0));
        // Calculate total duration
        dtoView.setDuration(course.getListOfChapters().stream()
                .flatMap(chapter -> chapter.getLessons().stream())
                .mapToInt(lesson -> lesson.getDuration() != null ? lesson.getDuration() : 0)
                .sum());
        // Calculate total lessons
            dtoView.setTotalLessons(course.getListOfChapters().stream()
                .flatMap(chapter -> chapter.getLessons().stream())
                .count());
        return dtoView;
    }

    //lay ve thong tin course basic khi previous step
    public AddCourseStep1DTO DraftStep1(Course course) {
        AddCourseStep1DTO dto = new AddCourseStep1DTO();
        dto.setId(course.getCourseId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategoryName(course.getCategory().getName());
        dto.setPrice(course.getPrice() != null ? course.getPrice().doubleValue() : null);
        dto.setCourseLevel(course.getCourseLevel());
        return dto;
    }

    public static CourseDTO toDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(course.getCourseId());
        courseDTO.setTitle(course.getTitle());
        courseDTO.setDescription(course.getDescription());
        courseDTO.setPrice(course.getPrice());
        courseDTO.setDiscount(course.getDiscount());
        courseDTO.setCourseImg(course.getCourseImg());
        courseDTO.setCourseLevel(course.getCourseLevel());
        courseDTO.setCreatedAt(course.getCreatedAt());
        courseDTO.setUpdatedAt(course.getUpdatedAt());
        courseDTO.setStatus(course.getStatus());
        courseDTO.setVideoUrlPreview(course.getVideoUrlPreview());
        return courseDTO;
    }
    }

