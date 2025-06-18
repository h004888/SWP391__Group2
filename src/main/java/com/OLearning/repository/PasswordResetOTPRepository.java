package com.OLearning.repository;

import com.OLearning.entity.PasswordResetOTP;
import com.OLearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetOTPRepository extends JpaRepository<PasswordResetOTP, Long> {
    Optional<PasswordResetOTP> findByUser(User user);

    void deleteByExpiryTimeBefore(LocalDateTime expiryTime);
}