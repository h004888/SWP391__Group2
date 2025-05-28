package com.OLearning.mapper.instructorDashBoard;

import com.OLearning.dto.instructorDashboard.CourseAddDTO;
import com.OLearning.entity.Categories;
import com.OLearning.entity.Course;
import com.OLearning.repository.instructorDashBoard.InstructorCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
