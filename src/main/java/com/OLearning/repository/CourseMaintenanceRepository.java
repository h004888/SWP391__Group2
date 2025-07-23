package com.OLearning.repository;

import com.OLearning.entity.CourseMaintenance;
import com.OLearning.entity.Fee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CourseMaintenanceRepository extends JpaRepository<CourseMaintenance, Long> {

    // Tìm theo username của instructor
    List<CourseMaintenance> findByCourseInstructorUsernameContaining(String username);

    @Query("SELECT cm FROM CourseMaintenance cm WHERE cm.course.courseId = :courseId AND cm.monthYear = :monthYear AND cm.status = 'overdue'")
    CourseMaintenance findOverdueByCourseIdAndMonthYear(Long courseId, LocalDate monthYear);

    @Query("SELECT cm.fee FROM CourseMaintenance cm WHERE cm.course.courseId = :courseId ORDER BY cm.sentAt DESC")
    Fee findLatestFeeByCourseId(Long courseId);

    boolean existsByCourseCourseIdAndDueDate(Long courseId, LocalDate dueDate);

    List<CourseMaintenance> findByMonthYearBetween(LocalDate startDate, LocalDate endDate);

    // New methods for pagination and filtering
    Page<CourseMaintenance> findAll(Pageable pageable);

    @Query("SELECT cm FROM CourseMaintenance cm WHERE " +
           "(:username IS NULL OR :username = '' OR " +
           "LOWER(cm.course.instructor.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR cm.status = :status) AND " +
           "(:monthYear IS NULL OR FUNCTION('YEAR', cm.monthYear) = FUNCTION('YEAR', :monthYear) AND " +
           "FUNCTION('MONTH', cm.monthYear) = FUNCTION('MONTH', :monthYear)) " +
           "ORDER BY cm.monthYear DESC")
    Page<CourseMaintenance> findByUsernameAndStatusAndMonthYear(
        @Param("username") String username,
        @Param("status") String status,
        @Param("monthYear") LocalDate monthYear,
        Pageable pageable
    );

    // Find all maintenance payments for a given instructor
    List<CourseMaintenance> findByCourse_Instructor_UserId(Long instructorId);
    
    // filtering maintenance payments by instructor with course name and month/year
    @Query("SELECT cm FROM CourseMaintenance cm WHERE " +
           "cm.course.instructor.userId = :instructorId AND " +
           "(:courseName IS NULL OR :courseName = '' OR " +
           "LOWER(cm.course.title) LIKE LOWER(CONCAT('%', :courseName, '%'))) AND " +
           "(:monthYear IS NULL OR FUNCTION('YEAR', cm.monthYear) = FUNCTION('YEAR', :monthYear) AND " +
           "FUNCTION('MONTH', cm.monthYear) = FUNCTION('MONTH', :monthYear)) " +
           "ORDER BY cm.monthYear DESC, cm.sentAt DESC")
    Page<CourseMaintenance> findByInstructorIdAndCourseNameAndMonthYear(
        @Param("instructorId") Long instructorId,
        @Param("courseName") String courseName,
        @Param("monthYear") LocalDate monthYear,
        Pageable pageable
    );
}
