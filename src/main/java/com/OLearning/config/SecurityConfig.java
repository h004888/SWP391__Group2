package com.OLearning.config;

import com.OLearning.security.CustomAuthenticationSuccessHandler;
import com.OLearning.security.CustomOAuth2UserService;
import com.OLearning.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        //Use NoOpPasswordEncoder cho password chưa mã hóa
//      return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    //Config to use customUserDetailsService
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                                // Cho phép truy cập public resources
                                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**","/assets/**").permitAll()
                                .requestMatchers("/login", "/register", "/select-role", "/assign-role","/forgot-password","/reset-password","/otp-verification").permitAll()
                                .requestMatchers("/error", "/403").permitAll()

                                // Root path redirect
                                .requestMatchers("/home").permitAll()

                                // Chỉ admin mới được truy cập /admin/**
                                .requestMatchers("/admin/**").hasRole("ADMIN")

                                // Chỉ INSTRUCTOR mới được truy cập /instructordashboard/**
                                .requestMatchers("/instructordashboard/**").hasRole("INSTRUCTOR")

                                // User có thể truy cập /user/** và /home
//                        .requestMatchers("/home").hasAnyRole("USER","INSTRUCTOR")

                                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // Dùng chung trang login
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/login?error=true")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                        .expiredUrl("/login?expired=true")
                        .sessionRegistry(sessionRegistry())
                )
                .rememberMe(remember -> remember
                        .key("my-unique-key")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // 7days
                )
                .csrf(csrf -> csrf.disable()); // Disable CSRF

        return http.build();
    }


}
