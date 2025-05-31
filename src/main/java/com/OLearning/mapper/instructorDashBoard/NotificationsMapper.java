package com.OLearning.mapper.instructorDashBoard;

import com.OLearning.dto.instructorDashBoard.NotificationsDTO;
import com.OLearning.entity.Notifications;
import org.springframework.stereotype.Component;

@Component
public class NotificationsMapper {

    public NotificationsDTO toDTO(Notifications entity) {
        NotificationsDTO dto = new NotificationsDTO();
        dto.setNotificationId(entity.getNotificationId());
        dto.setMessage(entity.getMessage());
        dto.setSentAt(entity.getSentAt());
        dto.setType(entity.getType());
        dto.setStatus(entity.isStatus());

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getUserId());
            dto.setUserName(entity.getUser().getFullName()); // giả sử có getFullName
        }

        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getCourseId());
            dto.setCourseTitle(entity.getCourse().getTitle());
        }

        return dto;
    }

    public Notifications toEntity(NotificationsDTO dto) {
        Notifications entity = new Notifications();
        entity.setNotificationId(dto.getNotificationId());
        entity.setMessage(dto.getMessage());
        entity.setSentAt(dto.getSentAt());
        entity.setType(dto.getType());
        entity.setStatus(dto.isStatus());

        // KHÔNG map user và course ở đây nếu dùng để tạo mới (cần xử lý trong service)
        return entity;
    }
}