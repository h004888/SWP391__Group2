package com.OLearning.service.instructorDashBoard;

import com.OLearning.dto.instructorDashBoard.NotificationsDTO;
import com.OLearning.entity.Notifications;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.Serializable;
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