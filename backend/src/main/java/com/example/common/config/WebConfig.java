package com.example.common.config;

import com.example.common.interceptor.CompanyInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Web配置类
 * 用于配置Spring MVC的相关功能
 * 
 * 主要配置：
 * 1. 注册JWT拦截器，实现接口的权限控制
 * 2. 注册公司拦截器，自动为管理员用户的请求添加公司ID属性
 * 3. 配置拦截器拦截的路径，包括：
 *    - 拦截所有请求 ("/**")
 *    - 排除首页 ("/")
 *    - 排除登录注册接口 ("/login", "/register")
 *    - 排除静态资源 ("/files/**")
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private JWTInterceptor jwtInterceptor;
    
    @Autowired
    private CompanyInterceptor companyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/register", "/files/**");
                
        // 注册公司拦截器
        registry.addInterceptor(companyInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/register", "/file/**","/swagger-ui/**","/v3/api-docs/**");
    }

}
