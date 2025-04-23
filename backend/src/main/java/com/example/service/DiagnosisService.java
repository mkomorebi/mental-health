package com.example.service;

import com.example.entity.Diagnosis;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;

/**
 * 诊断业务层服务接口
 * 定义与诊断相关的业务逻辑操作
 */
public interface DiagnosisService {

    /**
     * 创建诊断记录
     * @param diagnosis 诊断信息
     * @return 创建的诊断记录
     */
    Diagnosis createDiagnosis(Diagnosis diagnosis);

    /**
     * 根据ID获取诊断记录
     * @param diagnosisId 诊断ID
     * @return 诊断记录
     */
    Diagnosis getDiagnosisById(Integer diagnosisId);

    /**
     * 获取医生的所有诊断记录
     * @param doctorId 医生ID
     * @return 诊断记录列表
     */
    List<Diagnosis> getDiagnosesByDoctorId(Integer doctorId);

    /**
     * 获取患者的所有诊断记录
     * @param patientId 患者ID
     * @return 诊断记录列表
     */
    List<Diagnosis> getDiagnosesByPatientId(Integer patientId);

    /**
     * 更新诊断记录
     * @param diagnosis 诊断信息
     * @return 更新后的诊断记录
     */
    Diagnosis updateDiagnosis(Diagnosis diagnosis);

    /**
     * 删除诊断记录
     * @param diagnosisId 诊断ID
     * @return 是否删除成功
     */
    boolean deleteDiagnosis(Integer diagnosisId);

    /**
     * 获取所有诊断记录
     * @return 诊断记录列表
     */
    List<Diagnosis> getAllDiagnoses();
    
    /**
     * 根据健康分数范围查询诊断记录
     * @param minScore 最小分数
     * @param maxScore 最大分数
     * @return 诊断记录列表
     */
    List<Diagnosis> getDiagnosesByHealthScoreRange(Integer minScore, Integer maxScore);
    
    /**
     * 获取患者最新的诊断记录
     * @param patientId 患者ID
     * @return 最新的诊断记录
     */
    Diagnosis getLatestDiagnosisByPatientId(Integer patientId);
    
    /**
     * 统计医生的诊断数量
     * @param doctorId 医生ID
     * @return 诊断数量
     */
    int countDiagnosesByDoctorId(Integer doctorId);
    
    /**
     * 统计患者的诊断数量
     * @param patientId 患者ID
     * @return 诊断数量
     */
    int countDiagnosesByPatientId(Integer patientId);

    /**
     * 根据条件查询诊断记录
     * @param doctorId 医生ID
     * @param patientName 患者姓名
     * @param symptoms 症状关键词
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 诊断记录列表
     */
    List<Diagnosis> getDiagnosesByCondition(
            Integer doctorId,
            String patientName,
            String symptoms,
            Date startDate,
            Date endDate
    );

    /**
     * 根据患者ID和日期范围获取诊断记录
     * 
     * @param patientId 患者ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制返回记录数量，如果为null则不限制
     * @return 诊断记录列表
     */
    List<Diagnosis> getPatientDiagnosesInDateRange(Integer patientId, LocalDate startDate, LocalDate endDate, Integer limit);
} 