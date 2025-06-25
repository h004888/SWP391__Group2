package com.OLearning.repository;

import com.OLearning.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    @Query(value = "SELECT * FROM Chapters WHERE courseId = :courseId ORDER BY orderNumber ASC", nativeQuery = true)
    List<Chapter> findChaptersByCourse(@Param("courseId") Long courseId);

    @Query("SELECT DISTINCT c FROM Chapter c LEFT JOIN FETCH c.lessons WHERE c.course.courseId = :courseId")
    List<Chapter> findByCourseIdWithLessons(@Param("courseId") Long courseId);
    @Query(value = "SELECT * FROM Chapters WHERE  chapterId = :id", nativeQuery = true)
    Chapter findChapterById(@Param("id") Long id);
    void deleteById(Long id);
}
