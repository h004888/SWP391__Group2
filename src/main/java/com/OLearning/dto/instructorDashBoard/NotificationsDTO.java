package com.OLearning.dto.instructorDashBoard;

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
public class NotificationsDTO {
    private Long notificationId;
    private String message;
    private LocalDateTime sentAt;
    private String type;
    private boolean status;
    private User user;
    private Course course;
}