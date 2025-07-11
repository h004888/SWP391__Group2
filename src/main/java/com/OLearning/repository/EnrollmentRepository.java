package com.OLearning.repository;

import com.OLearning.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.OLearning.entity.Enrollment;

import jakarta.transaction.Transactional;

import java.util.List;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByUserUserId(Long userId);

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

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.instructor.userId = :instructorId")
    Long countStudentsByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.instructor.userId = :instructorId AND YEAR(e.enrollmentDate) = :year AND MONTH(e.enrollmentDate) = :month")
    Long countByInstructorIdAndMonth(@Param("instructorId") long instructorId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.instructor.userId = :instructorId AND e.enrollmentDate >= :startDate AND e.enrollmentDate <= :endDate")
    Long countByInstructorIdAndDateRange(@Param("instructorId") long instructorId, @Param("startDate") Date start, @Param("endDate") Date end);

    @Query("SELECT e FROM Enrollment e WHERE e.user.userId = :userId AND e.course.courseId = :courseId")
    Enrollment findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    //muốn làm theo phân trang
    //query lấy tất cả enrolled by Id
    @Query("SELECT e FROM Enrollment e WHERE e.course.instructor.userId = :userId")
    Page<Enrollment> findEnrollmentsByInstructorId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT e FROM Enrollment e WHERE e.user.userId = :userId")
    List<Enrollment> findEnrollmentsByUserId(@Param("userId") Long userId);

    @Query("SELECT e FROM Enrollment e WHERE e.enrollmentId = :enrollmentId")
    Optional<Enrollment> findByEnrollmentId(int enrollmentId);
    boolean existsByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);
}
