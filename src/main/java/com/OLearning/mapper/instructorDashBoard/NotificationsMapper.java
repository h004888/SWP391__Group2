package com.OLearning.mapper.instructorDashBoard;

import com.OLearning.dto.instructorDashBoard.NotificationsDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notifications;
import com.OLearning.entity.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationsMapper {

    public  NotificationsDTO toDTO(Notifications notification) {

        NotificationsDTO dto = new NotificationsDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setMessage(notification.getMessage());
        dto.setSentAt(notification.getSentAt());
        dto.setType(notification.getType());
        dto.setStatus(notification.getStatus());
        dto.setUser(notification.getUser());
        dto.setCourse(notification.getCourse());

        return dto;
    }

    public Notifications toEntity(NotificationsDTO dto, User user, Course course) {

        Notifications notification = new Notifications();
        notification.setMessage(dto.getMessage());
        notification.setSentAt(dto.getSentAt());
        notification.setType(dto.getType());
        notification.setStatus(dto.getStatus());
        notification.setUser(user);
        notification.setCourse(course); // có thể null nếu không liên quan

        return notification;
    }
}