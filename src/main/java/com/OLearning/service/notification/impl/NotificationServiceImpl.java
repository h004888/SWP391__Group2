package com.OLearning.service.notification.impl;

import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.User;
import com.OLearning.mapper.notification.NotificationMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private NotificationMapper notificationMapper;


    @Override
    public Notification sendMess(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void rejectCourseMess(NotificationDTO dto, boolean allowResubmission) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        String fullMessage = "Your course was rejected: " + dto.getMessage();
        if (allowResubmission) {
            fullMessage += " You may revise and resubmit.";
        } else {
            fullMessage += " You are not allowed to resubmit.";
        }

        dto.setMessage(fullMessage);
        dto.setSentAt(LocalDateTime.now());
        dto.setType("COURSE_REJECTION");
        dto.setStatus(true);

        Notification notification = notificationMapper.toEntity(dto, user, course);
        notificationRepository.save(notification);

        //set Course when reject
        course.setCanResubmit(allowResubmission);
        course.setStatus("rejected");
        courseRepository.save(course);

    }

}
