package com.OLearning.dto.adminDashBoard;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long notificationId;
    private String message;
    private LocalDateTime sentAt;
    private String type;
    private boolean status;
    private Long userId;
    private Long courseId;
}