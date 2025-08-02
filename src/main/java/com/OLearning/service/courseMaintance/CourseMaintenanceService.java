package com.OLearning.service.courseMaintance;

import com.OLearning.entity.CourseMaintenance;
import com.OLearning.entity.Fee;
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

    Page<CourseMaintenance> getAllCourseMaintenances(Pageable pageable);

    Page<CourseMaintenance> filterMaintenances(String username, String status, LocalDate monthYear, Pageable pageable);

    // New method for filtering maintenance payments by instructor
    Page<CourseMaintenance> filterMaintenancesByInstructor(Long instructorId, String courseName, LocalDate monthYear, Pageable pageable);

    void checkOverdueMaintenance();

    List<Fee> getListFees();

    void updateFee(Long feeId, Long minEnrollments, Long maxEnrollments, Long maintenanceFee);

    void deleteFee(Long feeId);

    void addFee(Long minEnrollments, Long maxEnrollments, Long maintenanceFee);

    List<CourseMaintenance> getMaintenancesByInstructorId(Long instructorId);

    boolean processMaintenancePayment(Long maintenanceId, String refCode);

    String getMaintenanceStatusById(Long maintenanceId);

    Long sumCourseMaintainForInstructor(Long instructorId);
    
    boolean canPayMaintenance(Long maintenanceId);
}
