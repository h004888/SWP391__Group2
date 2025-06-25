package com.OLearning.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String referer = request.getHeader("Referer");
        boolean isAdminLoginForm = referer != null && referer.contains("/dashboard_login");
        
        String loginType = request.getParameter("loginType");
        
        if ("admin".equals(loginType) || isAdminLoginForm) {
            response.sendRedirect("/dashboard_login?error=true");
        } else {
            response.sendRedirect("/login?error=true");
        }
    }
} 