package com.OLearning.service.course;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.entity.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {
        
        List<Course> getTopCourses();

        Page<CourseDTO> searchCoursesGrid(
                        List<Long> categoryIds,
                        List<String> priceFilters,
                        List<String> levels,
                        String sortBy,
                        String keyword,
                        int page,
                        int size);

}
