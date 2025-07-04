package com.OLearning.repository;

import java.util.List;

import com.OLearning.dto.lessonCompletion.LessonCompletionDTO;
import com.OLearning.entity.LessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Integer> {

        List<LessonCompletion> findByUser_UserIdAndLesson_Chapter_Course_CourseId(Long userId,
                        Long courseId);

        boolean existsByUserUserIdAndLessonLessonId(Long userId, Long lessonId);

        @Query("""
                            SELECT
                                (COUNT(DISTINCT lc.lesson.id) * 1.0) / COUNT(DISTINCT l.id) * 100
                            FROM Lesson l
                            JOIN l.chapter c
                            JOIN c.course co
                            LEFT JOIN LessonCompletion lc ON lc.lesson.id = l.id AND lc.user.id = :userId
                            WHERE co.id = :courseId
                        """)
        Double getCourseProgressPercent(@Param("userId") Long userId, @Param("courseId") Long courseId);

        @Query(value = """
                    DECLARE @UserId INT = :userId;
                    DECLARE @CourseId INT = :courseId;
                
                    SELECT COUNT(DISTINCT lc.LessonID) AS LessonsCompleted
                    FROM LessonCompletion lc
                    JOIN Lessons l ON lc.LessonID = l.LessonID
                    JOIN Chapters c ON l.ChapterID = c.ChapterID
                    WHERE lc.UserID = @UserId AND c.CourseID = @CourseId;
                    
                """,
                nativeQuery = true
        )
        Integer getNumberOfCompletedLessons(Long userId, Long courseId);
}
