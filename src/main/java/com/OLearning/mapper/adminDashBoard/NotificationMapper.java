package com.OLearning.mapper.adminDashBoard;

import com.OLearning.dto.adminDashBoard.NotificationDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notifications;
import com.OLearning.entity.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public static NotificationDTO toDTO(Notifications notification) {

        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setMessage(notification.getMessage());
        dto.setSentAt(notification.getSentAt());
        dto.setStatus(notification.isStatus());
        dto.setUserId(notification.getUser() != null ? notification.getUser().getUserId() : null);
        dto.setCourseId(notification.getCourse() != null ? notification.getCourse().getCourseId() : null);

        return dto;
    }

    public static Notifications toEntity(NotificationDTO dto, User user, Course course) {

        Notifications notification = new Notifications();
        notification.setMessage(dto.getMessage());
        notification.setSentAt(dto.getSentAt());
        notification.setStatus(dto.isStatus());
        notification.setUser(user);     // đảm bảo đã tìm user bằng service/repo trước khi truyền vào
        notification.setCourse(course); // có thể null nếu không liên quan

        return notification;
    }
}
