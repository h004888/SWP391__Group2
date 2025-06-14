package com.OLearning.repository;

import com.OLearning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.OLearning.entity.Enrollment;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.courseId = :courseId AND YEAR(e.enrollmentDate) = :year AND MONTH(e.enrollmentDate) = :month")
    Long countByCourseIdAndMonth(Long courseId, int year, int month);

    @Query("SELECT e.course FROM Enrollment e WHERE e.user.userId = :userId")
    List<Course> findCoursesByUserId(Long userId);

    @Query(value = "SELECT c.name as category, COUNT(e.enrollmentID) as count " +
            "FROM enrollments e " +
            "JOIN courses co ON e.courseId = co.courseId " +
            "JOIN categories c ON co.categoryId = c.categoryId " +
            "WHERE e.enrollmentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY c.name", nativeQuery = true)
    List<Object[]> getEnrollmentsByCategoryAndDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(DISTINCT e.user.userId) FROM Enrollment e")
    Long countTotalStudents();

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.status = 'completed'")
    Long countCompletedEnrollments();
}
