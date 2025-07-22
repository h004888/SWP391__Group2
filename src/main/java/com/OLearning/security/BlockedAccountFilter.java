package com.OLearning.security;

import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BlockedAccountFilter implements Filter {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            User user = userRepository.findByEmail(auth.getName()).orElse(null);
            if (user != null && !Boolean.TRUE.equals(user.getStatus())) {
                String uri = request.getRequestURI();
                if (!uri.equals("/account-blocked") && !uri.equals("/logout") && !uri.startsWith("/account-blocked/appeal")) {
                    response.sendRedirect("/account-blocked");
                    return;
                }
            }
        }
        chain.doFilter(req, res);
    }
} 