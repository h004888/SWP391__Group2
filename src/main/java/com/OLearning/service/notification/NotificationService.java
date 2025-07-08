package com.OLearning.service.notification;

import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NotificationService {
    Notification sendMess(Notification notification);
    void rejectCourseMess(NotificationDTO dto, boolean allowResubmission);
    List<NotificationDTO> getNotificationsByUserId(Long userId);
    Page<NotificationDTO> getNotificationsByUserId(Long userId, Pageable pageable);
    Page<NotificationDTO> getNotificationsByUserId(Long userId, List<String> types, String status, Pageable pageable);
    Page<NotificationDTO> getUnreadNotificationsByUserId(Long userId, List<String> types, Pageable pageable);
    public List<NotificationDTO> searchNotificationsByUser(String keyword, Long userId);
    Page<NotificationDTO> searchNotificationsByUser(String keyword, Long userId, Pageable pageable);

    //    NotificationsDTO getNotificationDetail(Long notificationId, Long userId);
    void markAsRead(Long notificationId);
    Optional<Notification> findById(Long id);
    void markAllAsRead(Long userId);
    void deleteNotification(Long id);
    void deleteAllReadNotifications(Long userId);
    long countUnreadByUserId(Long userId);
}
