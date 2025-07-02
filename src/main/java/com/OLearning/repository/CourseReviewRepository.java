package com.OLearning.repository;

import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    // Lấy review theo course
    List<CourseReview> findByCourse(Course course);

    Optional<CourseReview> findByEnrollment(Enrollment enrollment);

    // Lấy reviews theo course và sắp xếp theo thời gian tạo mới nhất
    @Query("SELECT r FROM CourseReview r JOIN FETCH r.enrollment e JOIN FETCH e.user WHERE r.course = :course ORDER BY r.createdAt DESC")
    List<CourseReview> findByCourseWithUserOrderByCreatedAtDesc(@Param("course") Course course);

    // Lấy tất cả comment cha (không phải reply)
    List<CourseReview> findByCourseAndParentReviewIsNull(Course course);

    // Lấy tất cả reply (children) của 1 comment
    List<CourseReview> findByParentReview(CourseReview parent);

    // Lấy tất cả reply (children) của 1 comment theo thứ tự mới nhất
    @Query("SELECT r FROM CourseReview r JOIN FETCH r.enrollment e JOIN FETCH e.user WHERE r.parentReview = :parent ORDER BY r.createdAt DESC")
    List<CourseReview> findByParentReviewOrderByCreatedAtDesc(@Param("parent") CourseReview parent);

    // Fetch comment with user data for notification, bao gồm cả những review chưa có user (LEFT JOIN)
    @Query("SELECT r FROM CourseReview r LEFT JOIN FETCH r.enrollment e LEFT JOIN FETCH e.user WHERE r.enrollment.course = :course")
    List<CourseReview> findByCourseWithUserForNotification(@Param("course") Course course);
    
    void deleteByParentReview(CourseReview parentReview);

    @Query("SELECT cr FROM CourseReview cr WHERE cr.enrollment.course.instructor.userId = :instructorId ORDER BY cr.rating DESC")
    Page<CourseReview> findByInstructorId(@Param("instructorId") Long instructorId, Pageable pageable);

    @Query("SELECT cr FROM CourseReview cr LEFT JOIN FETCH cr.enrollment e LEFT JOIN FETCH e.user WHERE cr.reviewId = :reviewId")
    Optional<CourseReview> findByIdWithUser(@Param("reviewId") Long reviewId);
    
    CourseReview findByReviewId(Long id);
}
