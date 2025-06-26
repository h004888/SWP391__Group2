package com.OLearning.service.courseReview.impl;

import com.OLearning.entity.CourseReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.service.courseReview.CourseReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
