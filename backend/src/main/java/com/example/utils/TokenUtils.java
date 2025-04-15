package com.example.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.common.Constants;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.service.AdminService;
import com.example.service.DoctorService;
import com.example.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

/**
 * TokenUtils 工具类，用于生成和解析 JWT token。
 * 提供获取当前登录用户的功能。
 */
@Component
public class TokenUtils {
    private static final Logger log = LoggerFactory.getLogger(TokenUtils.class);

    @Resource
    private AdminService adminService;
    @Resource
    private DoctorService doctorService;
    @Resource
    private UserService userService;

    private static AdminService staticAdminService;
    private static DoctorService staticDoctorService;
    private static UserService staticUserService;

    @PostConstruct
    public void init() {
        staticAdminService = adminService;
        staticDoctorService = doctorService;
        staticUserService = userService;
    }

    /**
     * 生成 JWT token。
     *
     * @param data 用户信息（ID和角色）
     * @param sign 签名密钥
     * @return 生成的 JWT token
     */
    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data)
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1)) // 设置过期时间为1天后
                .sign(Algorithm.HMAC256(sign));
    }

    /**
     * 获取当前请求中的token
     * @return token字符串
     */
    public static String getToken() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return request.getHeader(Constants.TOKEN);
        } catch (Exception e) {
            log.error("获取token失败", e);
            return null;
        }
    }

    /**
     * 获取当前登录的用户。
     *
     * @return 当前用户的 Account 实体
     */
    public static Account getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader(Constants.TOKEN);
            String audience = JWT.decode(token).getAudience().get(0);
            String[] userRole = audience.split("-");
            Integer userId = Integer.valueOf(userRole[0]);
            String role = userRole[1];
            if (RoleEnum.ADMIN.name().equals(role)) {
                return staticAdminService.selectById(userId);
            }
            if (RoleEnum.DOCTOR.name().equals(role)) {
                return staticDoctorService.selectById(userId);
            }
            if (RoleEnum.USER.name().equals(role)) {
                return staticUserService.selectById(userId);
            }
        } catch (Exception e) {
            log.error("获取当前登录用户出错", e);
        }
        return null;
    }

    /**
     * 获取当前登录的管理员ID
     * @return 管理员ID，如果不是管理员则返回null
     */
    public static Integer getCurrentAdmin() {
        String token = getToken();
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        try {
            // 使用正确的JWT验证方法
            String audience = JWT.decode(token).getAudience().get(0);
            if (audience != null && audience.contains("-")) {
                String[] parts = audience.split("-");
                if (parts.length == 2 && RoleEnum.ADMIN.name().equals(parts[1])) {
                    return Integer.parseInt(parts[0]);
                }
            }
        } catch (Exception e) {
            log.error("解析token失败", e);
        }
        return null;
    }
}
