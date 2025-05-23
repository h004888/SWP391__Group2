package com.OLearning.service.adminDashBoard;

import com.OLearning.dto.adminDashBoard.CourseDTO;
import com.OLearning.dto.adminDashBoard.CourseDetailDTO;
import com.OLearning.entity.Course;
import com.OLearning.mapper.adminDashBoard.CourseDetailMapper;
import com.OLearning.mapper.adminDashBoard.CourseMapper;
import com.OLearning.repository.adminDashBoard.CourseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepo courseRepo;
    private final CourseDetailMapper courseDetailMapper;

    public List<CourseDTO> getAllCourses() {
        List<Course> courseList = courseRepo.findAll();
        List<CourseDTO> collect = courseList.stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
        return collect;
    }

    public Optional<CourseDetailDTO> getDetailCourse(Long id) {
        return courseRepo.findById(id).map(courseDetailMapper::toDTO);
    }

    public boolean approveCourse(Long id) {
        return courseRepo.findById(id)
                .map(course -> {
                    if (!Boolean.TRUE.equals(course.getIsChecked())) {
                        course.setIsChecked(true);
                        courseRepo.save(course);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    public boolean rejectCourse(Long id) {
        return courseRepo.findById(id)
                .map(course -> {
                    if (!Boolean.TRUE.equals(course.getIsChecked())) {
                        course.setIsChecked(false);
                        courseRepo.save(course);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    public boolean deleteCourse(Long id) {
        if (courseRepo.existsById(id)) {
            courseRepo.deleteById(id);
            return true;
        }
        return false;
    }


}
