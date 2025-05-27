//package com.OLearning.config;
//
//import com.OLearning.service.adminDashBoard.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationSuccessHandler authenticationSuccessHandler() {
//        return new CustomAuthenticationSuccessHandler();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authz -> authz
//                        // Cho phép truy cập public resources
//                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
//                        .requestMatchers("/login", "/register").permitAll()
//                        .requestMatchers("/error", "/403").permitAll()
//
//                        // Root path redirect
//                        .requestMatchers("/").permitAll()
//
//                        // Chỉ admin mới được truy cập /admin/**
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//
//                        // User có thể truy cập /user/** và /home
//                        .requestMatchers("/home", "/user/**").hasAnyRole("USER", "ADMIN")
//
//                        // Các request khác cần xác thực
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .loginProcessingUrl("/perform_login")
//                        .usernameParameter("username")
//                        .passwordParameter("password")
//                        .successHandler(authenticationSuccessHandler()) // dùng bean thay vì new
//                        .failureUrl("/login?error=true")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                        .logoutSuccessUrl("/login?logout=true")
//                        .deleteCookies("JSESSIONID")
//                        .invalidateHttpSession(true)
//                        .permitAll()
//                )
//                .exceptionHandling(ex -> ex
//                        .accessDeniedPage("/403")
//                )
//                .sessionManagement(session -> session
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(false)
//                )
//                .csrf(csrf -> csrf.disable()); // Disable CSRF cho đơn giản
//
//        return http.build();
//    }
//
//
//
//}
