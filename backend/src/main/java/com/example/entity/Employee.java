package com.example.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户类，表示用户的信息。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Employee extends Account {

    /** 主键ID */
    @ExcelProperty(value = "主键", index = 0)
    private Integer id; // 用户ID
    /** 账号 */
    @ExcelProperty(value = "用户名", index = 1)
    private String username; // 用户名
    @ExcelIgnore
    private String password; // 密码
    @ExcelProperty(value = "姓名", index = 2)
    private String name; // 姓名
    @ExcelProperty(value = "头像", index = 3)
    private String avatar; // 头像
    @ExcelProperty(value = "角色", index = 4)
    private String role; // 角色
    @ExcelProperty(value = "电话", index = 5)
    private String phone; // 电话
    @ExcelProperty(value = "邮箱", index = 6)
    private String email; // 邮箱
    @ExcelProperty(value = "部门ID", index = 7)
    private Integer departmentId; // 部门ID
    @ExcelProperty(value = "部门名称", index = 8)
    /** 部门名称 */
    private String departmentName; // 部门名称
    /**
     * 公司ID
     */
    @ExcelProperty(value = "公司ID", index = 9)
    private Integer companyId; // 公司ID
    @ExcelProperty(value = "公司名称", index = 10)
    private String companyName; // 公司名称

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
