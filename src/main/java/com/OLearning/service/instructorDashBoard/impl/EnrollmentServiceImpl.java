package com.OLearning.service.instructorDashboard.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.OLearning.entity.Enrollment;
import com.OLearning.repository.instructorDashBoard.EnrollmentRepository;
import com.OLearning.service.instructorDashboard.EnrollmentService;
import java.util.List;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Enrollment findById(Integer id) {
        return enrollmentRepository.findById(id).orElse(null);
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public void delete(Integer id) {
        enrollmentRepository.deleteById(id);
    }

    @Override
    public List<Enrollment> findBySearch(String search) {
        return enrollmentRepository.findBySearch(search);
    }

    @Override
    public List<Enrollment> findByFilter(String filter) {
        return enrollmentRepository.findByFilter(filter);
    }
}