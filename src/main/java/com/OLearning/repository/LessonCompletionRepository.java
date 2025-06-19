package com.OLearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.OLearning.entity.LessonCompletion;

public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Integer> {

    List<LessonCompletion> findByUser_UserId(Long userId);

    boolean existsByUser_UserIdAndLesson_LessonId(Long userId, Long lessonId);

    @Query("""
                SELECT COUNT(lc) FROM LessonCompletion lc
                WHERE lc.user.userId = :userId AND lc.lesson.chapter.course.courseId = :courseId
            """)
    int countCompletedLessons(@Param("userId") Long userId, @Param("courseId") Long courseId);

}
