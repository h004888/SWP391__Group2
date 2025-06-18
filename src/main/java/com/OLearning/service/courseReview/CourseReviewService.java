package com.OLearning.service.courseReview;

import com.OLearning.entity.CourseReview;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface CourseReviewService {
Page<CourseReview> getCourseReviews( int page, int size);
Page<CourseReview> getCourseReviewsByInstructorId(Long instructorId, int page, int size);
}
