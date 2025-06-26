package com.OLearning.mapper.course;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseAddDTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.Course;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
    public static CourseDTO toDTO(Course course) {
        if (course == null)  {return null;}

        return new CourseDTO(
                course.getCourseId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice() != null ? course.getPrice().doubleValue() : null,
                course.getDiscount() != null ? course.getDiscount().doubleValue() : null,
                course.getCourseImg(),
                course.getIsFree(),
                course.getCategory() != null ? course.getCategory().getName() : "N/A",
                course.getListOfChapters().size(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                course.getInstructor(),
                course.getStatus(),
                course.getCourseLevel(),
                course.getEnrollments() != null ? course.getEnrollments().size() : 0
        );
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
        return courseDTO;
    }
    //save course basic
    public Course CourseBasic(AddCourseStep1DTO dto, Course course) {
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCreatedAt(LocalDateTime.now());
        course.setPrice(dto.getPrice());
        course.setCourseLevel(dto.getCourseLevel());
        // Không set averageRating, reviewCount, duration vì tính tự động
        return course;
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
}
