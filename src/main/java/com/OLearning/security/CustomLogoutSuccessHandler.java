package com.OLearning.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String requestURI = request.getRequestURI();
        
        if (requestURI.startsWith("/instructor")) {
            response.sendRedirect("/login?logout=true");
        }
        else if (authentication != null &&
                 authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            response.sendRedirect("/dashboard_login?logout=true");
        }
        else {
            response.sendRedirect("/login?logout=true");
        }
    }
} 