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
                //http默认端口是80, https默认端口是443
                .allowedOrigins("http://127.0.0.1", "http://localhost", "http://127.0.0.1:8088","http://localhost:8088")
                .allowedMethods("*")
                .maxAge(3600);
    }
}
