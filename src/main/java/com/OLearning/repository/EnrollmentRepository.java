package com.OLearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.OLearning.dto.enrollment.UserCourseProgressDTO;
import com.OLearning.entity.Enrollment;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByUserUserId(Long userId);

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

    Optional<Enrollment> findByEnrollmentDateAfter(LocalDate date);

    Optional<Enrollment> findByUser_UserId(Long userId);

    @Query("SELECT e FROM Enrollment e WHERE e.user.userId = :userId AND e.course.courseId = :courseId")
    Enrollment findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

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
}
