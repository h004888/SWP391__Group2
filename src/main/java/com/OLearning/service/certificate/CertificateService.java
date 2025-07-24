package com.OLearning.service.certificate;

import com.OLearning.entity.Certificate;

public interface CertificateService {
    Certificate generateCertificate(Long userId, Long courseId);

    boolean existsByUser_UserId(Long userUserId);

    Certificate findByUser_UserIdAndCourse_CourseId(Long userUserId, Long courseId);
}
