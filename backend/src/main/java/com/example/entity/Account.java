package com.example.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.Data;

/**
 * 账户类，表示用户的账户信息。
 */
@Data
public class Account {

    @ExcelIgnore
    private Integer id; // 主键ID
    @ExcelIgnore
    private String username; // 用户名
    @ExcelIgnore
    private String name; // 姓名
    @ExcelIgnore
    private String password; // 密码
    @ExcelIgnore
    private String role; // 角色
    @ExcelIgnore
    private String newPassword; // 新密码
    @ExcelIgnore
    private String token; // 令牌
    @ExcelIgnore
    private String avatar;
    @ExcelIgnore
    private String refreshToken;
    @ExcelIgnore
    private Integer companyId; // 添加公司ID字段

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}
