package com.OLearning.service.report.impl;

import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.Report;
import com.OLearning.entity.User;
import com.OLearning.repository.ReportRepository;
import com.OLearning.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Override
    public Report saveReport(String reportType, Course course, User user, Notification notification, String content, String evidenceLink, String status) {
        if (reportType == null || reportType.trim().isEmpty()) {
            throw new IllegalArgumentException("Report type cannot be null or empty");
        }
        
        Report report = new Report();
        report.setReportType(reportType);
        report.setCourse(course);
        report.setUser(user);
        report.setNotification(notification);
        report.setContent(content);
        report.setEvidenceLink(evidenceLink);
        report.setStatus(status != null ? status : "pending");
        report.setCreatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }
} 