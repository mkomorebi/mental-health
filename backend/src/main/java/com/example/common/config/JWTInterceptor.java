package com.example.common.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Admin;
import com.example.entity.Doctor;
import com.example.entity.Employee;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.DoctorService;
import com.example.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

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

    @Autowired
    private ApplicationContext applicationContext;
    
    private AdminService adminService;
    private DoctorService doctorService;
    private EmployeeService employeeService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 从 http 请求头中取出 token
        String token = request.getHeader("token");
        if (token == null || token.equals("")) {
            throw new CustomException(ResultCodeEnum.TOKEN_ERROR);
        }

        // 获取 token 中的 userId
        String userId;
        String role;
        try {
            String userIdAndRole = JWT.decode(token).getAudience().get(0);
            String[] parts = userIdAndRole.split("-");
            userId = parts[0];  // 获取用户ID
            role = parts.length > 1 ? parts[1] : "";  // 获取角色
        } catch (JWTDecodeException j) {
            throw new CustomException(ResultCodeEnum.TOKEN_ERROR);
        }

        // 验证 token
        try {
            // 根据角色选择不同的验证方式
            if ("ADMIN".equals(role)) {
                // 延迟加载 AdminService
                if (adminService == null) {
                    adminService = applicationContext.getBean(AdminService.class);
                }
                Admin admin = adminService.selectById(Integer.valueOf(userId));
                if (admin == null) {
                    throw new CustomException(ResultCodeEnum.TOKEN_ERROR);
                }
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(admin.getPassword())).build();
                jwtVerifier.verify(token);
            } else if ("DOCTOR".equals(role)) {
                // 延迟加载 DoctorService
                if (doctorService == null) {
                    doctorService = applicationContext.getBean(DoctorService.class);
                }
                Doctor doctor = doctorService.selectById(Integer.valueOf(userId));
                if (doctor == null) {
                    throw new CustomException(ResultCodeEnum.TOKEN_ERROR);
                }
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(doctor.getPassword())).build();
                jwtVerifier.verify(token);
            } else if ("USER".equals(role)) {
                // 延迟加载 EmployeeService
                if (employeeService == null) {
                    employeeService = applicationContext.getBean(EmployeeService.class);
                }
                Employee employee = employeeService.selectById(Integer.valueOf(userId));
                if (employee == null) {
                    throw new CustomException(ResultCodeEnum.TOKEN_ERROR);
                }
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(employee.getPassword())).build();
                jwtVerifier.verify(token);
            } else {
                throw new CustomException(ResultCodeEnum.TOKEN_ERROR);
            }
        } catch (JWTVerificationException e) {
            throw new CustomException(ResultCodeEnum.TOKEN_ERROR);
        }

        return true;
    }
}
