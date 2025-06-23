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
        
        // Nếu logout từ instructordashboard thì redirect về /login
        if (requestURI.startsWith("/instructordashboard")) {
            response.sendRedirect("/login?logout=true");
        }
        // Nếu logout từ admin dashboard và user là admin thì redirect về /dashboard_login
        else if (requestURI.startsWith("/admin") && authentication != null && 
                 authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            response.sendRedirect("/dashboard_login?logout=true");
        }
        // Các trường hợp khác redirect về /login
        else {
            response.sendRedirect("/login?logout=true");
        }
    }
} 