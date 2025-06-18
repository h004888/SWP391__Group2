package com.OLearning.service.course.impl;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.entity.Course;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.mapper.course.CourseDetailMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.service.course.CourseService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("adminCourseService")
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseDetailMapper courseDetailMapper;

    public Pageable getPageable(int page, int size, String sortBy) {
        switch (sortBy) {
            case "Newest":
                return PageRequest.of(page, size, Sort.by("createdAt").descending());
            case "MostPopular":
                return PageRequest.of(page, size, Sort.by("totalRatings").descending());
            case "MostViewed":
                return PageRequest.of(page, size, Sort.by("totalStudentEnrolled").descending());
            case "Free":
                return PageRequest.of(page, size, Sort.by("createdAt").descending());
            default:
                return PageRequest.of(page, size); // Không sort
        }
    }

    @Override
    public Page<CourseDTO> searchCoursesGrid(
            List<Long> categoryIds,
            List<String> priceFilters,
            List<String> levels,
            String sortBy,
            String keyword,
            int page,
            int size) {

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        if (categoryIds != null && categoryIds.isEmpty()) {
            categoryIds = null;
        }

        if (priceFilters != null) {
            if (priceFilters.contains("All")) {
                priceFilters = null;
            }
        }

        if (levels != null) {
            if (levels.isEmpty()) {
                levels = null;
            }
        }

        Pageable pageable = getPageable(page, size, sortBy);

        Page<Course> coursesPage = courseRepository.searchCourses(
                keyword,
                categoryIds,
                priceFilters,
                levels,
                pageable);

        return coursesPage.map(CourseMapper::toDTO);
    }

}
