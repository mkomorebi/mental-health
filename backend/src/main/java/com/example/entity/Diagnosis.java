package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty(value = "diagnosisId", access = JsonProperty.Access.READ_WRITE)
    private Integer diagnosisId;
    
    /**
     * 医生id，外键关联doctor表
     */
    @JsonProperty(value = "doctor_id", access = JsonProperty.Access.READ_WRITE)
    private Integer doctorId;
    
    /**
     * 症状描述
     */
    private String symptoms;
    
    /**
     * 病人（员工）id，外键关联user表
     */
    @JsonProperty(value = "patient_id", access = JsonProperty.Access.READ_WRITE)
    private Integer patientId;
    
    /**
     * 诊断详情
     */
    @JsonProperty(value = "diagnosis_details", access = JsonProperty.Access.READ_WRITE)
    private String diagnosisDetails;
    
    /**
     * 健康打分
     */
    @JsonProperty(value = "health_score", access = JsonProperty.Access.READ_WRITE)
    private Integer healthScore;
    
    /**
     * 诊断日期
     */
    @JsonProperty(value = "diagnosis_date", access = JsonProperty.Access.READ_WRITE)
    private Date diagnosisDate;
    
    // 关联实体
    private Employee patient;
    private Doctor doctor;
}