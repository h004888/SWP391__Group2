package com.OLearning.mapper.course;

import com.OLearning.dto.course.CourseAddDTO;
import com.OLearning.entity.Course;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class CourseMapper {
    //Mapper ve de save course
    public Course MapCourseAdd (CourseAddDTO dto) {
       Course course = new Course();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setPrice(dto.getPrice());
        course.setTotalStudentEnrolled(0);
        course.setIsChecked(true);
        course.setDiscount(dto.getDiscount());
        course.setTotalLessons(0);
        course.setTotalRatings(0);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        course.setDuration(0);
        return course;
    }


}
