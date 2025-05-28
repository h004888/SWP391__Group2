package com.OLearning.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectURL = request.getContextPath();

        if (hasRole(authentication, "ROLE_ADMIN")) {
            redirectURL += "/admin";
        } else if (hasRole(authentication, "ROLE_USER")) {
            redirectURL += "/home";
        } else {
            redirectURL += "/home";
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
