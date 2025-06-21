package com.OLearning.repository;



import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    // Lấy review theo course
    List<CourseReview> findByCourse(Course course);

    Optional<CourseReview> findByEnrollment(Enrollment enrollment);

    // Query để fetch reviews với user data
    @Query("SELECT r FROM CourseReview r JOIN FETCH r.enrollment e JOIN FETCH e.user WHERE r.course = :course ORDER BY r.createdAt DESC")
    List<CourseReview> findByCourseWithUser(@Param("course") Course course);

    // Tìm review theo enrollmentID (đã sửa)
//    Optional<CourseReview> findByEnrollment_EnrollmentID(Long enrollmentID);
//    @Query("SELECT r FROM CourseReview r JOIN FETCH r.enrollment e JOIN FETCH e.user WHERE r.course.courseId = :courseId ORDER BY r.createdAt DESC")
//    List<CourseReview> findByCourseIdWithUser(@Param("courseId") Long courseId);

    // Lấy tất cả comment cha (không phải reply)
    List<CourseReview> findByCourseAndParentReviewIsNull(Course course);

    // Lấy tất cả reply (children) của 1 comment
    List<CourseReview> findByParentReview(CourseReview parent);

}

