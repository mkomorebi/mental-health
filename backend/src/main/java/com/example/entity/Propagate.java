package com.example.entity;

import lombok.Data;

/**
 * 健康宣传类，表示健康宣传信息。
 */
@Data
public class Propagate {
    private Integer id; // 宣传ID
    private String title; // 宣传标题
    private String content; // 宣传内容
    private String time; // 发布时间
    private Integer doctorId; // 医生ID
    private String img; // 图片链接
    private Integer num; // 浏览量
    private Integer tagId; // 标签ID
    private String tag; // 标签名称（不映射到数据库，用于显示）
    private String doctorName; // 医生姓名（同时用于显示和搜索）
    private String doctorAvatar; // 医生头像
    private Integer companyId; // 公司ID
    
    // 排序相关字段，不映射到数据库
    private String orderBy;
    private String orderType;

    public static final String STATUS_PENDING = "PENDING";   // 待审核
    public static final String STATUS_APPROVED = "APPROVED"; // 已通过
    public static final String STATUS_REJECTED = "REJECTED"; // 已拒绝
    
    private String status; // 文章审核状态

    // 由于使用了@Data注解，移除所有手动定义的getter/setter方法
}
