package com.OLearning.service.courseReview.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.service.courseReview.CourseReviewService;

@Service
public class CourseReviewServiceImpl implements CourseReviewService {
    @Autowired
    private CourseReviewRepository courseReviewRepository;

}
