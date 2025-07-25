package com.OLearning.service.certificate.impl;

import com.OLearning.entity.Certificate;
import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.repository.CertificateRepository;
import com.OLearning.service.certificate.CertificateService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;

    @Override
    public Certificate generateCertificate(Long userId, Long courseId) {
        Course course = courseService.findById(courseId);
        User user = userService.findById(userId);
        if (course == null || user == null) {
            throw new RuntimeException("Course or user not found");
        }
        if (existsByUser_UserId(userId)) {
            throw new RuntimeException("User already has a certificate");
        }

        String certificateCode = UUID.randomUUID().toString();
        Certificate certificate = new Certificate();
        certificate.setUser(user);
        certificate.setCourse(course);
        certificate.setIssueDate(new Date());
        certificate.setCertificateCode(certificateCode);
        certificate.setFileUrl("/certificates/" + certificateCode + ".pdf");
        return certificateRepository.save(certificate);
    }

    @Override
    public boolean existsByUser_UserId(Long userUserId) {
        return certificateRepository.existsByUser_UserId(userUserId);
    }

    @Override
    public Certificate findByUser_UserIdAndCourse_CourseId(Long userUserId, Long courseId) {
        return certificateRepository.findByUser_UserIdAndCourse_CourseId(userUserId, courseId);
    }

}
