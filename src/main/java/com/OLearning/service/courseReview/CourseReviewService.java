package com.OLearning.service.courseReview;

import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

public interface CourseReviewService {
CourseReview getCourseReviews(Long id);
Page<CourseReview> getCourseReviewsByInstructorId(Long instructorId, int page, int size);
List<CourseReview> getCourseReviewsByCourse(Course course);
List<CourseReview> getCourseReviewsByCourseWithUser(Course course);
List<CourseReview> getReviewsByCourseWithUser(Course course);
CourseReview save(CourseReview review);
Optional<CourseReview> findByEnrollment(Enrollment enrollment);
Optional<CourseReview> findByUserIdAndCourseIdAndRatingGreaterThanZero(Long userId, Long courseId);
List<CourseReview> findAllByUserIdAndCourseId(Long userId, Long courseId);
Long countByUserIdAndCourseId(Long userId, Long courseId);
Optional<CourseReview> findById(Long reviewId);
void deleteReview(Long reviewId);

}
