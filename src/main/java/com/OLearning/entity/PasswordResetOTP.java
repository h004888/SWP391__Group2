package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PasswordResetOTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_userId", nullable = false)
    private User user;

    private String otp;
    private LocalDateTime expiryTime;
    private int attempts = 0;
    private boolean verified = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
