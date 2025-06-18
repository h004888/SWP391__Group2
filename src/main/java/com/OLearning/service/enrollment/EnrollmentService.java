package com.OLearning.service.enrollment;

import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public interface EnrollmentService {

    int enrollmentCount();

    Map<String, Long> getEnrollmentsByCategoryAndDateRange(LocalDate startDate, LocalDate endDate);

    Long getTotalStudents();

    Long getCompletedEnrollments();

    Long getStudentCountByInstructorId(Long instructorId);

    // Đếm số lượng enrollment theo instructor, năm, tháng
    Long countEnrollmentsByInstructorAndMonth(long instructorId, int year, int month);

    Long countEnrollmentsByInstructorAndDateRange(long instructorId, java.time.LocalDate start, java.time.LocalDate end);
}
