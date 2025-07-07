package com.OLearning.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UserNotificationInterceptor userNotificationInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL "/home/assets/**" tới thư mục resources/static/assets/
        registry.addResourceHandler("/home/assets/**", "/learning/course/assets/**", "/assets/**",
                "/learning/course/view/assets/**", "/learning/course/lesson/assets/**")
                .addResourceLocations("classpath:/static/assets/");
        // Thêm các resource handler phổ biến cho css, js, vendor, img
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/vendor/**")
                .addResourceLocations("classpath:/static/vendor/");
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userNotificationInterceptor)
                .addPathPatterns("/learning/**", "/user/**");
    }
}
