package com.OLearning.service.adminDashBoard;

import com.OLearning.dto.adminDashBoard.CourseDTO;
import com.OLearning.dto.adminDashBoard.CourseDetailDTO;
import com.OLearning.entity.Course;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {

    List<CourseDTO> getAllCourses();

    Optional<CourseDetailDTO> getDetailCourse(Long id);

    boolean deleteCourse(Long id);

    List<CourseDTO> filterCourses(String keyword, Integer categoryId, String price, String status);
}
