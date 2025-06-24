package com.OLearning.repository;

import com.OLearning.entity.Lesson;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("""
                SELECT lc.lesson FROM LessonCompletion lc
                WHERE lc.user.userId = :userId AND lc.lesson.chapter.course.courseId = :courseId
                ORDER BY lc.lesson.orderNumber DESC
            """)
    List<Lesson> findCompletedLessons(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("""
                SELECT l FROM Lesson l
                WHERE l.chapter.course.courseId = :courseId
                ORDER BY l.orderNumber ASC
                LIMIT 1
            """)
    Lesson findFirstLesson(@Param("courseId") Long courseId);

    @Query("""
                SELECT l FROM Lesson l
                WHERE l.chapter.course.courseId = :courseId AND l.orderNumber = :orderNumber
            """)
    Optional<Lesson> findByCourseIdAndOrderNumber(@Param("courseId") Long courseId,
                                                  @Param("orderNumber") int orderNumber);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.chapter.course.courseId = :courseId")
    int countLessonsByCourseId(@Param("courseId") Long courseId);

    @Query(value = """
            SELECT TOP 1
              L.* 
            FROM Lessons AS L
            INNER JOIN Chapters AS C
              ON L.ChapterID = C.ChapterID
            WHERE C.CourseID = :courseId
              AND L.LessonID > :currentLessonId
            ORDER BY L.LessonID ASC
            """,
            nativeQuery = true)
    Optional<Lesson> findNextLesson(
            @Param("courseId") Long courseId,
            @Param("currentLessonId") Long currentLessonId
    );

    @Query("""
            SELECT l 
            FROM Lesson l 
            WHERE l.chapter.course.courseId = :courseId 
              AND l.lessonId NOT IN (
                SELECT lc.lesson.lessonId 
                FROM LessonCompletion lc 
                WHERE lc.user.userId = :userId
              )
            ORDER BY l.chapter.chapterId ASC, l.orderNumber ASC
            """)
    List<Lesson> findNextUncompletedLessonForUser(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId,
            Pageable pageable
    );


    Optional<Lesson> findFirstByChapter_Course_CourseIdOrderByChapter_ChapterIdAscOrderNumberAsc(Long courseId);

}