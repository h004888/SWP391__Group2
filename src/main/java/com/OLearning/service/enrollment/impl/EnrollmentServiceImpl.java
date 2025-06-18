package com.OLearning.service.enrollment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.service.enrollment.EnrollmentService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public int enrollmentCount() {
        return (int) enrollmentRepository.count();
    }

    @Override
    public Map<String, Long> getEnrollmentsByCategoryAndDateRange(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> enrollmentsByCategory = new HashMap<>();
        List<Object[]> results = enrollmentRepository.getEnrollmentsByCategoryAndDateRange(startDate, endDate);
        for (Object[] result : results) {
            String category = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            enrollmentsByCategory.put(category, count);
        }
        return enrollmentsByCategory;
    }

    @Override
    public Long getTotalStudents() {
        return enrollmentRepository.countTotalStudents();
    }

    @Override
    public Long getCompletedEnrollments() {
        return enrollmentRepository.countCompletedEnrollments();
    }

    @Override
    public Long getStudentCountByInstructorId(Long instructorId) {
        return enrollmentRepository.countStudentsByInstructorId(instructorId);
    }
}
