package com.OLearning.service.course.impl;

import com.OLearning.dto.CourseDTO;
import com.OLearning.dto.CourseDetailDTO;
import com.OLearning.entity.Course;
import com.OLearning.mapper.CourseDetailMapper;
import com.OLearning.mapper.CourseMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.service.course.CourseService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("adminCourseService")
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final CourseDetailMapper courseDetailMapper;

    @Override
    public List<CourseDTO> getAllCourses() {
        List<Course> courseList = courseRepository.findAll();
        return courseList.stream()
                .map(courseMapper::toDTO)
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
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

}
