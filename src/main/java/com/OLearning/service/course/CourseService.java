package com.OLearning.service.course;

import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.dto.course.CourseViewDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {

        Course getCourseById(Long id);

        List<Chapter> getChaptersWithLessons(Long courseId);

        List<CourseViewDTO> getTopCourses();

        List<CourseViewDTO> getCoursesByCategoryId(int categoryId);

        Page<CourseViewDTO> searchCoursesGrid(
                        List<Long> categoryIds,
                        List<String> priceFilters,
                        List<String> levels,
                        String sortBy,
                        String keyword,
                        int page,
                        int size);

}
