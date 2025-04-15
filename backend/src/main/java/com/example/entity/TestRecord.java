package com.example.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 测试记录类，表示用户的测试记录信息。
 */
@Setter
@Getter
public class TestRecord {
    private Integer id; // 测试记录ID
    private Integer testPaperId; // 试卷ID
    private Integer userId; // 用户ID
    private Integer doctorId; // 医生ID
    private Integer score; // 得分
    private String result; // 测试结果
    private String time; // 测试时间

    private String testPaperName; // 试卷名称
    private String userName; // 用户姓名
    private String doctorName; // 医生姓名
    private String typeName; // 试卷类型名称
    private String departmentName; // 部门名称
    
    // 查询参数
    private Integer departmentId; // 部门ID
    private Integer minScore; // 最低分数
    private Integer maxScore; // 最高分数

    private String department;
}
