package com.OLearning.repository.adminDashBoard;


import com.OLearning.entity.CourseMaintenance;
import com.OLearning.entity.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CourseMaintenanceRepository extends JpaRepository<CourseMaintenance, Long> {

    // Tìm theo username của instructor
    List<CourseMaintenance> findByCourseInstructorUsernameContaining(String username);

    @Query("SELECT cm FROM CourseMaintenance cm WHERE cm.course.courseId = :courseId AND cm.monthYear = :monthYear AND cm.status = 'overdue'")
    CourseMaintenance findOverdueByCourseIdAndMonthYear(Long courseId, LocalDate monthYear);

    @Query("SELECT cm.fee FROM CourseMaintenance cm WHERE cm.course.courseId = :courseId ORDER BY cm.sentAt DESC")
    Fees findLatestFeeByCourseId(Long courseId);

    boolean existsByCourseCourseIdAndDueDate(Long courseId, LocalDate dueDate);
}
