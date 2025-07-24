package com.OLearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.OLearning.entity.Certificate;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    boolean existsByUser_UserId(Long userUserId);
    Certificate findByUser_UserIdAndCourse_CourseId(Long userUserId, Long courseId);
}
