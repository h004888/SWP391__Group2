package com.OLearning.controller.instructorDashboard;

import com.OLearning.dto.instructorDashBoard.NotificationsDTO;
import com.OLearning.entity.Notifications;
import com.OLearning.mapper.instructorDashBoard.NotificationsMapper;
import com.OLearning.service.instructorDashBoard.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
public class NotificationsController {

    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private NotificationsMapper notificationsMapper;

    @GetMapping("/instructordashboard/notifications")
    public String viewNotifications(@RequestParam(name = "userId", defaultValue = "2") Long userId,
                                    Model model) {
        List<NotificationsDTO> notifications = notificationsService.getNotificationsByUserId(userId);
        model.addAttribute("notifications", notifications);
        return "instructorDashboard/notifications";
    }

    @GetMapping("/notifications/search")
    public String searchNotifications(@RequestParam("keyword") String keyword,
                                      Model model) {
        try {
            List<NotificationsDTO> notifications = notificationsService.searchNotifications(keyword);
            model.addAttribute("notifications", notifications);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error searching notifications: " + e.getMessage());
            model.addAttribute("notifications", List.of());
        }
        model.addAttribute("keyword", keyword);
        return "instructorDashboard/notifications";
    }
    @PostMapping("/instructordashboard/notifications/{id}/mark-read")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            notificationsService.markAsRead(id);
            return ResponseEntity.ok("Notification marked as read");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update");
        }
    }
    @PostMapping("/instructordashboard/notifications/mark-all-read")
    public String markAllAsRead(@RequestParam(name = "userId", defaultValue = "2") Long userId) {
        notificationsService.markAllAsRead(userId);
        return "redirect:/instructordashboard/notifications?userId=" + userId;
    }
    @GetMapping("/instructordashboard/notifications/view/{id}")
    public String viewNotificationDetail(@PathVariable("id") Long notificationId, Model model) {
        Optional<Notifications> notificationOpt = notificationsService.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notifications notification = notificationOpt.get();
            if (!"sent".equals(notification.getStatus())) {
                notificationsService.markAsRead(notificationId);
                notification.setStatus("sent"); // update status for view
            }
            model.addAttribute("notification", notificationsMapper.toDTO(notification));
            return "instructorDashboard/notificationsDetail";
        } else {
            return "redirect:/instructordashboard/notifications";
        }
    }
}
