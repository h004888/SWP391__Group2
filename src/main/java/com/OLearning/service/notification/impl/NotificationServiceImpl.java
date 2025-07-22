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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        User user = userRepository.findById(dto.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(dto.getCourse().getCourseId())
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
        dto.setStatus("failed");

        Notification notification = notificationMapper.toEntity(dto, user, course);
        notificationRepository.save(notification);

        //set Course when reject
        course.setCanResubmit(allowResubmission);
        course.setStatus("rejected");
        courseRepository.save(course);

    }
    @Override
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUser_UserIdOrderByUnreadFirstAndSentAtDesc(userId)
                .stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDTO> searchNotificationsByUser(String keyword, Long userId) {
        List<Notification> entities = notificationRepository.findByUserIdAndKeywordUnreadFirst(userId, keyword);
        return entities.stream().map(notificationMapper::toDTO).collect(Collectors.toList());
    }


    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus("sent"); // true = read
            notificationRepository.save(notification);
        });

    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }
    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    public Page<NotificationDTO> getNotificationsByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByUser_UserIdOrderByUnreadFirstAndSentAtDesc(userId, pageable)
                .map(notificationMapper::toDTO);
    }

    @Override
    public Page<NotificationDTO> searchNotificationsByUser(String keyword, Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndKeywordUnreadFirst(userId, keyword, pageable)
                .map(notificationMapper::toDTO);
    }

    @Override
    public Page<NotificationDTO> getNotificationsByUserId(Long userId, List<String> types, String status, Pageable pageable) {
        return notificationRepository.findByUserIdAndTypesAndStatus(userId, types, status, pageable)
                .map(notificationMapper::toDTO);
    }

    @Override
    public Page<NotificationDTO> getUnreadNotificationsByUserId(Long userId, List<String> types, Pageable pageable) {
        return notificationRepository.findUnreadByUserIdAndTypes(userId, types, pageable)
                .map(notificationMapper::toDTO);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllReadNotifications(Long userId) {
        notificationRepository.deleteByUser_UserIdAndStatus(userId, "sent");
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public List<String> getAllNotificationTypes() {
        return notificationRepository.findAllNotificationTypes();
    }

    @Override
    public List<String> getAllNotificationTypesByUserId(Long userId) {
        return notificationRepository.findAllNotificationTypesByUserId(userId);
    }
}
