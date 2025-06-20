package com.OLearning.controller.instructorDashboard;

import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.entity.Notification;
import com.OLearning.mapper.notification.NotificationMapper;
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
import java.util.Optional;

@Controller
public class NotificationsController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationMapper notificationMapper;

    @GetMapping("/instructordashboard/notifications")
    public String viewNotifications(Authentication authentication, Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDTO> notificationPage = notificationService.getNotificationsByUserId(userId, pageable);
        model.addAttribute("notifications", notificationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notificationPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/notificationContent :: notificationContent");
        model.addAttribute("isSearch", false);
        return "instructorDashboard/index";
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
        return "instructorDashboard/index";
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
            model.addAttribute("notification", notificationMapper.toDTO(notification));
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/notificationDetailContent :: notificationDetailContent");
            return "instructorDashboard/index";
        } else {
            return "redirect:/instructordashboard/notifications";
        }
    }
}
