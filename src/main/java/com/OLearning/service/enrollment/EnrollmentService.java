package com.OLearning.service.enrollment;

import java.util.List;

import org.springframework.stereotype.Service;

import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;

@Service
public interface EnrollmentService {
    List<Course> getCoursesByUserId(Long userId);
    boolean hasEnrolled(Long userId, Long courseId);
    
}
