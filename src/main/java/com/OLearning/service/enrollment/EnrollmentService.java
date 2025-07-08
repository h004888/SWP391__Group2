package com.OLearning.service.enrollment;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;

@Service
public interface EnrollmentService {
    List<Course> getCoursesByUserId(Long userId);
    boolean hasEnrolled(Long userId, Long courseId);
    Integer getWeeksEnrolled(Long userId, Long courseId);
    void updateProgressByUser( Long userId,  Long courseId);

    // Lấy enrollment trong 30 ngày gần nhất
    List<Enrollment> findByEnrollmentDateAfter(LocalDate date);

    // Lấy enrollment của một user cụ thể
    List<Enrollment> findByUserId(Long userId);
}
