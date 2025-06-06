package com.OLearning.service.course;

import com.OLearning.dto.adminDashBoard.CourseDTO;
import com.OLearning.dto.adminDashBoard.CourseDetailDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {

    List<CourseDTO> getAllCourses();

    Optional<CourseDetailDTO> getDetailCourse(Long id);

    boolean approveCourse(Long id);


    boolean rejectCourse(Long id);


    boolean deleteCourse(Long id);

    List<CourseDTO> filterCourses(String keyword, Integer categoryId, String price,String status);
}
