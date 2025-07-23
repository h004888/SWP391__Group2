package com.OLearning.service.course.impl;

import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.dto.course.CourseViewDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.mapper.course.CourseDetailMapper;
import com.OLearning.repository.ChapterRepository;
import com.OLearning.repository.CourseRepository;
import com.OLearning.service.course.CourseService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    private ChapterRepository chapterRepository;

    public Pageable getPageable(int page, int size, String sortBy) {
        // Chỉ cho phép sort trong DB với các field có trong DB thật
        if ("Newest".equals(sortBy) || "Free".equals(sortBy)) {
            return PageRequest.of(page, size, Sort.by("createdAt").descending());
        }
        // Với MostPopular & MostViewed: sort sau khi lấy dữ liệu
        return PageRequest.of(page, size); // Không sort trong DB
    }

    @Override
    public Page<CourseViewDTO> searchCoursesGrid(
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

        List<Course> result = new ArrayList<>(coursesPage.getContent());

        // Sort bằng Java nếu là MostPopular hoặc MostViewed
        if ("MostPopular".equals(sortBy)) {
            result.sort((a, b) -> Long.compare(b.getCourseReviews().size(), a.getCourseReviews().size()));
        } else if ("MostViewed".equals(sortBy)) {
            result.sort((a, b) -> Integer.compare(b.getEnrollments().size(), a.getEnrollments().size()));
        }

        // Trả về lại Page<CourseDTO>
        return new PageImpl<>(
                result,
                pageable,
                coursesPage.getTotalElements()).map(CourseMapper::toCourseViewDTO);
    }

    @Override
    public CourseViewDTO getCourseRecentIncomplete(Long userId) {
        return CourseMapper.toCourseViewDTO(courseRepository.findMostRecentIncompleteCourse(userId));
    }

    @Override
    public List<CourseViewDTO> getTopCourses() {
        return courseRepository.findAllPublishedOrderByStudentCountDesc().stream()
                .map(CourseMapper::toCourseViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }

    @Override
    public CourseViewDTO getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(CourseMapper::toCourseViewDTO)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));
    }

    @Override
    public List<Chapter> getChaptersWithLessons(Long courseId) {
        return chapterRepository.findByCourseIdWithLessons(courseId);
    }

    @Override
    public List<CourseViewDTO> getCoursesByCategoryId(Long categoryId) {
        return courseRepository.findByCategoryIdAndStatusIgnoreCase(categoryId, "publish").stream()
                .map(CourseMapper::toCourseViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CourseViewDTO> getCourseByUserId(Long userId) {
        return courseRepository.findCoursesByUserId(userId).stream()
                .map(CourseMapper::toCourseViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countCourseIsPublish() {
        return courseRepository.countCourseIsPublish();
    }
}
