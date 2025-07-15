package com.OLearning.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary configkey() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dh0qn5xbk");
        config.put("api_key", "367863941559932");
        config.put("api_secret", "QVOQihDhOZc7KfdzVm2aPgC4nYs");
        return new Cloudinary(config);
    }
}
