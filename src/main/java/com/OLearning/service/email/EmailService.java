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
        message.setFrom("olearningwebsite@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public void sendPasswordChangedEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("olearningwebsite@gmail.com");
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
        message.setFrom("olearningwebsite@gmail.com");
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

    public void sendBecomeInstructorEmail(User user, boolean isApproved) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("olearningwebsite@gmail.com");
        message.setTo(user.getEmail());

        if (isApproved) {
            message.setSubject("Chúc mừng! Yêu cầu trở thành giảng viên đã được chấp nhận");

            String content = "Xin chào " + user.getFullName() + ",\n\n"
                    + "Chúng tôi xin chúc mừng bạn! Yêu cầu trở thành giảng viên trên hệ thống OLearning của bạn đã được **chấp nhận**.\n"
                    + "Từ bây giờ, bạn có thể tạo và quản lý các khóa học của riêng mình.\n\n"
                    + "Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ đội ngũ hỗ trợ qua email: support@olearning.com\n\n"
                    + "Trân trọng,\n"
                    + "Đội ngũ OLearning";

            message.setText(content);
        } else {
            message.setSubject("Thông báo: Yêu cầu trở thành giảng viên chưa được chấp thuận");

            String content = "Xin chào " + user.getFullName() + ",\n\n"
                    + "Chúng tôi rất tiếc phải thông báo rằng yêu cầu trở thành giảng viên trên hệ thống OLearning của bạn **chưa được chấp thuận** tại thời điểm này.\n"
                    + "Bạn có thể kiểm tra lại thông tin hồ sơ của mình hoặc liên hệ với chúng tôi để biết thêm chi tiết.\n\n"
                    + "Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ đội ngũ hỗ trợ qua email: support@olearning.com\n\n"
                    + "Trân trọng,\n"
                    + "Đội ngũ OLearning";

            message.setText(content);
        }

        mailSender.send(message);
    }

    public void sendOverdueMaintenanceEmail(String to, String instructorName, String courseTitle,
            String monthYear, Long enrollmentCount, Double maintenanceFee, String dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("olearningwebsite@gmail.com");
        message.setTo(to);
        message.setSubject("Thông báo quá hạn thanh toán phí bảo trì khóa học");

        String content = String.format(
            "Kính gửi %s,\n\n" +
            "Phí bảo trì khóa học '%s' đã quá hạn thanh toán.\n" +
            "Thông tin chi tiết:\n" +
            "- Tháng: %s\n" +
            "- Số học viên: %d\n" +
            "- Phí bảo trì: %,.0f VND\n" +
            "- Hạn thanh toán: %s\n\n" +
            "Vui lòng thanh toán sớm để tránh các biện pháp xử lý theo quy định.\n\n" +
            "Trân trọng,\n" +
            "Hệ thống OLearning",
            instructorName,
            courseTitle,
            monthYear,
            enrollmentCount,
            maintenanceFee,
            dueDate
        );

        message.setText(content);
        mailSender.send(message);
    }

    public void sendSecondReminderEmail(String to, String instructorName, String courseTitle,
            String monthYear, Long enrollmentCount, Double maintenanceFee, String dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("olearningwebsite@gmail.com");
        message.setTo(to);
        message.setSubject("Nhắc nhở lần 2: Phí bảo trì khóa học quá hạn");

        String content = String.format(
            "Kính gửi %s,\n\n" +
            "Đây là nhắc nhở lần 2 về việc phí bảo trì khóa học '%s' đã quá hạn thanh toán.\n" +
            "Thông tin chi tiết:\n" +
            "- Tháng: %s\n" +
            "- Số học viên: %d\n" +
            "- Phí bảo trì: %,.0f VND\n" +
            "- Hạn thanh toán: %s\n\n" +
            "Lưu ý: Nếu không thanh toán trong vòng 4 ngày tới, khóa học của bạn sẽ bị tạm ẩn và không cho phép học viên mới đăng ký.\n\n" +
            "Trân trọng,\n" +
            "Hệ thống OLearning",
            instructorName,
            courseTitle,
            monthYear,
            enrollmentCount,
            maintenanceFee,
            dueDate
        );

        message.setText(content);
        mailSender.send(message);
    }

    public void sendMaintenanceInvoiceEmail(String to, String instructorName, String courseTitle,
            String monthYear, Long enrollmentCount, Double maintenanceFee, String dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("olearningwebsite@gmail.com");
        message.setTo(to);
        message.setSubject("Thông báo phí bảo trì khóa học mới");

        String content = String.format(
            "Kính gửi %s,\n\n" +
            "Hệ thống đã tạo hóa đơn phí bảo trì cho khóa học '%s'.\n" +
            "Thông tin chi tiết:\n" +
            "- Tháng: %s\n" +
            "- Số học viên: %d\n" +
            "- Phí bảo trì: %,.0f VND\n" +
            "- Hạn thanh toán: %s\n\n" +
            "Vui lòng thanh toán đúng hạn để đảm bảo khóa học của bạn hoạt động bình thường.\n\n" +
            "Trân trọng,\n" +
            "Hệ thống OLearning",
            instructorName,
            courseTitle,
            monthYear,
            enrollmentCount,
            maintenanceFee,
            dueDate
        );

        message.setText(content);
        mailSender.send(message);
    }

    public void sendMessageToInstructor(String to, String instructorName, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("olearningwebsite@gmail.com");
        mailMessage.setTo(to);
        mailMessage.setSubject("Tin nhắn từ OLearning Admin");

        String content = String.format(
            "Kính gửi %s,\n\n" +
            "Bạn có tin nhắn mới từ Admin OLearning:\n\n" +
            "%s\n\n" +
            "Trân trọng,\n" +
            "Đội ngũ OLearning",
            instructorName,
            message
        );

        mailMessage.setText(content);
        mailSender.send(mailMessage);
    }
}

