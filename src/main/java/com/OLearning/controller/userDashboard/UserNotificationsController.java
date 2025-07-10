package com.OLearning.controller.userDashboard;

import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.service.notification.NotificationService;
import com.OLearning.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.dto.notification.NotificationDropdownDTO;

@Controller
@RequestMapping("/user/notifications")
public class UserNotificationsController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CourseReviewRepository courseReviewRepository;

    @GetMapping
    public String viewNotifications(Authentication authentication, Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "type", required = false) List<String> types,
                                   @RequestParam(value = "status", required = false) String status,
                                   @RequestParam(value = "search", required = false) String search) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        if (types == null || types.isEmpty()) {
            types = List.of("ENROLLMENT", "COURSE_COMPLETION", "QUIZ_RESULT", "CERTIFICATE", "PAYMENT_SUCCESS", "PAYMENT_FAILED", "COMMENT", "COMMENT_HIDDEN");
        }
        Page<NotificationDTO> notificationPage;
        if (search != null && !search.isBlank()) {
            notificationPage = notificationService.searchNotificationsByUser(search, userId, pageable);
        } else {
            notificationPage = notificationService.getNotificationsByUserId(userId, types, status, pageable);
        }
        model.addAttribute("notifications", notificationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notificationPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("selectedTypes", types);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("search", search);
        long unreadCount = notificationService.countUnreadByUserId(userId);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("user", userDetails.getUser());
        // --- Thêm map notificationId -> lessonId cho notification comment ---
        java.util.Map<Long, Long> lessonIdMap = new java.util.HashMap<>();
        for (var n : notificationPage.getContent()) {
            if ("COMMENT".equals(n.getType()) && n.getCommentId() != null) {
                var commentOpt = courseReviewRepository.findById(n.getCommentId());
                if (commentOpt.isPresent() && commentOpt.get().getLesson() != null) {
                    lessonIdMap.put(n.getNotificationId(), commentOpt.get().getLesson().getLessonId());
                }
            }
        }
        model.addAttribute("lessonIdMap", lessonIdMap);
        model.addAttribute("fragmentContent", "homePage/fragments/notificationsContent :: notificationsContent");
        return "homePage/index";
    }

    @PostMapping("/{id}/mark-read")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok("Notification marked as read");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update");
        }
    }

    @PostMapping("/mark-all-read")
    @ResponseBody
    public ResponseEntity<?> markAllAsRead(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok("All notifications marked as read");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update");
        }
    }

    @GetMapping("/view/{id}")
    public String viewNotificationDetail(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            var notificationOpt = notificationService.findById(id);
            if (notificationOpt.isPresent() && notificationOpt.get().getUser().getUserId().equals(userId)) {
                // Mark as read if not already
                if (!"sent".equals(notificationOpt.get().getStatus())) {
                    notificationService.markAsRead(id);
                }
                NotificationDTO notificationDTO = new NotificationDTO();
                var notification = notificationOpt.get();
                notificationDTO.setNotificationId(notification.getNotificationId());
                notificationDTO.setMessage(notification.getMessage());
                notificationDTO.setSentAt(notification.getSentAt());
                notificationDTO.setType(notification.getType());
                notificationDTO.setStatus("sent"); // ensure status is 'sent' for view
                notificationDTO.setUser(notification.getUser());
                notificationDTO.setCourse(notification.getCourse());
                notificationDTO.setCommentId(notification.getCommentId());
                // Lấy lessonId nếu là notification comment
                Long lessonId = null;
                if (notification.getCommentId() != null) {
                    var commentOpt = courseReviewRepository.findById(notification.getCommentId());
                    if (commentOpt.isPresent() && commentOpt.get().getLesson() != null) {
                        lessonId = commentOpt.get().getLesson().getLessonId();
                    }
                }
                model.addAttribute("notification", notificationDTO);
                model.addAttribute("lessonId", lessonId);
                model.addAttribute("user", userDetails.getUser());
                 model.addAttribute("fragmentContent", "homePage/fragments/notificationsDetailContent :: notificationsDetailContent");
                return "homePage/index";
            } else {
                return "redirect:/user/notifications";
            }
        } catch (Exception e) {
            return "redirect:/user/notifications";
        }
    }

    @PostMapping("/delete-all-read")
    @ResponseBody
    public ResponseEntity<?> deleteAllReadNotifications(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            notificationService.deleteAllReadNotifications(userId);
            return ResponseEntity.ok("All read notifications deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete read notifications");
        }
    }

    // API endpoint để lấy 5 thông báo chưa đọc cho dropdown
    @GetMapping("/api/latest")
    @ResponseBody
    public ResponseEntity<?> getLatestNotifications(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            Pageable pageable = PageRequest.of(0, 5);
            List<String> types = List.of("ENROLLMENT", "COURSE_COMPLETION", "QUIZ_RESULT", "CERTIFICATE", "PAYMENT_SUCCESS", "PAYMENT_FAILED", "COMMENT", "COMMENT_HIDDEN");
            Page<NotificationDTO> notificationPage = notificationService.getUnreadNotificationsByUserId(userId, types, pageable);
            long unreadCount = notificationService.countUnreadByUserId(userId);
            // Chuyển sang NotificationDropdownDTO, rút gọn message chỉ 25 ký tự
            List<NotificationDropdownDTO> dropdownList = notificationPage.getContent().stream()
                .map(n -> {
                    String msg = n.getMessage();
                    if (msg != null) {
                        msg = msg.split("\\r?\\n")[0]; // chỉ lấy dòng đầu tiên
                        if (msg.length() > 25) msg = msg.substring(0, 25) + "...";
                    }
                    return new NotificationDropdownDTO(
                        n.getNotificationId(),
                        msg,
                        n.getType(),
                        n.getStatus(),
                        n.getSentAt()
                    );
                }).toList();
            return ResponseEntity.ok(Map.of(
                "notifications", dropdownList,
                "unreadCount", unreadCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error loading notifications: " + e.getMessage()));
        }
    }
} 