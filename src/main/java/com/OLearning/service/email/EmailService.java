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

    public void sendPasswordChangedEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ndthang1704@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Thông báo đổi mật khẩu thành công");

        String content = "Xin chào " + user.getFullName() + ",\n\n"
                + "Bạn vừa thay đổi mật khẩu tài khoản (" + user.getEmail() + ") trên hệ thống OLearning.\n"
                + "Nếu bạn không thực hiện hành động này, vui lòng liên hệ bộ phận hỗ trợ ngay qua email: support@olearning.com.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ OLearning";

        message.setText(content);
        mailSender.send(message);
    }

    public void sendPromotedToStaffEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ndthang1704@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Chúc mừng! Bạn đã trở thành nhân viên hệ thống");

        String content = "Xin chào \n\n"
                + "Chúng tôi xin chúc mừng bạn đã được cấp quyền trở thành nhân viên hệ thống OLearning.\n"
                + "Từ bây giờ, bạn có thể truy cập các chức năng quản trị phù hợp với vai trò mới của mình.\n\n"
                + "Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ đội ngũ hỗ trợ qua email: support@olearning.com\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ OLearning";

        message.setText(content);
        mailSender.send(message);
    }


}

