package com.OLearning.repository;

import com.OLearning.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query(value = "SELECT * FROM Lessons WHERE chapterId = :chapterId ORDER BY orderNumber ASC", nativeQuery = true)
    List<Lesson> findByChapterId(Long chapterId);
    
    @Query(value = "DELETE FROM Lessons WHERE chapterId = :chapterId", nativeQuery = true)
    void deleteByChapterId(Long chapterId);
    
    void deleteByLessonId(Long lessonId);
}