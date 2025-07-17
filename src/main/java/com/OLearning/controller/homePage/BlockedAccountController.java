package com.OLearning.controller.homePage;

import com.OLearning.entity.User;
import com.OLearning.entity.Notification;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class BlockedAccountController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/account-blocked")
    public String accountBlockedPage(@RequestParam(value = "sent", required = false) String sent, Model model) {
        model.addAttribute("success", sent != null);
        return "loginPage/accountBlocked";
    }

    @PostMapping("/account-blocked/appeal")
    public String submitAppeal(@RequestParam String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            User user = userRepository.findByEmail(auth.getName()).orElse(null);
            if (user != null) {
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setMessage("Appeal from blocked account: " + message);
                notification.setType("BLOCKED_ACCOUNT_APPEAL");
                notification.setStatus("failed");
                notification.setSentAt(LocalDateTime.now());
                notificationService.sendMess(notification);
            }
        }
        return "redirect:/account-blocked?sent=true";
    }
} 