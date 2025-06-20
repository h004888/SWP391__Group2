package com.OLearning.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String loginType = request.getParameter("loginType");
        
        // Lấy referer URL để xác định form nào được sử dụng
        String referer = request.getHeader("Referer");
        boolean isAdminLoginForm = referer != null && referer.contains("/dashboard_login");

        if ("admin".equals(loginType) || isAdminLoginForm) {
            // Đăng nhập từ form admin - chỉ cho phép ADMIN
            if (hasRole(authentication, "ROLE_ADMIN")) {
                response.sendRedirect(request.getContextPath() + "/admin");
                return;
            } else {
                // User/Instructor cố gắng đăng nhập từ form admin
                response.sendRedirect("/dashboard_login?error=unauthorized_user_login");
                return;
            }
        } else {
            // Đăng nhập từ form user - không cho phép ADMIN
            if (hasRole(authentication, "ROLE_ADMIN")) {
                // Admin cố gắng đăng nhập từ form user
                response.sendRedirect("/login?error=unauthorized_admin_login");
                return;
            } else {
                // Redirect theo role của user
                redirectUserByRole(request, response, authentication);
                return;
            }
        }
    }

    private void redirectUserByRole(HttpServletRequest request, HttpServletResponse response,
                                    Authentication authentication) throws IOException {
        String redirectURL = request.getContextPath();

        if (hasRole(authentication, "ROLE_ADMIN")) {
            redirectURL += "/admin";
        } else if (hasRole(authentication, "ROLE_INSTRUCTOR")) {
            redirectURL += "/instructordashboard";
        } else if (hasRole(authentication, "ROLE_USER")) {
            redirectURL += "/home";
        } else {
            redirectURL += "/home"; // default
        }

        response.sendRedirect(redirectURL);
    }

    private boolean hasRole(Authentication auth, String roleName) {
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (authority.getAuthority().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
}