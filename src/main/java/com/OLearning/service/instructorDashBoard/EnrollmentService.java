package com.OLearning.service.instructorDashboard;

import java.util.List;

import org.springframework.stereotype.Service;

import com.OLearning.entity.Enrollment;

@Service
public interface EnrollmentService {

    List<Enrollment> findAll();
    
    Enrollment findById(Integer id);

    Enrollment save(Enrollment enrollment);

    void delete(Integer id);

    List<Enrollment> findBySearch(String search);

    List<Enrollment> findByFilter(String filter);
}
