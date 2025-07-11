package com.OLearning.config;

import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.notification.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class UserNotificationInterceptor implements HandlerInterceptor {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            // Check if the view is a user page
            String viewName = modelAndView.getViewName();
            if (viewName != null && (viewName.startsWith("userPage/") || viewName.equals("userPage/LearningDashboard"))) {
                // Get current user
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                    Long userId = userDetails.getUserId();
                    
                    // Add unread notification count to model
                    long unreadCount = notificationService.countUnreadByUserId(userId);
                    modelAndView.addObject("unreadCount", unreadCount);
                    // Thêm user vào model
                    modelAndView.addObject("user", userDetails.getUser());
                }
            }
        }
    }
} 