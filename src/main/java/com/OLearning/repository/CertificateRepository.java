package com.OLearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.OLearning.entity.Certificate;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    boolean existsByUser_UserId(Long userUserId);

    Certificate findByUser_UserIdAndCourse_CourseId(Long userUserId, Long courseId);

    boolean existsByUser_UserIdAndCourse_CourseId(Long userUserId, Long courseId);

    Page<Certificate> findByUser_UserId(Long userUserId, Pageable pageable);

    @Query("""
        select count(c) from Certificate c
        """)
    Long countAllByCertificate();
}
