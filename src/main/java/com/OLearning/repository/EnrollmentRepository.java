package com.OLearning.repository;

import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import com.OLearning.dto.enrollment.UserCourseProgressDTO;
import com.OLearning.entity.Enrollment;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Date;
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
        List<Object[]> getEnrollmentsByCategoryAndDateRange(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT COUNT(DISTINCT e.user.userId) FROM Enrollment e")
        Long countTotalStudents();

        @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.status = 'completed'")
        Long countCompletedEnrollments();

        @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.instructor.userId = :instructorId")
        Long countStudentsByInstructorId(@Param("instructorId") Long instructorId);

        @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.instructor.userId = :instructorId AND YEAR(e.enrollmentDate) = :year AND MONTH(e.enrollmentDate) = :month")
        Long countByInstructorIdAndMonth(@Param("instructorId") long instructorId, @Param("year") int year,
                        @Param("month") int month);

        @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.instructor.userId = :instructorId AND e.enrollmentDate >= :startDate AND e.enrollmentDate <= :endDate")
        Long countByInstructorIdAndDateRange(@Param("instructorId") long instructorId, @Param("startDate") Date start,
                        @Param("endDate") Date end);

        @Query("SELECT e FROM Enrollment e WHERE e.user.userId = :userId AND e.course.courseId = :courseId")
        Enrollment findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

        // muốn làm theo phân trang
        // query lấy tất cả enrolled by Id
        @Query("SELECT e FROM Enrollment e WHERE e.course.instructor.userId = :userId")
        Page<Enrollment> findEnrollmentsByInstructorId(@Param("userId") Long userId, Pageable pageable);

        @Query("SELECT e FROM Enrollment e WHERE e.user.userId = :userId")
        List<Enrollment> findEnrollmentsByUserId(@Param("userId") Long userId);

        // query lấy all enrollment by courseId
        @Query("SELECT e FROM Enrollment e WHERE e.course.courseId = :courseId")
        Page<Enrollment> findEnrollmentsByCourseId(@Param("courseId") Long courseId, Pageable pageable);

        @Query("SELECT e FROM Enrollment e WHERE e.enrollmentId = :enrollmentId")
        Optional<Enrollment> findByEnrollmentId(int enrollmentId);

        boolean existsByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);

        @Query(value = """
                        SELECT DATEDIFF(WEEK, e.EnrollmentDate, GETDATE())
                        FROM Enrollments e
                        WHERE e.UserID = :userId AND e.CourseID = :courseId
                        """, nativeQuery = true)
        Integer getWeeksEnrolled(@Param("userId") Long userId, @Param("courseId") Long courseId);

        @Modifying
        @Transactional
        @Query(value = """
                        UPDATE e
                        SET e.Progress = CAST(
                            100.0 * ISNULL(lc.CompletedLessons, 0) / NULLIF(tl.TotalLessons, 0)
                            AS DECIMAL(5,2)
                        )
                        FROM Enrollments e
                        JOIN (
                            SELECT COUNT(DISTINCT lc.LessonID) AS CompletedLessons
                            FROM Lessons l
                            JOIN Chapters ch ON l.ChapterID = ch.ChapterID
                            JOIN LessonCompletion lc ON lc.LessonID = l.LessonID
                            WHERE ch.CourseID = :courseId AND lc.UserID = :userId
                        ) AS lc ON 1=1
                        JOIN (
                            SELECT COUNT(l.LessonID) AS TotalLessons
                            FROM Lessons l
                            JOIN Chapters ch ON l.ChapterID = ch.ChapterID
                            WHERE ch.CourseID = :courseId
                        ) AS tl ON 1=1
                        WHERE e.UserID = :userId AND e.CourseID = :courseId
                        """, nativeQuery = true)
        void updateProgressByUser(@Param("userId") Long userId, @Param("courseId") Long courseId);

        Optional<Enrollment> findByUserAndCourse(User user, Course course);

        // lay all enrollment theo InstructorId
        @Query("SELECT e FROM Enrollment e JOIN e.course c WHERE c.instructor.userId = :instructorId")
        List<Enrollment> calculateSumEnrollment(@Param("instructorId") Long instructorId);

        @Query("SELECT e FROM Enrollment e WHERE e.user = :user AND e.course = :course ORDER BY e.enrollmentDate DESC")
        List<Enrollment> findByUserAndCourseOrderByEnrollmentDateDesc(@Param("user") User user,
                        @Param("course") Course course);

        @Query("SELECT e FROM Enrollment e WHERE e.user = :user AND e.course = :course ORDER BY e.enrollmentDate DESC")
        Optional<Enrollment> findFirstByUserAndCourseOrderByEnrollmentDateDesc(@Param("user") User user,
                        @Param("course") Course course);

        @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.user = :user AND e.course = :course")
        Long countByUserAndCourse(@Param("user") User user, @Param("course") Course course);

        @Query("SELECT e FROM Enrollment e WHERE e.user = :user AND e.course = :course ORDER BY e.enrollmentDate DESC")
        List<Enrollment> findAllByUserAndCourseOrderByEnrollmentDateDesc(@Param("user") User user,
                        @Param("course") Course course);

        Optional<Enrollment> findByEnrollmentDateAfter(LocalDate date);

        Optional<Enrollment> findByUser_UserId(Long userId);

        @Query("""
                            SELECT new com.OLearning.dto.enrollment.UserCourseProgressDTO(
                                e.user.userId,
                                e.course.courseId,
                                e.enrollmentId,
                                e.progress
                            )
                            FROM Enrollment e
                            WHERE e.user.userId = :userId
                              AND e.course.courseId NOT IN (
                                  SELECT e2.course.courseId
                                  FROM Enrollment e2
                                  WHERE e2.user.userId = :userId
                                    AND e2.status = 'completed'
                              )
                        """)
        List<UserCourseProgressDTO> findProgressDTOExcludingCompleted(@Param("userId") Long userId);

        @Modifying
        @Transactional
        @Query(value = "UPDATE Enrollments SET Status = 'completed' WHERE Status = 'on going' AND UserID = :userId AND CourseID = :courseId", nativeQuery = true)
        int updateStatusCompleted(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query(value = "SELECT COUNT(*) AS TotalEnrollments " +
            "FROM Courses c " +
            "INNER JOIN Enrollments e ON c.CourseID = e.CourseID " +
            "WHERE c.InstructorID = :instructorId", nativeQuery = true)
    Long countTotalEnrollmentByUserId(@Param("instructorId") Long instructorId);

    @Query("SELECT e.course.courseId, e.course.title, COUNT(e), SUM(CASE WHEN e.status = 'completed' THEN 1 ELSE 0 END) FROM Enrollment e WHERE e.course.instructor.userId = :instructorId GROUP BY e.course.courseId, e.course.title")
    List<Object[]> getEnrollmentStatsByInstructor(@Param("instructorId") Long instructorId);

}
