package com.OLearning.service.course;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {

    Page<CourseDTO> getAllCourses(Pageable pageable);

    Optional<CourseDetailDTO> getDetailCourse(Long id);

    boolean approveCourse(Long id);

    boolean rejectCourse(Long id);

    boolean deleteCourse(Long id);

//    List<CourseDTO> filterCourses(String keyword, Integer categoryId, String price, String status);

    Page<CourseDTO> filterCoursesWithPagination(String keyword, Integer category, String price, String status, int page, int size);
}
