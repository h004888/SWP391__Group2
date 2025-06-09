package com.OLearning.service.notification;

import com.OLearning.dto.notification.NotificationsDTO;
import com.OLearning.entity.Notifications;

import java.util.List;
import java.util.Optional;

public interface NotificationsService {
    List<NotificationsDTO> getNotificationsByUserId(Long userId);
    List<NotificationsDTO> searchNotifications( String keyword);
//    NotificationsDTO getNotificationDetail(Long notificationId, Long userId);
    void markAsRead(Long notificationId);
    Optional<Notifications> findById(Long id);
    void markAllAsRead(Long userId);
}