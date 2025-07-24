package com.OLearning.repository;

import com.OLearning.dto.enrollment.EnrollMaxMinDTO;
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

    @Query("SELECT e FROM Enrollment e WHERE e.course.courseId = :courseId")
    List<Enrollment> findByCourseId(@Param("courseId") Long courseId);

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

    //query lấy all enrollment by courseId
    @Query("SELECT e FROM Enrollment e WHERE e.course.courseId = :courseId")
    Page<Enrollment> findEnrollmentsByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    @Query("SELECT e FROM Enrollment e WHERE e.enrollmentId = :enrollmentId")
    Optional<Enrollment> findByEnrollmentId(int enrollmentId);

    boolean existsByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);

    //lay all enrollment theo InstructorId
    @Query("SELECT e FROM Enrollment e JOIN e.course c WHERE c.instructor.userId = :instructorId")
    List<Enrollment> calculateSumEnrollment(@Param("instructorId") Long instructorId);

    @Query("SELECT e.course.courseId, e.course.title, COUNT(e), SUM(CASE WHEN e.status = 'completed' THEN 1 ELSE 0 END) FROM Enrollment e WHERE e.course.instructor.userId = :instructorId GROUP BY e.course.courseId, e.course.title")
    List<Object[]> getEnrollmentStatsByInstructor(@Param("instructorId") Long instructorId);

    //tong nguoi dang ki khoa hoc
    @Query("SELECT Count(e) FROM Enrollment e JOIN e.course c WHERE c.instructor.userId = :instructorId")
    Integer countAllEnrollments(@Param("instructorId") Long instructorId);

    @Query("SELECT Count(e) FROM Enrollment e JOIN e.course c WHERE c.instructor.userId = :instructorId and e.status = 'onGoing'")
    Integer countAllEnrollmentsOnGoing(@Param("instructorId") Long instructorId);
    //tong nguoi dang ki khoa hoc voi trang thai 'onGoing'

    @Query("SELECT Count(e) FROM Enrollment e JOIN e.course c WHERE c.instructor.userId = :instructorId and e.status = 'completed'")
    Integer countAllEnrollmentsCompleted(@Param("instructorId") Long instructorId);
    //tong so nguoi dang ki khao hoc

    @Query(value = "SELECT Top 1 CAST(COUNT(e.EnrollmentID) AS BIGINT) AS enrollment_count, " +
            "c.CourseID, c.CourseImg, AVG(e.Progress), c.CourseLevel, e.EnrollmentDate, c.Title " +
            "FROM Enrollments e " +
            "JOIN Courses c ON e.CourseID = c.CourseID " +
            "WHERE c.InstructorID = :instructorId " +
            "GROUP BY c.CourseID, c.CourseImg, c.CourseLevel, e.EnrollmentDate, c.Title " +
            "ORDER BY COUNT(e.EnrollmentID) DESC", nativeQuery = true)
    EnrollMaxMinDTO courseEnrollMax(@Param("instructorId") Long instructorId);

    @Query(value = "SELECT Top 1 CAST(COUNT(e.EnrollmentID) AS BIGINT) AS enrollment_count, " +
            "c.CourseID, c.CourseImg, AVG(e.Progress), c.CourseLevel, e.EnrollmentDate, c.Title " +
            "FROM Enrollments e " +
            "JOIN Courses c ON e.CourseID = c.CourseID " +
            "WHERE c.InstructorID = :instructorId " +
            "GROUP BY c.CourseID, c.CourseImg, c.CourseLevel, e.EnrollmentDate, c.Title " +
            "ORDER BY COUNT(e.EnrollmentID) ASC", nativeQuery = true)
    EnrollMaxMinDTO courseEnrollMin(@Param("instructorId") Long instructorId);
    @Query(value="SELECT ISNULL((\n" +
            "    SELECT TOP 1 COUNT(e.EnrollmentID)\n " +
            "    FROM Enrollments e\n " +
            "    JOIN Courses c ON e.CourseID = c.CourseID\n " +
            "    WHERE c.InstructorID = 31 AND e.Status = 'onGoing'\n " +
            "    GROUP BY c.Title\n " +
            "    ORDER BY COUNT(e.EnrollmentID) DESC\n " +
            "), 0) AS enrollment_count", nativeQuery = true)
    Integer onGoingMax(@Param("instructorId") Long instructorId);
    @Query(value="SELECT ISNULL((\n" +
            "    SELECT TOP 1 COUNT(e.EnrollmentID)\n " +
            "    FROM Enrollments e\n " +
            "    JOIN Courses c ON e.CourseID = c.CourseID\n " +
            "    WHERE c.InstructorID = 31 AND e.Status = 'completed'\n " +
            "    GROUP BY c.Title\n " +
            "    ORDER BY COUNT(e.EnrollmentID) DESC\n " +
            "), 0) AS enrollment_count", nativeQuery = true)
    Integer completedMax(@Param("instructorId") Long instructorId);
    @Query(value="SELECT ISNULL((\n" +
            "    SELECT TOP 1 COUNT(e.EnrollmentID)\n " +
            "    FROM Enrollments e\n " +
            "    JOIN Courses c ON e.CourseID = c.CourseID\n " +
            "    WHERE c.InstructorID = 31 AND e.Status = 'onGoing'\n " +
            "    GROUP BY c.Title\n " +
            "    ORDER BY COUNT(e.EnrollmentID) ASC\n " +
            "), 0) AS enrollment_count", nativeQuery = true)
    Integer onGoingMin(@Param("instructorId") Long instructorId);
    @Query(value="SELECT ISNULL((\n" +
            "    SELECT TOP 1 COUNT(e.EnrollmentID)\n " +
            "    FROM Enrollments e\n " +
            "    JOIN Courses c ON e.CourseID = c.CourseID\n " +
            "    WHERE c.InstructorID = 31 AND e.Status = 'completed'\n " +
            "    GROUP BY c.Title\n " +
            "    ORDER BY COUNT(e.EnrollmentID) ASC\n " +
            "), 0) AS enrollment_count", nativeQuery = true)
    Integer completedMin(@Param("instructorId") Long instructorId);

    @Query("SELECT e FROM Enrollment e " +
           "JOIN e.course c " +
           "JOIN e.user u " +
           "WHERE c.instructor.userId = :instructorId " +
           "AND (:courseId IS NULL OR c.courseId = :courseId) " +
           "AND (:status IS NULL OR :status = '' OR e.status = :status) " +
           "AND (:searchTerm IS NULL OR :searchTerm = '' OR " +
           "     LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Enrollment> findEnrollmentsWithFilters(
            @Param("instructorId") Long instructorId,
            @Param("courseId") Long courseId,
            @Param("status") String status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);
}
