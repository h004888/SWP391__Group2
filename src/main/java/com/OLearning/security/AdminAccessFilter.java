package com.OLearning.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AdminAccessFilter implements Filter {
    @Override
    public void doFilter(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);

        String uri = request.getRequestURI();

        // Bỏ qua filter cho webhook của SePay
        if (uri.equals("/api/payment/sepay/webhook")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // Nếu vào /home 
            if (uri.startsWith("/home")) {
                if (session != null) {
                    session.setAttribute("admin_visited_home", true);
                }
            }
            // Nếu vào /admin mà đã từng vào /home thì chặn
            if (uri.startsWith("/admin")) {
                if (session != null && Boolean.TRUE.equals(session.getAttribute("admin_visited_home"))) {
                    response.sendRedirect("/403");
                    return;
                }
                // Đánh dấu admin đã vào /admin
                if (session != null) {
                    session.setAttribute("admin_visited_admin", true);
                }
            }
            // Nếu vào /instructordashboard mà đã từng vào /admin thì chặn
            if (uri.startsWith("/instructordashboard")) {
                if (session != null && Boolean.TRUE.equals(session.getAttribute("admin_visited_admin"))) {
                    response.sendRedirect("/403");
                    return;
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
} 