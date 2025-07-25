package com.OLearning.service.certificate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.OLearning.entity.Certificate;

public interface CertificateService {
    Certificate generateCertificate(Long userId, Long courseId);

    boolean existsByUser_UserId(Long userUserId);

    Certificate findByUser_UserIdAndCourse_CourseId(Long userUserId, Long courseId);

    Page<Certificate> findByUser_UserId(Long userUserId, Pageable pageable);
}
