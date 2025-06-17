package com.OLearning.service.report.impl;

import com.OLearning.dto.report.ReportCourseDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.User;
import com.OLearning.mapper.report.ReportCourseMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.report.ReportCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportCourseServiceImpl implements ReportCourseService {
    private final NotificationRepository notiRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final ReportCourseMapper mapper;

    public void report(ReportCourseDTO dto) {
        User user = userRepo.findById(dto.getUserId()).orElseThrow();
        Course course = courseRepo.findById(dto.getCourseId()).orElseThrow();
        Notification noti = mapper.toNotification(dto, user, course);
        notiRepo.save(noti);
    }
}