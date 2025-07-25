package com.OLearning.repository;

import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    List<CourseReview> findByParentReviewOrderByCreatedAtDesc(CourseReview parentReview);
    
    // Comment methods (Rating = null) - chỉ theo lesson
    List<CourseReview> findByLessonAndRatingIsNullAndParentReviewIsNull(Lesson lesson);

    // Combined method to get both course-level and lesson-specific reviews
    @Query("SELECT cr FROM CourseReview cr WHERE cr.course = :course AND cr.rating IS NOT NULL AND cr.parentReview IS NULL AND (cr.lesson = :lesson OR cr.lesson IS NULL) ORDER BY cr.createdAt DESC")
    List<CourseReview> findReviewsByCourseAndLessonOrNullAndParentReviewIsNull(@Param("course") Course course, @Param("lesson") Lesson lesson);

    @Query("SELECT COUNT(r) FROM CourseReview r WHERE r.enrollment.user.userId = :userId AND r.course.courseId = :courseId AND r.rating > 0")
    Long countByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    // Lấy reviews theo course và sắp xếp theo thời gian tạo mới nhất
    @Query("SELECT r FROM CourseReview r JOIN FETCH r.enrollment e JOIN FETCH e.user WHERE r.course = :course ORDER BY r.createdAt DESC")
    List<CourseReview> findByCourseWithUserOrderByCreatedAtDesc(@Param("course") Course course);
    
    // Lấy reviews có rating (không phải comment thông thường) theo course
    @Query("SELECT r FROM CourseReview r JOIN FETCH r.enrollment e JOIN FETCH e.user WHERE r.course = :course AND r.rating > 0 ORDER BY r.createdAt DESC")
    List<CourseReview> findReviewsByCourseWithUserOrderByCreatedAtDesc(@Param("course") Course course);
    
    @Query("SELECT cr FROM CourseReview cr WHERE cr.enrollment.course.instructor.userId = :instructorId ORDER BY cr.rating DESC")
    Page<CourseReview> findByInstructorId(@Param("instructorId") Long instructorId, Pageable pageable);
    
    @Query("SELECT cr FROM CourseReview cr LEFT JOIN FETCH cr.enrollment e LEFT JOIN FETCH e.user WHERE cr.reviewId = :reviewId")
    Optional<CourseReview> findByIdWithUser(@Param("reviewId") Long reviewId);
    
    CourseReview findByReviewId(Long id);
}
