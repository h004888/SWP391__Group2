package com.OLearning.service.courseMaintance;

import com.OLearning.entity.CourseMaintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    // New methods for pagination
    Page<CourseMaintenance> getAllCourseMaintenances(Pageable pageable);
    Page<CourseMaintenance> filterMaintenances(String username, String status, LocalDate monthYear, Pageable pageable);
}
