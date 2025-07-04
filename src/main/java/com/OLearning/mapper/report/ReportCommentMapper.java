package com.OLearning.mapper.report;

import com.OLearning.dto.report.ReportCommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReportCommentMapper {
    public Notification toNotification(ReportCommentDTO dto, User user, Course course) {
        Notification noti = new Notification();
        noti.setUser(user);
        noti.setCourse(course);
        noti.setMessage("Report Comment ID " + dto.getCommentId() + ": " + dto.getReason());
        noti.setType("REPORT_COMMENT");
        noti.setStatus("sent");
        noti.setSentAt(LocalDateTime.now());
        noti.setCommentId(dto.getCommentId());
        return noti;
    }
}
