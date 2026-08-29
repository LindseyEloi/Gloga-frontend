package com.centremedical.client.config;  // Adaptez selon votre package

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${backend.url:http://localhost:9091}")
    private String backendUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getBackendUrl() {
        return backendUrl;
    }
}