package com.OLearning.service.courseMaintenance;

import com.OLearning.entity.CourseMaintenance;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CourseMaintenanceService {
    List<CourseMaintenance> getAllCourseMaintenances();
    List<CourseMaintenance> searchByUsername(String username);
}
