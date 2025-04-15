package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.service.AdminService;
import com.example.service.DoctorService;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WebController {

    @Resource
    private AdminService adminService;
    @Resource
    private DoctorService doctorService;
    @Resource
    private UserService userService;

    /**
     * 默认请求接口
     */
    @GetMapping("/")
    public Result hello () {
        return Result.success();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account loginAccount = null;
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            loginAccount = adminService.login(account);
        }
        if (RoleEnum.DOCTOR.name().equals(account.getRole())) {
            loginAccount = doctorService.login(account);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            loginAccount = userService.login(account);
        }
        return Result.success(loginAccount);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        // 通过前端传过来的数据里面的角色来判断往哪个数据库里新增一条数据
        if (RoleEnum.DOCTOR.name().equals(account.getRole())) {
            doctorService.register(account);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            // 确保不调用Account对象上不存在的put方法
            userService.register(account);
        }
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        }
        if (RoleEnum.DOCTOR.name().equals(account.getRole())) {
            doctorService.updatePassword(account);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }
        return Result.success();
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refreshToken")
    public Result refreshToken(@RequestBody Map<String, String> params) {
        String refreshToken = params.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return Result.error("401", "刷新令牌无效");
        }
        
        try {
            // 解析refreshToken
            String audience = JWT.decode(refreshToken).getAudience().get(0);
            String userId = audience.split("-")[0];
            String role = audience.split("-")[1];
            
            Account account = null;
            if (RoleEnum.ADMIN.name().equals(role)) {
                account = adminService.selectById(Integer.valueOf(userId));
            }
            if (RoleEnum.DOCTOR.name().equals(role)) {
                account = doctorService.selectById(Integer.valueOf(userId));
            }
            if (RoleEnum.USER.name().equals(role)) {
                account = userService.selectById(Integer.valueOf(userId));
            }
            
            if (account == null) {
                return Result.error("401", "用户不存在");
            }
            
            // 验证refreshToken
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword() + "_refresh")).build();
            jwtVerifier.verify(refreshToken);
            
            // 生成新的token
            String newToken = JWT.create()
                    .withAudience(userId + "-" + role)
                    .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                    .sign(Algorithm.HMAC256(account.getPassword()));
            
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", newToken);
            
            return Result.success(tokenMap);
        } catch (Exception e) {
            return Result.error("401", "刷新令牌验证失败");
        }
    }

}
