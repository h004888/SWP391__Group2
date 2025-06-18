package com.OLearning.service.enrollment;

import com.OLearning.dto.enrollment.EnrollmentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public interface EnrollmentService {
    boolean unblockEnrollment(int enrollmentId);
    int enrollmentCount();

    Map<String, Long> getEnrollmentsByCategoryAndDateRange(LocalDate startDate, LocalDate endDate);

    Long getTotalStudents();
    Enrollment getEnrollmentById(int erollmentId);
    Long getCompletedEnrollments();

    Page<EnrollmentDTO> getEnrollmentsByInstructorId(Long userId, int page, int size);

    boolean blockEnrollment(int enrollmentId);
}
