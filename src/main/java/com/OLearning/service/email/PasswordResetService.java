package com.OLearning.service.email;

import com.OLearning.entity.PasswordResetOTP;
import com.OLearning.entity.User;
import com.OLearning.repository.PasswordResetOTPRepository;
import com.OLearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetOTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_OTP_ATTEMPTS = 3;

    public void generateOTP(String email) {
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Generate secure OTP
        String otp = generateSecureOTP();

        // Delete existing OTP for this user to prevent multiple active OTPs
        otpRepository.findByUser(user).ifPresent(otpRepository::delete);

        // Create new OTP record
        PasswordResetOTP resetOtp = new PasswordResetOTP();
        resetOtp.setUser(user);
        resetOtp.setOtp(otp);
        resetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        resetOtp.setAttempts(0); // Initialize attempt counter
        resetOtp.setCreatedAt(LocalDateTime.now());

        otpRepository.save(resetOtp);

        // Send OTP via email
        String subject = "Khôi phục mật khẩu - OTP";
        String message = OTPEmailMessage(user.getFullName(), otp);

        try {
            emailService.sendOTP(user.getEmail(), subject, message);
        } catch (Exception e) {
            // If email sending fails, delete the OTP record
            otpRepository.delete(resetOtp);
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }

    public boolean verifyOTP(String email, String inputOtp) {
        if (email == null || inputOtp == null || inputOtp.trim().isEmpty()) {
            return false;
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElse(null);

        if (user == null) {
            return false;
        }

        Optional<PasswordResetOTP> resetOtpOpt = otpRepository.findByUser(user);
        if (resetOtpOpt.isEmpty()) {
            return false;
        }

        PasswordResetOTP resetOtp = resetOtpOpt.get();

        // Check if OTP is expired
        if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(resetOtp);
            return false;
        }

        // Check if max attempts exceeded
        if (resetOtp.getAttempts() >= MAX_OTP_ATTEMPTS) {
            otpRepository.delete(resetOtp);
            return false;
        }

        // Increment attempt counter
        resetOtp.setAttempts(resetOtp.getAttempts() + 1);
        otpRepository.save(resetOtp);

        // Verify OTP
        if (!resetOtp.getOtp().equals(inputOtp.trim())) {
            return false;
        }

        // OTP is valid - mark as verified but don't delete yet
        resetOtp.setVerified(true);
        otpRepository.save(resetOtp);

        return true;
    }

    public String resetPassword(String email, String otp, String newPassword) {
        // Validate inputs
        if (email == null || otp == null || newPassword == null) {
            return "Thông tin không hợp lệ!";
        }

        if (newPassword.trim().length() < 8) {
            return "Mật khẩu phải có ít nhất 8 ký tự!";
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElse(null);

        if (user == null) {
            return "Email không tồn tại!";
        }

        PasswordResetOTP resetOtp = otpRepository.findByUser(user)
                .orElse(null);

        if (resetOtp == null) {
            return "Không tìm thấy OTP hoặc OTP đã hết hạn!";
        }

        // Check if OTP is expired
        if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(resetOtp);
            return "OTP đã hết hạn!";
        }

        // Check if OTP matches
        if (!resetOtp.getOtp().equals(otp.trim())) {
            return "Sai mã OTP!";
        }

        // Check if OTP was verified (if using separate verification step)
        if (!resetOtp.isVerified()) {
            return "OTP chưa được xác thực!";
        }

        try {
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword.trim()));
            userRepository.save(user);

            // Delete OTP record after successful password reset
            otpRepository.delete(resetOtp);

            return "Đổi mật khẩu thành công!";
        } catch (Exception e) {
            return "Có lỗi xảy ra khi đổi mật khẩu. Vui lòng thử lại!";
        }
    }

    public void cleanupExpiredOTPs() {
        otpRepository.deleteByExpiryTimeBefore(LocalDateTime.now());
    }

    /**
     * Check if user has pending OTP
     */
    public boolean hasPendingOTP(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElse(null);

        if (user == null) {
            return false;
        }

        Optional<PasswordResetOTP> resetOtp = otpRepository.findByUser(user);
        return resetOtp.isPresent() &&
                resetOtp.get().getExpiryTime().isAfter(LocalDateTime.now());
    }

    private String generateSecureOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit number
        return String.valueOf(otp);
    }

    private String OTPEmailMessage(String fullName, String otp) {
        String format = String.format(
                "Xin chào %s,\n\n" +
                        "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản OLearning.\n\n" +
                        "Mã OTP của bạn là: %s\n\n" +
                        "Mã này có hiệu lực trong %d phút.\n\n" +
                        "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                        "Trân trọng,\n" +
                        "Đội ngũ OLearning",
                fullName != null ? fullName : "Người dùng",
                otp,
                OTP_EXPIRY_MINUTES
        );
        return format;
    }
}

