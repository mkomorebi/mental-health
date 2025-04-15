package com.example.common.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 用于配置Spring MVC的相关功能
 * 
 * 主要配置：
 * 1. 注册JWT拦截器，实现接口的权限控制
 * 2. 配置拦截器拦截的路径，包括：
 *    - 拦截所有请求 ("/**")
 *    - 排除首页 ("/")
 *    - 排除登录注册接口 ("/login", "/register")
 *    - 排除静态资源 ("/files/**")
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private JWTInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器，拦截所有请求
        // 排除登录、注册、刷新token和静态资源等不需要验证token的路径
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", 
                                    "/register",
                                    "/refreshToken",
                                    "/files/upload", 
                                    "/files/download/**")
                .excludePathPatterns("/error"); // 排除错误页面
    }

}
