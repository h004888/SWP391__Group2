package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "Notifications")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String message;
    private LocalDateTime sentAt;
    private String type; // e.g., "REJECT_COURSE"
    private String status; // false = chưa đọc, true = đã đọc

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;
}
