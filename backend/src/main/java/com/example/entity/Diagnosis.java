package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 诊断结果实体类
 * 对应数据库中的diagnosis表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diagnosis {
    
    /**
     * 诊断表id，自增主键
     */
    private Integer diagnosisId;
    
    /**
     * 医生id，外键关联doctor表
     */
    private Integer doctorId;
    
    /**
     * 症状描述
     */
    private String symptoms;
    
    /**
     * 病人（员工）id，外键关联user表
     */
    private Integer patientId;
    
    /**
     * 诊断详情
     */
    private String diagnosisDetails;
    
    /**
     * 健康打分
     */
    private Integer healthScore;
    
    /**
     * 诊断日期
     */
    private Date diagnosisDate;
    
    // 可以添加关联实体
    private User patient;
    private Doctor doctor;
}