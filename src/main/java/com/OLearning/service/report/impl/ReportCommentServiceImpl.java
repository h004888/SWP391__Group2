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
        Notification noti = mapper.toNotification(dto, user, course);
        notiRepo.save(noti);
    }
}
