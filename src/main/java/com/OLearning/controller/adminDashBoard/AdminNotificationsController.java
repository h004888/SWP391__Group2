package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.dto.notification.NotificationDropdownDTO;
import com.OLearning.entity.Notification;
import com.OLearning.mapper.notification.NotificationMapper;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.notification.NotificationService;
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
@RequestMapping("/admin/notifications")
public class AdminNotificationsController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationMapper notificationsMapper;
    @Autowired
    private CourseReviewRepository courseReviewRepository;

    @GetMapping
    public String viewNotifications(Authentication authentication, Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "5") int size,
                                   @RequestParam(value = "type", required = false) List<String> types,
                                   @RequestParam(value = "status", required = false) String status) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        if (types == null || types.isEmpty()) {
            types = List.of("REPORT_COURSE", "INSTRUCTOR_REPLY_BLOCK", "REPORT_COMMENT");
        }
        Page<NotificationDTO> notificationPage = notificationService.getNotificationsByUserId(userId, types, status, pageable);
        model.addAttribute("notifications", notificationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notificationPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("selectedTypes", types);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/adminNotificationContent :: adminNotificationContent");
        model.addAttribute("isSearch", false);
        long unreadCount = notificationService.countUnreadByUserId(userId);
        model.addAttribute("unreadCount", unreadCount);
        return "adminDashBoard/index";
    }

    @GetMapping("/search")
    public String searchNotifications(@RequestParam("keyword") String keyword,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "5") int size,
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
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/adminNotificationContent :: adminNotificationContent");
        model.addAttribute("isSearch", true);
        return "adminDashBoard/index";
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
    public String markAllAsRead(Authentication authentication) {
        // Ép kiểu để lấy ra CustomUserDetails
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        notificationService.markAllAsRead(userId);
        return "redirect:/admin/notifications";
    }

    @GetMapping("/view/{id}")
    public String viewNotificationDetail(@PathVariable("id") Long notificationId, Model model) {
        Optional<Notification> notificationOpt = notificationService.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            if (!"sent".equals(notification.getStatus())) {
                notificationService.markAsRead(notificationId);
                notification.setStatus("sent"); // update status for view
            }
            model.addAttribute("notification", notificationsMapper.toDTO(notification));
            model.addAttribute("fragmentContent", "adminDashBoard/fragments/adminNotificationDetailContent :: adminNotificationDetailContent");
            return "adminDashBoard/index";
        } else {
            return "redirect:/admindashBoard/notifications";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return "redirect:/admin/notifications";
    }

    @PostMapping("/delete-read")
    public String deleteAllReadNotifications(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        notificationService.deleteAllReadNotifications(userId);
        return "redirect:/admin/notifications";
    }

    @GetMapping("/api/latest")
    @ResponseBody
    public ResponseEntity<?> getLatestNotifications(Authentication authentication) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            
            // Lấy 5 thông báo chưa đọc
            Pageable pageable = PageRequest.of(0, 5);
            List<String> types = List.of("REPORT_COURSE", "INSTRUCTOR_REPLY_BLOCK", "REPORT_COMMENT");
            Page<NotificationDTO> notificationPage = notificationService.getUnreadNotificationsByUserId(userId, types, pageable);
            
            long unreadCount = notificationService.countUnreadByUserId(userId);

            List<NotificationDropdownDTO> dropdownList = notificationPage.getContent().stream()
                .map(n -> {
                    String msg = n.getMessage();
                    if (msg != null) {
                        msg = msg.split("\\r?\\n")[0]; // chỉ lấy dòng đầu tiên
                        if (msg.length() > 25) msg = msg.substring(0, 25) + "...";
                    }
                    
                    // Lấy lessonId nếu là notification comment
                    Long lessonId = null;
                    if ("REPORT_COMMENT".equals(n.getType()) && n.getCommentId() != null) {
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
                        lessonId
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