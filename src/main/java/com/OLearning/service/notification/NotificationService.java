package com.OLearning.service.notification;

import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.entity.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NotificationService {
    Notification sendMess(Notification notification);
    void rejectCourseMess(NotificationDTO dto, boolean allowResubmission);
    List<NotificationDTO> getNotificationsByUserId(Long userId);
    public List<NotificationDTO> searchNotificationsByUser(String keyword, Long userId);

    //    NotificationsDTO getNotificationDetail(Long notificationId, Long userId);
    void markAsRead(Long notificationId);
    Optional<Notification> findById(Long id);
    void markAllAsRead(Long userId);
}
