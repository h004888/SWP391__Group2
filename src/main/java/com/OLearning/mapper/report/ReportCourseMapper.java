package com.OLearning.mapper.report;

import com.OLearning.dto.report.ReportCourseDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReportCourseMapper {
    public Notification toNotification(ReportCourseDTO dto, User reporter, Course course) {
        Notification noti = new Notification();
        noti.setCourse(course);
        noti.setMessage("Course Report: " + course.getTitle() + " - Reported by " + reporter.getFullName() + " (" + reporter.getEmail() + ") - Reason: " + dto.getReason());
        noti.setType("REPORT_COURSE");
        noti.setStatus("failed");
        noti.setSentAt(LocalDateTime.now());
        return noti;
    }
}
