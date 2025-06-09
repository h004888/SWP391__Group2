package com.OLearning.service.enrollment.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.service.enrollment.EnrollmentService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

}
