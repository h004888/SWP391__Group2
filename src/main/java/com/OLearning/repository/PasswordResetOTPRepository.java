package com.OLearning.repository;

import com.OLearning.entity.PasswordResetOTP;
import com.OLearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetOTPRepository extends JpaRepository<PasswordResetOTP, Long> {
    Optional<PasswordResetOTP> findByUser(User user);

    void deleteByExpiryTimeBefore(LocalDateTime expiryTime);
    
    // Xóa OTP theo user ID để tránh constraint violation
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetOTP p WHERE p.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
