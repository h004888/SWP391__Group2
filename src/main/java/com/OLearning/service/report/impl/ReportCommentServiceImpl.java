package com.OLearning.service.report.impl;

import com.OLearning.dto.report.ReportCommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.User;
import com.OLearning.mapper.report.ReportCommentMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.report.ReportCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportCommentServiceImpl implements ReportCommentService {
    private final NotificationRepository notiRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final ReportCommentMapper mapper;

    public void report(ReportCommentDTO dto) {
        User user = userRepo.findById(dto.getUserId()).orElseThrow();
        Course course = courseRepo.findById(dto.getCourseId()).orElseThrow();
        // Lấy tất cả admin users (roleId = 1 là admin)
        List<User> adminUsers = userRepo.findByRoleId(1L);
        for (User admin : adminUsers) {
            Notification noti = mapper.toNotification(dto, user, course);
            noti.setUser(admin);
            noti.setMessage("Report Comment ID " + dto.getCommentId() + " by " + user.getFullName() + " (" + user.getEmail() + ") - Reason: " + dto.getReason());
            noti.setStatus("failed");
            noti.setType("REPORT_COMMENT");
            noti.setSentAt(java.time.LocalDateTime.now());
            noti.setCommentId(dto.getCommentId());
            noti.setCourse(course);
            notiRepo.save(noti);
        }
    }
}
