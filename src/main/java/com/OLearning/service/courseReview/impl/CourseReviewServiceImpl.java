package com.OLearning.service.courseReview.impl;

import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.service.courseReview.CourseReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CourseReviewServiceImpl implements CourseReviewService {


    @Autowired
    private  CourseReviewRepository courseReviewRepository;

    @Override
    public CourseReview getCourseReviews(Long id ) {
        return courseReviewRepository.findByReviewId(id);
    }

    @Override
    public Page<CourseReview> getCourseReviewsByInstructorId(Long instructorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Lấy tất cả review theo instructorId, sau đó filter rating != null
        Page<CourseReview> allReviews = courseReviewRepository.findByInstructorId(instructorId, pageable);
        List<CourseReview> filtered = allReviews.getContent().stream()
            .filter(r -> r.getRating() != null)
            .toList();
        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public List<CourseReview> getCourseReviewsByCourse(Course course) {
        return courseReviewRepository.findByCourseWithUserOrderByCreatedAtDesc(course);
    }

    @Override
    public List<CourseReview> getCourseReviewsByCourseWithUser(Course course) {
        return courseReviewRepository.findByCourseWithUserOrderByCreatedAtDesc(course);
    }

    @Override
    public List<CourseReview> getReviewsByCourseWithUser(Course course) {
        return courseReviewRepository.findReviewsByCourseWithUserOrderByCreatedAtDesc(course);
    }

    @Override
    public CourseReview save(CourseReview review) {
        return courseReviewRepository.save(review);
    }
    
    @Override
    public Optional<CourseReview> findByEnrollment(Enrollment enrollment) {
        return courseReviewRepository.findByEnrollment(enrollment);
    }
    
    @Override
    public Optional<CourseReview> findByUserIdAndCourseIdAndRatingGreaterThanZero(Long userId, Long courseId) {
        return courseReviewRepository.findByUserIdAndCourseIdAndRatingGreaterThanZero(userId, courseId);
    }
    
    @Override
    public List<CourseReview> findAllByUserIdAndCourseId(Long userId, Long courseId) {
        return courseReviewRepository.findAllByUserIdAndCourseId(userId, courseId);
    }
    
    @Override
    public Long countByUserIdAndCourseId(Long userId, Long courseId) {
        return courseReviewRepository.countByUserIdAndCourseId(userId, courseId);
    }
    
    @Override
    public Optional<CourseReview> findById(Long reviewId) {
        return courseReviewRepository.findById(reviewId);
    }
    
    @Override
    public void deleteReview(Long reviewId) {
        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy review với ID: " + reviewId));
        courseReviewRepository.delete(review);
    }
}
