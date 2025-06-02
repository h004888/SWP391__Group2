package com.OLearning.repository.instructorDashBoard;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.OLearning.entity.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findBySearch(String search);

    List<Enrollment> findByFilter(String filter);
}
