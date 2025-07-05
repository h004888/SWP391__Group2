package com.OLearning.service.courseReview;

import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public interface CourseReviewService {
CourseReview getCourseReviews(Long id);
Page<CourseReview> getCourseReviewsByInstructorId(Long instructorId, int page, int size);
List<CourseReview> getCourseReviewsByCourse(Course course);
CourseReview save(CourseReview review);
Optional<CourseReview> findByEnrollment(Enrollment enrollment);

}
