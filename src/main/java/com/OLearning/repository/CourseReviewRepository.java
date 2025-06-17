package com.OLearning.repository;



import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    // Lấy review theo course
    List<CourseReview> findByCourse(Course course);

    // Lấy tất cả review của 1 người dùng (nếu cần)
    List<CourseReview> findByUser(User user);

    // Kiểm tra xem user đã review course chưa
    Optional<CourseReview> findByUserAndCourse(User user, Course course);

    // Tìm review theo enrollmentID (đã sửa)
    Optional<CourseReview> findByEnrollment_EnrollmentID(Long enrollmentID);
}

