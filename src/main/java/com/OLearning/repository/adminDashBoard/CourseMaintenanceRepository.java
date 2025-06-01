package com.OLearning.repository.adminDashBoard;


import com.OLearning.entity.CourseMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CourseMaintenanceRepository extends JpaRepository<CourseMaintenance, Long> {

    // Tìm theo username của instructor
    List<CourseMaintenance> findByCourseInstructorUsernameContaining(String username);
    // Tìm theo tháng-năm
    List<CourseMaintenance> findByMonthYear(LocalDate monthYear);

    // Lấy danh sách tháng-năm duy nhất
    @Query("SELECT DISTINCT cm.monthYear FROM CourseMaintenance cm ORDER BY cm.monthYear")
    List<LocalDate> findDistinctMonthYears();
}
