package com.OLearning.service.instructorDashBoard;

import com.OLearning.dto.instructorDashBoard.NotificationsDTO;
import com.OLearning.entity.Notifications;

import java.util.List;

public interface NotificationsService {
    List<NotificationsDTO> getNotificationsByUserId(Long userId);
    List<NotificationsDTO> searchNotifications( String keyword);
    NotificationsDTO getNotificationDetail(Long notificationId, Long userId);
}