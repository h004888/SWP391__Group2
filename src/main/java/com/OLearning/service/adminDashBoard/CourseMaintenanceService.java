package com.OLearning.service.adminDashBoard;

import com.OLearning.entity.CourseMaintenance;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface CourseMaintenanceService {
    List<CourseMaintenance> getAllCourseMaintenances();
    List<CourseMaintenance> searchByUsername(String username);
    List<CourseMaintenance> filterByMonthYear(String monthYear);
    List<LocalDate> getDistinctMonthYears();
}
