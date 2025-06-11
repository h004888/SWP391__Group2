package com.OLearning.service.courseMaintance;

import com.OLearning.entity.CourseMaintenance;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public interface CourseMaintenanceService {
    List<CourseMaintenance> getAllCourseMaintenances();
    List<CourseMaintenance> searchByUsername(String username);
    void processMonthlyMaintenance();
    Map<String, Object> getMaintenanceRevenueByDateRange(LocalDate startDate, LocalDate endDate);
}
