package com.example.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.common.Constants;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.service.AdminService;
import com.example.service.DoctorService;
import com.example.service.EmployeeService;
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
    private EmployeeService employeeService;

    private static AdminService staticAdminService;
    private static DoctorService staticDoctorService;
    private static EmployeeService staticUserService;

    @PostConstruct
    public void init() {
        staticAdminService = adminService;
        staticDoctorService = doctorService;
        staticUserService = employeeService;
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
            if (StrUtil.isNotBlank(token)) {
                String userId = JWT.decode(token).getAudience().get(0);
                String[] userRole = userId.split("-");
                Integer userIdInt = Integer.valueOf(userRole[0]);
                String role = userRole[1];
                
                // 创建一个基本的Account对象，只包含ID和角色信息
                // 而不是调用Service层方法
                Account account = new Account();
                account.setId(userIdInt);
                account.setRole(role);
                return account;
                
                // 注释掉原来的代码，避免循环依赖
                /*
                if (RoleEnum.ADMIN.name().equals(role)) {
                    return staticAdminService.selectById(userIdInt);
                }
                if (RoleEnum.DOCTOR.name().equals(role)) {
                    return staticDoctorService.selectById(userIdInt);
                }
                if (RoleEnum.USER.name().equals(role)) {
                    return staticUserService.selectById(userIdInt);
                }
                */
            }
        } catch (Exception e) {
            log.error("获取当前登录用户出错", e);
        }
        return null;
    }

    /**
     * 获取当前登录的管理员
     *
     * @return Admin对象
     */
    public static Admin getCurrentAdmin() {
        try {
            Account currentUser = getCurrentUser();
            if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
                // 不要直接转换，而是通过ID查询完整的Admin对象
                return staticAdminService.selectById(currentUser.getId());
            }
            return null;
        } catch (Exception e) {
            System.err.println("获取当前管理员失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 从token中获取用户信息
     * @param token JWT token
     * @return 用户信息数组，第一个元素是用户ID和角色，第二个元素是密码
     */
    public static String[] getUserInfo(String token) {
        try {
            if (StrUtil.isNotBlank(token)) {
                String userId = JWT.decode(token).getAudience().get(0);
                return new String[]{userId};
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 从token中获取用户角色
     * @param token JWT token
     * @return 用户角色
     */
    public static String getUserRole(String token) {
        try {
            String[] userInfo = getUserInfo(token);
            if (userInfo != null && userInfo.length >= 1) {
                String userIdAndRole = userInfo[0];
                if (userIdAndRole != null && userIdAndRole.contains("-")) {
                    String[] parts = userIdAndRole.split("-");
                    if (parts.length >= 2) {
                        return parts[1];
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前用户角色
     * @return 用户角色
     */
    public static String getCurrentRole() {
        String token = getToken();
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        try {
            String audience = JWT.decode(token).getAudience().get(0);
            String[] parts = audience.split("-");
            if (parts.length >= 2) {
                return parts[1]; // 返回角色部分
            }
        } catch (Exception e) {
            return null;
        }
        
        return null;
    }

    /**
     * 解析JWT令牌
     * @param token JWT令牌
     * @return 解析后的JWT对象
     */
    public static com.auth0.jwt.interfaces.DecodedJWT decodeToken(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return JWT.decode(token);
    }

    /**
     * 获取当前请求对象
     * @return 当前HTTP请求对象
     */
    public static HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
            return null;
        } catch (Exception e) {
            log.error("获取当前请求对象失败", e);
            return null;
        }
    }
}
