package com.OLearning.service.certificate.impl;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.xhtmlrenderer.pdf.ITextRenderer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.OLearning.entity.Certificate;
import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.repository.CertificateRepository;
import com.OLearning.service.user.UserService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.certificate.CertificateService;

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
        if (certificateRepository.existsByUser_UserIdAndCourse_CourseId(userId, courseId)) {
            return certificateRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId);
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

    @Override
    public Page<Certificate> findByUser_UserId(Long userUserId, Pageable pageable) {
        return certificateRepository.findByUser_UserId(userUserId, pageable);
    }

    @Override
    public Long countAllByCertificate() {

        return certificateRepository.countAllByCertificate();
    }
}