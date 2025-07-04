package com.OLearning.mapper.report;

import com.OLearning.dto.report.ReportCourseDTO;
import com.OLearning.dto.report.ReportCommentDTO;
import com.OLearning.entity.Report;
import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.entity.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReportMapper {
    
    public Report toReportFromCourseReport(ReportCourseDTO dto, Course course, User user, Notification notification) {
        Report report = new Report();
        report.setReportType("REPORT_COURSE");
        report.setCourse(course);
        report.setUser(user);
        report.setNotification(notification);
        report.setContent(dto.getReason());
        report.setStatus("pending");
        report.setCreatedAt(LocalDateTime.now());
        return report;
    }

    public Report toReportFromCommentReport(ReportCommentDTO dto, Course course, User user, Notification notification) {
        Report report = new Report();
        report.setReportType("REPORT_COMMENT");
        report.setCourse(course);
        report.setUser(user);
        report.setNotification(notification);
        report.setContent(dto.getReason());
        report.setStatus("pending");
        report.setCreatedAt(LocalDateTime.now());
        return report;
    }
} 