package com.OLearning.dto.notification;

import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long notificationId;
    private String message;
    private LocalDateTime sentAt;
    private String type;
    private String status;
    private User user;
    private Course course;
    private Long userId;
    private Long courseId;
    private Long commentId; // Add commentId field for comment notifications
}