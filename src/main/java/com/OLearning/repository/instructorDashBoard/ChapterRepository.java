package com.OLearning.repository.instructorDashBoard;

import com.OLearning.entity.Chapters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapters, Long> {
    @Query(value = "SELECT * FROM Chapter WHERE courseId = :courseId ORDER BY orderNumber ASC", nativeQuery = true)
    List<Chapters> findChaptersByCourse(@Param("courseId") Long courseId);

    @Query(value = "SELECT * FROM Chapter WHERE id = :id", nativeQuery = true)
    Chapters findChapterById(@Param("id") Long id);
}
