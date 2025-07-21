package com.OLearning.service.enrollment;

import com.OLearning.dto.enrollment.CourseEnrollmentStatsDTO;
import java.util.List;

public interface EnrollmentStatisticsService {
    List<CourseEnrollmentStatsDTO> getStatsForInstructor(Long instructorId);
} 