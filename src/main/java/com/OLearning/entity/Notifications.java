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

    @Column(columnDefinition = "nvarchar(max)")
    private String message;
    private LocalDateTime sentAt;
    private String type;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

}
