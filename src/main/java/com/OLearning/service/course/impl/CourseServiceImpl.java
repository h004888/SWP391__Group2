package com.OLearning.service.course.impl;

import com.OLearning.dto.CourseDTO;
import com.OLearning.dto.CourseDetailDTO;
import com.OLearning.entity.Course;
import com.OLearning.mapper.CourseDetailMapper;
import com.OLearning.mapper.CourseMapper;
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

    @Override
    public List<CourseDTO> getAllCourses() {
        List<Course> courseList = courseRepository.findAll();
        return courseList.stream()
                .map(CourseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CourseDetailDTO> getDetailCourse(Long id) {
        return courseRepository.findById(id).map(courseDetailMapper::toDTO);
    }

    @Override
    public boolean deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<CourseDTO> filterCourses(String keyword, Integer categoryId, String price, String status) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = "%" + keyword.trim().toLowerCase() + "%";
        } else {
            keyword = null;
        }
        if (categoryId != null && categoryId == 0)
            categoryId = null;
        if (price != null && price.trim().isEmpty())
            price = null;
        if (status != null && status.trim().isEmpty())
            status = null;

        return courseRepository.filterCourses(keyword, categoryId, price, status).stream()
                .map(CourseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> getTopCourses() {
        return courseRepository.findTop5ByOrderByTotalStudentEnrolledDesc();
    }

    @Override
    public Page<CourseDTO> getCoursesByTotalRatings(Pageable pageable) {
        return courseRepository.findAllByOrderByTotalRatingsDesc(pageable).map(CourseMapper::toDTO);
    }

    @Override
    public Page<CourseDTO> searchCourses(List<Long> categoryIds, String priceFilter, String sortBy, int page,
            int size) {
        Pageable pageable = getPageable(page, size, sortBy);
        Page<Course> resultPage = courseRepository.filterCourses(
                categoryIds == null || categoryIds.isEmpty() ? null : categoryIds,
                priceFilter == null || priceFilter.equalsIgnoreCase("All") ? null : priceFilter,
                pageable);

        return resultPage.map(course -> new CourseDTO(
                course.getCourseId(), course.getTitle(), course.getDescription(),
                course.getPrice(), course.getDiscount(), course.getCourseImg(),
                course.getDuration(), course.getTotalLessons(), course.getTotalRatings(),
                course.getTotalStudentEnrolled(), course.getCreatedAt(), course.getUpdatedAt(),
                course.getCourseLevel()));
    }

    public Pageable getPageable(int page, int size, String sortBy) {
        switch (sortBy) {
            case "Newest":
                return PageRequest.of(page, size, Sort.by("createdAt").descending());
            case "MostPopular":
                return PageRequest.of(page, size, Sort.by("totalRatings").descending());
            case "MostViewed":
                return PageRequest.of(page, size, Sort.by("totalStudentEnrolled").descending());
            case "Free": // Ưu tiên Free -> tạo điều kiện lọc phía trên, sort theo thời gian tạo
                return PageRequest.of(page, size, Sort.by("createdAt").descending());
            default:
                return PageRequest.of(page, size); // Không sort
        }
    }

    @Override
    public Course findById(Long id) {
        return courseRepository.findById(id).orElse(null);
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

        // 1. Chuẩn hóa keyword: nếu rỗng hoặc toàn khoảng trắng => null
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // 2. Chuẩn hóa categoryIds: nếu list rỗng => null (bỏ điều kiện)
        if (categoryIds != null && categoryIds.isEmpty()) {
            categoryIds = null;
        }

        // 3. Chuẩn hóa priceFilters: nếu có "All" 
        if (priceFilters != null) {
            if (priceFilters.contains("All")) {
                priceFilters = null;
            }
        }

        // 4. Chuẩn hóa levels: nếu list rỗng hoặc chứa "All levels" => null (bỏ lọc
        // level)
        if (levels != null) {
            if (levels.isEmpty()) {
                levels = null;
            }
        }

        // 5. Tạo Pageable với sort
        Pageable pageable = getPageable(page, size, sortBy);

        // 6. Gọi repository
        Page<Course> coursesPage = courseRepository.searchCourses(
                keyword,
                categoryIds,
                priceFilters,
                levels,
                pageable);

        // 7. Ánh xạ về DTO
        return coursesPage.map(CourseMapper::toDTO);
    }

}
