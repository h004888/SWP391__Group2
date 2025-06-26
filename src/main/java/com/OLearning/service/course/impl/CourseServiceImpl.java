package com.OLearning.service.course.impl;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.entity.Course;
import com.OLearning.mapper.course.CourseDetailMapper;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.course.CourseService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("adminCourseService")

public class CourseServiceImpl implements CourseService {
    @Autowired
    private  CourseRepository courseRepository;
    @Autowired
    private  CourseDetailMapper courseDetailMapper;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CourseDetailDTO> getDetailCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> courseDetailMapper.toDTO(course));
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

        return resultPage.map(CourseMapper::toDTO);
    }

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
                return PageRequest.of(page, size);
        }
    }

    @Override
    public Page<CourseDTO> searchCoursesGrid(
            List<Long> categoryIds,
            List<String> priceFilters,
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

        if (priceFilters != null && priceFilters.contains("All")) {
            priceFilters = null;
        }

        Pageable pageable = getPageable(page, size, sortBy);
        Page<Course> coursesPage = courseRepository.searchCourses(
                keyword,
                categoryIds,
                priceFilters,
                pageable
        );

        return coursesPage.map(CourseMapper::toDTO);
    }
    @Override
    public boolean approveCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (!"approved".equalsIgnoreCase(course.getStatus())) {
                        course.setStatus("approved");
                        course.setUpdatedAt(LocalDateTime.now());
                        courseRepository.save(course);
                        // Gửi notification cho instructor
                        if (course.getInstructor() != null) {
                            com.OLearning.entity.Notification notification = new com.OLearning.entity.Notification();
                            notification.setUser(course.getInstructor());
                            notification.setCourse(course);
                            notification.setMessage("Khóa học '" + course.getTitle() + "' của bạn đã được admin phê duyệt.");
                            notification.setType("COURSE_APPROVED");
                            notification.setStatus("failed");
                            notification.setSentAt(LocalDateTime.now());
                            notificationRepository.save(notification);
                        }
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
    @Override
    public boolean rejectCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (!"rejected".equalsIgnoreCase(course.getStatus())) {
                        course.setStatus("rejected");
                        course.setUpdatedAt(LocalDateTime.now());
                        courseRepository.save(course);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    public void blockCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setStatus("blocked"); // hoặc trạng thái bạn định nghĩa
        courseRepository.save(course);
    }

    @Override
    public Course findById(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow();
    }

    public void setPendingBlock(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setStatus("pending_block");
        courseRepository.save(course);
    }
}