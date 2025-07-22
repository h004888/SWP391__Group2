package com.OLearning.repository;

import com.OLearning.entity.Lesson;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Query(value = """
                DECLARE @currentLessonId INT = :currentLessonId;
                DECLARE @courseId INT = :courseID;
            
                -- 1. Lấy OrderNumber và ChapterID hiện tại
                DECLARE @currentOrder INT;
                DECLARE @currentChapterId INT;
            
                SELECT\s
                    @currentOrder = l.OrderNumber,
                    @currentChapterId = l.ChapterID
                FROM Lessons l
                WHERE l.LessonID = @currentLessonId;
            
                -- 2. Lấy bài học tiếp theo trong cùng khóa học, bất kể đã học hay chưa
                SELECT TOP 1 l.*
                FROM Lessons l
                JOIN Chapters c ON l.ChapterID = c.ChapterID
                WHERE c.CourseID = @courseId
                  AND (
                        c.ChapterID > @currentChapterId
                        OR (c.ChapterID = @currentChapterId AND l.OrderNumber > @currentOrder)
                        OR (
                            c.ChapterID = @currentChapterId AND l.OrderNumber = @currentOrder AND l.LessonID > @currentLessonId
                        )
                      )
                ORDER BY c.ChapterID ASC, l.OrderNumber ASC, l.LessonID ASC;
                
            """, nativeQuery = true)
    Lesson findNextLessonAfterCurrent(@Param("currentLessonId") Long currentLessonId,@Param("courseID") Long courseId);



    Optional<Lesson> findFirstByChapter_Course_CourseIdOrderByChapter_ChapterIdAscOrderNumberAsc(Long courseId);

    @Query(value = "SELECT * FROM Lessons WHERE chapterId = :chapterId ORDER BY orderNumber ASC", nativeQuery = true)
    List<Lesson> findByChapterId(Long chapterId);

    @Query(value = "DELETE FROM Lessons WHERE chapterId = :chapterId", nativeQuery = true)
    void deleteByChapterId(Long chapterId);

    void deleteByLessonId(Long lessonId);

    @Query("SELECT l FROM Lesson l WHERE l.chapter.course.courseId = :courseId")
    List<Lesson> findLessonsByCourseId(@Param("courseId") Long courseId);
}