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
}
