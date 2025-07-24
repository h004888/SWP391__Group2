package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.dto.notification.NotificationDropdownDTO;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Notification;
import com.OLearning.mapper.comment.CommentMapper;
import com.OLearning.mapper.notification.NotificationMapper;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.ReportRepository;
import com.OLearning.entity.Report;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.notification.NotificationService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.cloudinary.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class NotificationsController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private CourseReviewRepository courseReviewRepository;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseService courseService;
    @Autowired
    private UploadFile uploadFile;
    @Autowired
    private ReportRepository reportRepository;

    @GetMapping("/instructor/notifications")
    public String viewNotifications(Authentication authentication, Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "type", required = false) List<String> types,
            @RequestParam(value = "status", required = false) String status) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        List<String> allTypes = notificationService.getAllNotificationTypesByUserId(userId);
        if (allTypes == null || allTypes.isEmpty()) {
            allTypes = notificationService.getAllNotificationTypes();
        }
        if (types == null || types.isEmpty()) {
            types = allTypes;
        }
        if (types == null || types.isEmpty()) {
            types = List.of(""); // Đảm bảo không null để Thymeleaf render đúng
        }
        if (status == null || status.isBlank() || "All".equalsIgnoreCase(status)) {
            status = null;
        }
        Page<NotificationDTO> notificationPage = notificationService.getNotificationsByUserId(userId, types, status,
                pageable);
        model.addAttribute("notifications", notificationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notificationPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("selectedTypes", types);
        String typeQuery = types.stream().map(t -> "type=" + t).collect(Collectors.joining("&"));
        model.addAttribute("typeQuery", typeQuery);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("fragmentContent",
                "instructorDashboard/fragments/notificationContent :: notificationContent");
        model.addAttribute("isSearch", false);
        long unreadCount = notificationService.countUnreadByUserId(userId);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("allTypes", allTypes);
        return "instructorDashboard/indexUpdate";
    }

    @GetMapping("/instructor/notifications/search")
    public String searchNotifications(@RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationDTO> notificationsPage = notificationService.searchNotificationsByUser(keyword, userId,
                    pageable);
            model.addAttribute("notifications", notificationsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", notificationsPage.getTotalPages());
            model.addAttribute("pageSize", size);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error searching notifications: " + e.getMessage());
            model.addAttribute("notifications", List.of());
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", null);
        model.addAttribute("selectedTypes", null);
        model.addAttribute("isSearch", true);
        model.addAttribute("fragmentContent",
                "instructorDashboard/fragments/notificationContent :: notificationContent");
        return "instructorDashboard/indexUpdate";
    }

    @PostMapping("/instructor/notifications/{id}/mark-read")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok("Notification marked as read");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update");
        }
    }

    @PostMapping("/instructor/notifications/mark-all-read")
    public String markAllAsRead(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        notificationService.markAllAsRead(userId);
        return "redirect:/instructor/notifications";
    }

    @GetMapping("/instructor/notifications/view/{id}")
    public String viewNotificationDetail(@PathVariable("id") Long notificationId, Model model) {
        Optional<Notification> notificationOpt = notificationService.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            if (!"sent".equals(notification.getStatus())) {
                notificationService.markAsRead(notificationId);
                notification.setStatus("sent");
            }

            NotificationDTO notificationDTO = notificationMapper.toDTO(notification);
            model.addAttribute("notification", notificationDTO);
            if ("comment".equalsIgnoreCase(notification.getType()) && notification.getCommentId() != null) {
                try {
                    Optional<CourseReview> commentOpt = courseReviewRepository.findById(notification.getCommentId());
                    if (commentOpt.isPresent()) {
                        CourseReview comment = commentOpt.get();
                        CommentDTO commentDTO = commentMapper.toDTO(comment);
                        model.addAttribute("comment", commentDTO);
                    }
                } catch (Exception e) {
                    System.err.println("Error loading comment for notification: " + e.getMessage());
                }
            }

            model.addAttribute("fragmentContent",
                    "instructorDashboard/fragments/notificationDetailContent :: notificationDetailContent");
            return "instructorDashboard/indexUpdate";
        } else {
            return "redirect:/instructor/notifications";
        }
    }

    // API endpoint để lấy thông tin comment cho notification
    @GetMapping("/api/instructor/notifications/{notificationId}/comment")
    @ResponseBody
    public ResponseEntity<?> getCommentForNotification(@PathVariable Long notificationId) {
        try {
            Optional<Notification> notificationOpt = notificationService.findById(notificationId);
            if (notificationOpt.isPresent()) {
                Notification notification = notificationOpt.get();

                if ("comment".equals(notification.getType()) && notification.getCommentId() != null) {
                    Optional<CourseReview> commentOpt = courseReviewRepository
                            .findByIdWithUser(notification.getCommentId());
                    if (commentOpt.isPresent()) {
                        CourseReview comment = commentOpt.get();
                        CommentDTO commentDTO = commentMapper.toDTO(comment);
                        return ResponseEntity.ok(commentDTO);
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                } else {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "This notification is not a comment notification"));
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error loading comment: " + e.getMessage()));
        }
    }

    @PostMapping("/instructor/courses/reply-block")
    public String replyBlockCourse(@RequestParam("courseId") Long courseId,
            @RequestParam("notificationId") Long notificationId,
            @RequestParam("replyContent") String replyContent,
            @RequestParam(value = "evidenceFile", required = false) MultipartFile evidenceFile,
            Model model) {
        String evidenceLink = null;
        if (evidenceFile != null && !evidenceFile.isEmpty()) {
            try {
                String contentType = evidenceFile.getContentType();
                if (contentType != null && contentType.startsWith("image/")) {
                    evidenceLink = uploadFile.uploadImageFile(evidenceFile);
                } else if (contentType != null && contentType.startsWith("video/")) {
                    evidenceLink = uploadFile.uploadVideoFile(evidenceFile);
                } else {
                    model.addAttribute("error", "Chỉ hỗ trợ upload ảnh hoặc video.");
                    return "redirect:/instructor/notifications";
                }
            } catch (IOException e) {
                model.addAttribute("error", "Lỗi upload file: " + e.getMessage());
                return "redirect:/instructor/notifications";
            }
        }
        var notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            var notification = notificationOpt.get();
            var admins = userRepository.findByRole_RoleId(1L); // Giả sử roleId=1 là ADMIN
            for (var admin : admins) {
                var adminNoti = new Notification();
                adminNoti.setUser(admin);
                adminNoti.setCourse(notification.getCourse());
                adminNoti.setType("INSTRUCTOR_REPLY_BLOCK");
                adminNoti.setMessage(replyContent); // Nội dung instructor nhập
                adminNoti.setStatus("failed");
                adminNoti.setSentAt(java.time.LocalDateTime.now());
                // KHÔNG setEvidenceLink ở đây!
                notificationRepository.save(adminNoti);
            }
            // Lưu evidenceLink vào tất cả report liên quan đến courseId
            List<Report> relatedReports = reportRepository.findByCourse_CourseId(courseId);
            if (relatedReports != null && evidenceLink != null && !evidenceLink.isEmpty()) {
                for (Report report : relatedReports) {
                    report.setEvidenceLink(evidenceLink);
                    reportRepository.save(report);
                }
            }
        }
        model.addAttribute("success", "Phản hồi của bạn đã được gửi thành công!");
        return "redirect:/instructor/notifications";
    }

    @GetMapping("/instructor/courses/view/{id}")
    public String viewCourseDetail(Model model, @PathVariable("id") Long id) {
        var optionalDetail = courseService.getDetailCourse(id);
        if (optionalDetail.isEmpty()) {
            return "redirect:/instructor/notifications";
        }
        model.addAttribute("detailCourse", optionalDetail.get());
        model.addAttribute("fragmentContent",
                "instructorDashboard/fragments/courseDetailContent :: courseDetailContent");
        return "instructorDashboard/indexUpdate";
    }

    @PostMapping("/instructor/notifications/{id}/delete")
    public String deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return "redirect:/instructor/notifications";
    }

    @PostMapping("/instructor/notifications/delete-read")
    public String deleteAllReadNotifications(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        notificationService.deleteAllReadNotifications(userId);
        return "redirect:/instructor/notifications";
    }

    @GetMapping("/api/instructor/latest")
    @ResponseBody
    public ResponseEntity<?> getLatestNotifications(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            Pageable pageable = PageRequest.of(0, 5);
            List<String> types = notificationService.getAllNotificationTypesByUserId(userId);
            Page<NotificationDTO> notificationPage = notificationService.getUnreadNotificationsByUserId(userId, types,
                    pageable);
            long unreadCount = notificationService.countUnreadByUserId(userId);
            List<NotificationDropdownDTO> dropdownList = notificationPage.getContent().stream()
                    .map(n -> {
                        String msg = n.getMessage();
                        if (msg != null) {
                            msg = msg.split("\\r?\\n")[0]; // chỉ lấy dòng đầu tiên
                            if (msg.length() > 25)
                                msg = msg.substring(0, 25) + "...";
                        }

                        // Lấy lessonId nếu là notification comment
                        Long lessonId = null;
                        if ("comment".equalsIgnoreCase(n.getType()) && n.getCommentId() != null) {
                            var commentOpt = courseReviewRepository.findById(n.getCommentId());
                            if (commentOpt.isPresent() && commentOpt.get().getLesson() != null) {
                                lessonId = commentOpt.get().getLesson().getLessonId();
                            }
                        }

                        return new NotificationDropdownDTO(
                                n.getNotificationId(),
                                msg,
                                n.getType(),
                                n.getStatus(),
                                n.getSentAt(),
                                n.getCommentId(),
                                n.getCourse() != null ? n.getCourse().getCourseId() : null,
                                lessonId);
                    }).toList();
            return ResponseEntity.ok(Map.of(
                    "notifications", dropdownList,
                    "unreadCount", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error loading notifications: " + e.getMessage()));
        }
    }
}
