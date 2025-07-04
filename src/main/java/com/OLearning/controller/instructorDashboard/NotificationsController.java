package com.OLearning.controller.instructorDashboard;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Notification;
import com.OLearning.mapper.comment.CommentMapper;
import com.OLearning.mapper.notification.NotificationMapper;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.notification.NotificationService;
import com.OLearning.service.course.CourseService;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @GetMapping("/instructordashboard/notifications")
    public String viewNotifications(Authentication authentication, Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "type", required = false) List<String> types,
                                   @RequestParam(value = "status", required = false) String status) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        // Nếu không truyền type thì lấy mặc định cho instructor
        if (types == null || types.isEmpty()) {
            types = List.of("COURSE_BLOCKED", "comment", "COURSE_UNBLOCKED", "COURSE_REJECTION", "COURSE_APPROVED", "MAINTENANCE_FEE", "COURSE_CREATED", "SUCCESSFULLY");
        }
        Page<NotificationDTO> notificationPage = notificationService.getNotificationsByUserId(userId, types, status, pageable);
        model.addAttribute("notifications", notificationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notificationPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("selectedTypes", types);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/notificationContent :: notificationContent");
        model.addAttribute("isSearch", false);
        long unreadCount = notificationService.countUnreadByUserId(userId);
        model.addAttribute("unreadCount", unreadCount);
        return "instructorDashboard/indexUpdate";
    }

    @GetMapping("/instructordashboard/notifications/search")
    public String searchNotifications(@RequestParam("keyword") String keyword,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                      Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationDTO> notificationsPage = notificationService.searchNotificationsByUser(keyword, userId, pageable);
            model.addAttribute("notifications", notificationsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", notificationsPage.getTotalPages());
            model.addAttribute("pageSize", size);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error searching notifications: " + e.getMessage());
            model.addAttribute("notifications", List.of());
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/notificationContent :: notificationContent");
        return "instructorDashboard/indexUpdate";
    }

    @PostMapping("/instructordashboard/notifications/{id}/mark-read")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok("Notification marked as read");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update");
        }
    }

    @PostMapping("/instructordashboard/notifications/mark-all-read")
    public String markAllAsRead(Authentication authentication) {
        // Ép kiểu để lấy ra CustomUserDetails
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Lấy userId từ CustomUserDetails
        Long userId = userDetails.getUserId();

        // Gọi service để đánh dấu tất cả thông báo là đã đọc
        notificationService.markAllAsRead(userId);

        // Redirect về trang thông báo (không cần truyền userId)
        return "redirect:/instructordashboard/notifications";
    }

    @GetMapping("/instructordashboard/notifications/view/{id}")
    public String viewNotificationDetail(@PathVariable("id") Long notificationId, Model model) {
        Optional<Notification> notificationOpt = notificationService.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            if (!"sent".equals(notification.getStatus())) {
                notificationService.markAsRead(notificationId);
                notification.setStatus("sent"); // update status for view
            }
            
            NotificationDTO notificationDTO = notificationMapper.toDTO(notification);
            model.addAttribute("notification", notificationDTO);
            
            // If this is a comment notification, get the comment details
            if ("comment".equals(notification.getType()) && notification.getCommentId() != null) {
                try {
                    // Get comment details with user information using the new method
                    Optional<CourseReview> commentOpt = courseReviewRepository.findByIdWithUser(notification.getCommentId());
                    if (commentOpt.isPresent()) {
                        CourseReview comment = commentOpt.get();
                        CommentDTO commentDTO = commentMapper.toDTO(comment);
                        model.addAttribute("comment", commentDTO);
                    }
                } catch (Exception e) {
                    // Log error but don't break the page
                    System.err.println("Error loading comment for notification: " + e.getMessage());
                }
            }
            
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/notificationDetailContent :: notificationDetailContent");
            return "instructorDashboard/indexUpdate";
        } else {
            return "redirect:/instructordashboard/notifications";
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
                    Optional<CourseReview> commentOpt = courseReviewRepository.findByIdWithUser(notification.getCommentId());
                    if (commentOpt.isPresent()) {
                        CourseReview comment = commentOpt.get();
                        CommentDTO commentDTO = commentMapper.toDTO(comment);
                        return ResponseEntity.ok(commentDTO);
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                } else {
                    return ResponseEntity.badRequest().body(Map.of("error", "This notification is not a comment notification"));
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error loading comment: " + e.getMessage()));
        }
    }

    @PostMapping("/instructordashboard/courses/reply-block")
    public String replyBlockCourse(@RequestParam("courseId") Long courseId,
                                   @RequestParam("notificationId") Long notificationId,
                                   @RequestParam("replyContent") String replyContent,
                                   Model model) {
        // 1. KHÔNG cập nhật notification gốc
        // 2. Gửi thông báo cho admin với nội dung phản hồi
        var notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            var notification = notificationOpt.get();
            var admins = userRepository.findByRole_RoleId(1L); // Giả sử roleId=1 là ADMIN
            for (var admin : admins) {
                var adminNoti = new com.OLearning.entity.Notification();
                adminNoti.setUser(admin);
                adminNoti.setCourse(notification.getCourse());
                adminNoti.setType("INSTRUCTOR_REPLY_BLOCK");
                adminNoti.setMessage(replyContent); // Nội dung instructor nhập
                adminNoti.setStatus("failed");
                adminNoti.setSentAt(java.time.LocalDateTime.now());
                notificationRepository.save(adminNoti);
            }
        }
        model.addAttribute("success", "Phản hồi của bạn đã được gửi thành công!");
        return "redirect:/instructordashboard/notifications";
    }

    @GetMapping("/instructordashboard/courses/view/{id}")
    public String viewCourseDetail(Model model, @PathVariable("id") Long id) {
        var optionalDetail = courseService.getDetailCourse(id);
        if (optionalDetail.isEmpty()) {
            return "redirect:/instructordashboard/notifications";
        }
        model.addAttribute("detailCourse", optionalDetail.get());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/courseDetailContent :: courseDetailContent");
        return "instructorDashboard/indexUpdate";
    }

    @PostMapping("/instructordashboard/notifications/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok("Notification deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete");
        }
    }

    @PostMapping("/instructordashboard/notifications/delete-read")
    public String deleteAllReadNotifications(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        notificationService.deleteAllReadNotifications(userId);
        return "redirect:/instructordashboard/notifications";
    }
}
