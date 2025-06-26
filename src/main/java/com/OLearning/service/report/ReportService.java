package com.OLearning.service.report;

import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.Report;
import com.OLearning.entity.User;

public interface ReportService {
    Report saveReport(String reportType, Course course, User user, Notification notification, String content, String evidenceLink, String status);
} 