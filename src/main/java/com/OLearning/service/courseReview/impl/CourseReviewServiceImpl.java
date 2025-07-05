package com.OLearning.service.courseReview.impl;

import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
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
        return courseReviewRepository.findByInstructorId(instructorId, pageable);
    }

    @Override
    public List<CourseReview> getCourseReviewsByCourse(Course course) {
        return courseReviewRepository.findByCourseWithUserOrderByCreatedAtDesc(course);
    }

    @Override
    public CourseReview save(CourseReview review) {
        return courseReviewRepository.save(review);
    }
    
    @Override
    public Optional<CourseReview> findByEnrollment(Enrollment enrollment) {
        return courseReviewRepository.findByEnrollment(enrollment);
    }
}
