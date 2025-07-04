package com.OLearning.service.report.impl;

import com.OLearning.dto.report.ReportCommentDTO;
import com.OLearning.dto.report.ReportCourseDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.Report;
import com.OLearning.entity.User;
import com.OLearning.mapper.report.ReportCommentMapper;
import com.OLearning.mapper.report.ReportCourseMapper;
import com.OLearning.mapper.report.ReportMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.ReportRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
class ReportServiceImpl implements ReportService {
    private final NotificationRepository notiRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final ReportRepository reportRepo;
    private final ReportCourseMapper courseMapper;
    private final ReportCommentMapper commentMapper;
    private final ReportMapper reportMapper;

    @Override
    public void reportCourse(ReportCourseDTO dto) {
        User user = userRepo.findById(dto.getUserId()).orElseThrow();
        Course course = courseRepo.findById(dto.getCourseId()).orElseThrow();
        List<User> adminUsers = userRepo.findByRoleId(1L);
        Notification firstNoti = null;
        for (User admin : adminUsers) {
            Notification noti = courseMapper.toNotification(dto, user, course);
            noti.setUser(admin);
            noti.setMessage("Course Report: " + course.getTitle() + " - Reported by " + user.getFullName() + " (" + user.getEmail() + ") - Reason: " + dto.getReason());
            noti.setSentAt(LocalDateTime.now());
            noti.setStatus("failed");
            noti.setType("REPORT_COURSE");
            noti.setCourse(course);
            Notification savedNoti = notiRepo.save(noti);
            if (firstNoti == null) firstNoti = savedNoti;
        }
        Report report = reportMapper.toReportFromCourseReport(dto, course, user, firstNoti);
        reportRepo.save(report);
    }

    @Override
    public void reportComment(ReportCommentDTO dto) {
        User user = userRepo.findById(dto.getUserId()).orElseThrow();
        Course course = courseRepo.findById(dto.getCourseId()).orElseThrow();
        List<User> adminUsers = userRepo.findByRoleId(1L);
        Notification firstNoti = null;
        for (User admin : adminUsers) {
            Notification noti = commentMapper.toNotification(dto, user, course);
            noti.setUser(admin);
            noti.setMessage("Report Comment ID " + dto.getCommentId() + " by " + user.getFullName() + " (" + user.getEmail() + ") - Reason: " + dto.getReason());
            noti.setStatus("failed");
            noti.setType("REPORT_COMMENT");
            noti.setSentAt(LocalDateTime.now());
            noti.setCommentId(dto.getCommentId());
            noti.setCourse(course);
            Notification savedNoti = notiRepo.save(noti);
            if (firstNoti == null) firstNoti = savedNoti;
        }
        Report report = reportMapper.toReportFromCommentReport(dto, course, user, firstNoti);
        reportRepo.save(report);
    }
}
