package com.OLearning.service.enrollment.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.OLearning.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.service.enrollment.EnrollmentService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private CourseService courseService;

    @Override
    public List<Course> getCoursesByUserId(Long userId) {
        return enrollmentRepository.findByUserUserId(userId)
                .stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUser_UserIdAndCourse_CourseId(userId, courseId);
    }

    @Override
    public Integer getWeeksEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.getWeeksEnrolled(userId, courseId);
    }

    @Override
    public void updateProgressByUser(Long userId, Long courseId) {
       enrollmentRepository.updateProgressByUser(userId, courseId);
    }

    @Override
    public List<Enrollment> findByEnrollmentDateAfter(LocalDate date) {
        return enrollmentRepository.findByEnrollmentDateAfter(date).stream().toList();
    }

    @Override
    public List<Enrollment> findByUserId(Long userId) {
        return enrollmentRepository.findByUser_UserId(userId).stream().toList();
    }
}
