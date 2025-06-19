package com.OLearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.OLearning.entity.Chapter;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    @Query("SELECT DISTINCT c FROM Chapter c LEFT JOIN FETCH c.lessons WHERE c.course.courseId = :courseId")
    List<Chapter> findByCourseIdWithLessons(@Param("courseId") Long courseId);
}
