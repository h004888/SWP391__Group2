package com.OLearning.dto.notification;

import java.time.LocalDateTime;

public class NotificationDropdownDTO {
    private Long notificationId;
    private String message;
    private String type;
    private String status;
    private LocalDateTime sentAt;

    public NotificationDropdownDTO() {}

    public NotificationDropdownDTO(Long notificationId, String message, String type, String status, LocalDateTime sentAt) {
        this.notificationId = notificationId;
        this.message = message;
        this.type = type;
        this.status = status;
        this.sentAt = sentAt;
    }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
} 