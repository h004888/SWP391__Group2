package com.OLearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.OLearning.entity.Enrollment;

import jakarta.transaction.Transactional;
@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {


}
