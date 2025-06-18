package com.OLearning.repository;

import com.OLearning.entity.CourseReview;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    Page<CourseReview> findAll( Pageable pageable);

    @Query("SELECT cr FROM CourseReview cr WHERE cr.course.instructor.userId = :instructorId ORDER BY cr.rating DESC")
    Page<CourseReview> findByInstructorId(@Param("instructorId") Long instructorId, Pageable pageable);
}
