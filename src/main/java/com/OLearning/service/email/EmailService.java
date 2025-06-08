package com.OLearning.service.email;

import com.OLearning.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendAccountStatusEmail(User user, boolean isLocked) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(user.getEmail());
        helper.setSubject(isLocked ? "Thông báo khóa tài khoản" : "Thông báo mở khóa tài khoản");

        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("isLocked", isLocked);
        context.setVariable("supportEmail", "support@olearning.com");

        // Render HTML template
        String htmlContent = templateEngine.process("email/account-status", context);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }

    public void sendOTP(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ndthang1704@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

}

