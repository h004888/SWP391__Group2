package com.OLearning.repository;

import com.OLearning.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Integer> {
    // Tính trung bình rating cho một course
    @Query("""
              SELECT COALESCE(AVG(r.rating), 0)
              FROM CourseReview r
              WHERE r.course.courseId = :courseId
            """)
    Double findAverageRatingByCourseId(@Param("courseId") Long courseId);

    // Đếm số review cho một course
    Long countByCourse_CourseId(Long courseId);
}
