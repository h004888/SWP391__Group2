package com.OLearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.OLearning.entity.Enrollment;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByUserUserId(Long userId);

    boolean existsByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);

    @Query(
            value = """
                    SELECT DATEDIFF(WEEK, e.EnrollmentDate, GETDATE()) 
                    FROM Enrollments e 
                    WHERE e.UserID = :userId AND e.CourseID = :courseId
                    """,
            nativeQuery = true
    )
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

}
