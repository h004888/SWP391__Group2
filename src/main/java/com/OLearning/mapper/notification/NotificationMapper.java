package com.OLearning.mapper.notification;

import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public static NotificationDTO toDTO(Notification notification) {

        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setMessage(notification.getMessage());
        dto.setSentAt(notification.getSentAt());
        dto.setType(notification.getType());
        dto.setStatus(notification.isStatus());
        dto.setUserId(notification.getUser() != null ? notification.getUser().getUserId() : null);
        dto.setCourseId(notification.getCourse() != null ? notification.getCourse().getCourseId() : null);

        return dto;
    }

    public static Notification toEntity(NotificationDTO dto, User user, Course course) {

        Notification notification = new Notification();
        notification.setMessage(dto.getMessage());
        notification.setSentAt(dto.getSentAt());
        notification.setType(dto.getType());
        notification.setStatus(dto.isStatus());
        notification.setUser(user);
        notification.setCourse(course); // có thể null nếu không liên quan

        return notification;
    }
}