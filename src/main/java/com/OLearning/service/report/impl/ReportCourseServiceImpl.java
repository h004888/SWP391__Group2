package com.OLearning.service.report.impl;

import com.OLearning.dto.report.ReportCourseDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.Report;
import com.OLearning.entity.User;
import com.OLearning.mapper.report.ReportCourseMapper;
import com.OLearning.mapper.report.ReportMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.ReportRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.report.ReportCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportCourseServiceImpl implements ReportCourseService {
    private final NotificationRepository notiRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final ReportRepository reportRepo;
    private final ReportCourseMapper courseMapper;
    private final ReportMapper reportMapper;

    public void report(ReportCourseDTO dto) {
        User user = userRepo.findById(dto.getUserId()).orElseThrow();
        Course course = courseRepo.findById(dto.getCourseId()).orElseThrow();
        
        // Lấy tất cả admin users (roleId = 1 là admin)
        List<User> adminUsers = userRepo.findByRoleId(1L);
        
        // Gửi thông báo cho tất cả admin
        for (User admin : adminUsers) {
            Notification noti = courseMapper.toNotification(dto, user, course);
            noti.setUser(admin);
            noti.setMessage("Course Report: " + course.getTitle() + " - Reported by " + user.getFullName() + " (" + user.getEmail() + ") - Reason: " + dto.getReason());
            noti.setSentAt(LocalDateTime.now());
            noti.setStatus("failed");
            noti.setType("REPORT_COURSE");
            noti.setCourse(course);// Set admin là người nhận thông báo
            notiRepo.save(noti);
            

            Report report = reportMapper.toReportFromCourseReport(dto, course, user, noti);
            report.setReportType("REPORT_COURSE");
            report.setStatus("pending");
            report.setCreatedAt(LocalDateTime.now());
            report.setCourse(course);
            report.setUser(user);
            report.setContent(dto.getReason());
            reportRepo.save(report);
        }
    }
}