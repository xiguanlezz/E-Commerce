package com.cj.cn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Order(value = -1)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("-----------CORS---------------");
        registry.addMapping("/**")
                .allowCredentials(true)
                //http默认端口是80, https默认端口是443
                .allowedOrigins("http://127.0.0.1", "http://localhost")
                .allowedOrigins("http://127.0.0.1:8088", "http://localhost:8088")
                .allowedOrigins("http://127.0.0.1:8086", "http://localhost:8086")
                .allowedMethods("*")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
