package com.OLearning.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.OLearning.repository.UserRepository;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object isNewUser = request.getSession().getAttribute("OAUTH2_NEW_USER");
        String email = authentication.getName();
        boolean needForceChange = false;

        if (Boolean.TRUE.equals(isNewUser)) {
            request.getSession().removeAttribute("OAUTH2_NEW_USER");
            needForceChange = true;
        } else {
            // Kiểm tra nếu là user Oauth2 và password là mặc định
            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                String password = userOpt.get().getPassword();
                if (passwordEncoder.matches("default password", password)) {
                    needForceChange = true;
                }
            }
        }

        if (needForceChange) {
            response.sendRedirect("/change-password-oauth2");
        } else {
            String loginType = request.getParameter("loginType");
            
            String referer = request.getHeader("Referer");
            boolean isAdminLoginForm = referer != null && referer.contains("/dashboard_login");

            if ("admin".equals(loginType) || isAdminLoginForm) {
                // Đăng nhập từ form admin - chỉ cho phép ADMIN và STAFF
                if (hasRole(authentication, "ROLE_ADMIN") || hasRole(authentication, "ROLE_STAFF")) {
                    response.sendRedirect(request.getContextPath() + "/admin");
                    return;
                } else {
                    // User/Instructor cố gắng đăng nhập từ form admin
                    response.sendRedirect("/dashboard_login?error=unauthorized_user_login");
                    return;
                }
            } else {
                // Đăng nhập từ form user thông thường
                if (hasRole(authentication, "ROLE_ADMIN") || hasRole(authentication, "ROLE_STAFF")) {
                    // Admin/Staff cố gắng đăng nhập từ form user
                    response.sendRedirect("/login?error=unauthorized_admin_login");
                    return;
                } else {
                    // User/Instructor đăng nhập từ form user - redirect theo role
                    redirectUserByRole(request, response, authentication);
                    return;
                }
            }
        }
    }

    private void redirectUserByRole(HttpServletRequest request, HttpServletResponse response,
                                    Authentication authentication) throws IOException {
        String redirectURL = request.getContextPath();

        if (hasRole(authentication, "ROLE_INSTRUCTOR")) {
            redirectURL += "/instructor/courses";
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