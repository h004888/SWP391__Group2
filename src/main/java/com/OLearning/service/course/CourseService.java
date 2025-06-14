package com.OLearning.service.course;

import com.OLearning.dto.CourseDTO;
import com.OLearning.dto.CourseDetailDTO;
import com.OLearning.entity.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {

    List<CourseDTO> getAllCourses();

    Optional<CourseDetailDTO> getDetailCourse(Long id);

    boolean deleteCourse(Long id);

    List<CourseDTO> filterCourses(String keyword, Integer categoryId, String price, String status);

    List<Course> getTopCourses();

    Page<CourseDTO> getCoursesByTotalRatings(Pageable pageable);

    Page<CourseDTO> searchCourses(
            List<Long> categoryIds,
            String priceFilter,
            String sortBy,
            int page,
            int size);

    Page<CourseDTO> searchCoursesGrid(
            List<Long> categoryIds,
            List<String> priceFilters, // đổi kiểu ở đây
            String sortBy,
            String keyword,
            int page,
            int size);


}
