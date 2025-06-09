package com.OLearning.service.course;

import com.OLearning.dto.course.CourseAddDTO;
import com.OLearning.dto.course.CourseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    List<CourseDTO> findCourseByUserId(Long userId);
    boolean canCreateCourse(Long userId);
    void createCourse(CourseAddDTO courseAddDTO, MultipartFile courseImg);
}
