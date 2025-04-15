package com.example.common.config;

import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.DoctorService;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

/**
 * JWT拦截器
 * 用于对需要登录权限的接口进行token验证
 * 
 * 验证流程：
 * 1. 从请求头或请求参数中获取token
 * 2. 解析token中的用户ID和角色信息
 * 3. 根据角色从对应数据库表查询用户信息
 * 4. 使用用户密码作为密钥验证token的合法性
 * 
 * 验证失败的情况：
 * - token不存在：抛出TOKEN_INVALID_ERROR
 * - token解析失败：抛出TOKEN_CHECK_ERROR
 * - 用户不存在：抛出TOKEN_CHECK_ERROR
 * - token签名验证失败：抛出TOKEN_CHECK_ERROR
 */
@Component
public class JWTInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JWTInterceptor.class);

    @Resource
    private AdminService adminService;
    @Resource
    private DoctorService doctorService;
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 对预检请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 打印所有请求头，用于调试
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.info("{}={}", headerName, request.getHeader(headerName));
        }
        
        // 1. 从http请求标头里面获取token，如果没有则从请求参数中获取
        String token = request.getHeader(Constants.TOKEN);
        
        if (ObjectUtil.isNull(token)) {
            // 尝试从Authorization头获取
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7); // 去掉"Bearer "前缀
            }
        }
        
        if (ObjectUtil.isNull(token)) {
            token = request.getParameter(Constants.TOKEN);
        }
        
        // 2. 如果token为空，抛出token无效异常
        if (ObjectUtil.isNull(token)) {
            throw new CustomException(ResultCodeEnum.TOKEN_INVALID_ERROR);
        }
        
        Account account = null;
        try {
            // 3. 解析token中的用户信息
            String audience = JWT.decode(token).getAudience().get(0);
            
            String userId = audience.split("-")[0];    // 获取用户ID
            String role = audience.split("-")[1];      // 获取用户角色
            
            // 4. 根据用户角色查询对应数据库表中的用户信息
            if (RoleEnum.ADMIN.name().equals(role)) {
                account = adminService.selectById(Integer.valueOf(userId));
            }
            if (RoleEnum.DOCTOR.name().equals(role)) {
                account = doctorService.selectById(Integer.valueOf(userId));
            }
            if (RoleEnum.USER.name().equals(role)) {
                account = userService.selectById(Integer.valueOf(userId));
            }

        } catch (Exception e) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
        
        // 5. 如果用户不存在，抛出token验证错误
        if (ObjectUtil.isNull(account)) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
        
        try {
            // 6. 使用用户密码作为密钥验证token的合法性
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
        return true;
    }

}
