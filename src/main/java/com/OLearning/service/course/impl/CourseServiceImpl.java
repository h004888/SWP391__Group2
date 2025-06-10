package com.OLearning.service.course.impl;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.entity.Course;
import com.OLearning.mapper.course.CourseDetailMapper;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.service.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final CourseDetailMapper courseDetailMapper;

    @Override
    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        Page<Course> courseList = courseRepository.findAll(pageable);
        return courseList.map(courseMapper::toDTO);
    }

    @Override
    public Optional<CourseDetailDTO> getDetailCourse(Long id) {
        return courseRepository.findById(id).map(courseDetailMapper::toDTO);
    }

    @Override
    public boolean approveCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (!"approved".equalsIgnoreCase(course.getStatus())) {
                        course.setStatus("approved");
                        course.setUpdatedAt(LocalDateTime.now());
                        courseRepository.save(course);
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
    public boolean deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Page<CourseDTO> filterCoursesWithPagination(String keyword, Integer category, String price, String status, int page, int size) {
        String searchKeyword = keyword != null && !keyword.trim().isEmpty() ? keyword.trim() : null;

        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.filterCourses(
                searchKeyword, category, price, status, pageable
        );
        return coursePage.map(courseMapper::toDTO);
    }


}
