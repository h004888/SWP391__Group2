package com.OLearning.service.enrollment.impl;

import com.OLearning.dto.enrollment.CourseEnrollmentStatsDTO;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.service.enrollment.EnrollmentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentStatisticsServiceImpl implements EnrollmentStatisticsService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public List<CourseEnrollmentStatsDTO> getStatsForInstructor(Long instructorId) {
        List<Object[]> rawStats = enrollmentRepository.getEnrollmentStatsByInstructor(instructorId);
        List<CourseEnrollmentStatsDTO> stats = new ArrayList<>();
        for (Object[] row : rawStats) {
            CourseEnrollmentStatsDTO dto = new CourseEnrollmentStatsDTO();
            dto.setCourseId((Long) row[0]);
            dto.setCourseTitle((String) row[1]);
            dto.setTotalEnroll((Long) row[2]);
            dto.setCompletedEnroll((Long) row[3]);
            stats.add(dto);
        }
        return stats;
    }
} 