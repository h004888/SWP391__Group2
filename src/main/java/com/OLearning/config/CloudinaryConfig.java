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
        config.put("cloud_name", "dmwo4yrsq");
        config.put("api_key", "525245441342925");
        config.put("api_secret", "ZOSQV39ZXuEOKBxtkBWzzxlSkrE");
        return new Cloudinary(config);
    }
}
