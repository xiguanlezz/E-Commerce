package com.cj.cn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Order(value = -1)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("-----------CORS---------------");
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins("http://127.0.0.1", "http://localhost", "http://www.mmal.com")
                .allowedMethods("*")
                .maxAge(3600);
    }
}
