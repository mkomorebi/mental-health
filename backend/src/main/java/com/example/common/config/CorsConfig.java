package com.example.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // 不能同时使用 "*" 和 setAllowCredentials(true)
        // 替换为具体的前端地址
        corsConfiguration.addAllowedOrigin("http://localhost:5173"); // 开发环境前端地址
        // 增加 Mozilla PDF Viewer 的域名
        corsConfiguration.addAllowedOrigin("https://mozilla.github.io");
        // 如果需要允许所有来源访问，请使用以下配置（不要与 setAllowCredentials(true) 同时使用）
        // corsConfiguration.addAllowedOriginPattern("*");
        
        corsConfiguration.addAllowedHeader("*"); // 设置访问源请求头
        corsConfiguration.addAllowedMethod("*"); // 设置访问源请求方法
        corsConfiguration.setAllowCredentials(true); // 允许携带cookie等凭证
        
        // 明确暴露这些响应头，使前端可以访问
        corsConfiguration.addExposedHeader("Authorization");
        corsConfiguration.addExposedHeader("token");
        corsConfiguration.addExposedHeader("Content-Disposition"); // 添加此响应头，帮助文件下载
        
        // 预检请求的有效期，单位为秒
        corsConfiguration.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", corsConfiguration); // 对接口配置跨域设置
        return new CorsFilter(source);
    }
}