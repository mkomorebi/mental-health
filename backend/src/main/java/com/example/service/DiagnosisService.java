package com.example.service;

import com.example.entity.Diagnosis;
import com.example.mapper.DiagnosisMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 诊断业务层服务
 * 处理与诊断相关的业务逻辑
 */
@Service
public class DiagnosisService {

    @Autowired
    private DiagnosisMapper diagnosisMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DoctorService doctorService;

    /**
     * 创建诊断记录
     * @param diagnosis 诊断信息
     * @return 创建的诊断记录
     */
    @Transactional
    public Diagnosis createDiagnosis(Diagnosis diagnosis) {
        // 如果没有设置诊断日期，则设置为当前时间
        if (diagnosis.getDiagnosisDate() == null) {
            diagnosis.setDiagnosisDate(new Date());
        }
        
        // 验证医生和患者是否存在
        if (doctorService.selectById(diagnosis.getDoctorId()) == null) {
            throw new IllegalArgumentException("医生不存在");
        }
        
        if (userService.selectById(diagnosis.getPatientId()) == null) {
            throw new IllegalArgumentException("患者不存在");
        }
        
        // 插入诊断记录
        diagnosisMapper.insert(diagnosis);
        return diagnosis;
    }

    /**
     * 根据ID获取诊断记录
     * @param diagnosisId 诊断ID
     * @return 诊断记录
     */
    public Diagnosis getDiagnosisById(Integer diagnosisId) {
        return diagnosisMapper.selectById(diagnosisId);
    }

    /**
     * 获取医生的所有诊断记录
     * @param doctorId 医生ID
     * @return 诊断记录列表
     */
    public List<Diagnosis> getDiagnosesByDoctorId(Integer doctorId) {
        return diagnosisMapper.selectByDoctorId(doctorId);
    }

    /**
     * 获取患者的所有诊断记录
     * @param patientId 患者ID
     * @return 诊断记录列表
     */
    public List<Diagnosis> getDiagnosesByPatientId(Integer patientId) {
        return diagnosisMapper.selectByPatientId(patientId);
    }

    /**
     * 更新诊断记录
     * @param diagnosis 诊断信息
     * @return 更新后的诊断记录
     */
    @Transactional
    public Diagnosis updateDiagnosis(Diagnosis diagnosis) {
        // 检查诊断记录是否存在
        Diagnosis existingDiagnosis = diagnosisMapper.selectById(diagnosis.getDiagnosisId());
        if (existingDiagnosis == null) {
            throw new IllegalArgumentException("诊断记录不存在");
        }
        
        // 更新诊断记录
        diagnosisMapper.update(diagnosis);
        return diagnosisMapper.selectById(diagnosis.getDiagnosisId());
    }

    /**
     * 删除诊断记录
     * @param diagnosisId 诊断ID
     * @return 是否删除成功
     */
    @Transactional
    public boolean deleteDiagnosis(Integer diagnosisId) {
        // 检查诊断记录是否存在
        Diagnosis existingDiagnosis = diagnosisMapper.selectById(diagnosisId);
        if (existingDiagnosis == null) {
            return false;
        }
        
        // 删除诊断记录
        return diagnosisMapper.deleteById(diagnosisId) > 0;
    }

    /**
     * 获取所有诊断记录
     * @return 诊断记录列表
     */
    public List<Diagnosis> getAllDiagnoses() {
        return diagnosisMapper.selectAll();
    }
    
    /**
     * 根据健康分数范围查询诊断记录
     * @param minScore 最小分数
     * @param maxScore 最大分数
     * @return 诊断记录列表
     */
    public List<Diagnosis> getDiagnosesByHealthScoreRange(Integer minScore, Integer maxScore) {
        List<Diagnosis> allDiagnoses = diagnosisMapper.selectAll();
        return allDiagnoses.stream()
                .filter(d -> d.getHealthScore() != null && 
                        d.getHealthScore() >= minScore && 
                        d.getHealthScore() <= maxScore)
                .toList();
    }
    
    /**
     * 获取患者最新的诊断记录
     * @param patientId 患者ID
     * @return 最新的诊断记录
     */
    public Diagnosis getLatestDiagnosisByPatientId(Integer patientId) {
        List<Diagnosis> diagnoses = diagnosisMapper.selectByPatientId(patientId);
        if (diagnoses.isEmpty()) {
            return null;
        }
        
        // 按诊断日期降序排序，返回第一个（最新的）
        return diagnoses.stream()
                .sorted((d1, d2) -> d2.getDiagnosisDate().compareTo(d1.getDiagnosisDate()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 统计医生的诊断数量
     * @param doctorId 医生ID
     * @return 诊断数量
     */
    public int countDiagnosesByDoctorId(Integer doctorId) {
        List<Diagnosis> diagnoses = diagnosisMapper.selectByDoctorId(doctorId);
        return diagnoses.size();
    }
    
    /**
     * 统计患者的诊断数量
     * @param patientId 患者ID
     * @return 诊断数量
     */
    public int countDiagnosesByPatientId(Integer patientId) {
        List<Diagnosis> diagnoses = diagnosisMapper.selectByPatientId(patientId);
        return diagnoses.size();
    }

    /**
     * 根据条件查询诊断记录
     * @param doctorId 医生ID
     * @param patientName 患者姓名
     * @param symptoms 症状关键词
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 诊断记录列表
     */
    public List<Diagnosis> getDiagnosesByCondition(
            Integer doctorId,
            String patientName,
            String symptoms,
            Date startDate,
            Date endDate
    ) {
        List<Diagnosis> diagnoses = diagnosisMapper.selectByCondition(doctorId, patientName, symptoms, startDate, endDate);
        
        // 加载关联的医生和患者信息
        for (Diagnosis diagnosis : diagnoses) {
            if (diagnosis.getDoctorId() != null) {
                diagnosis.setDoctor(doctorService.selectById(diagnosis.getDoctorId()));
            }
            if (diagnosis.getPatientId() != null) {
                diagnosis.setPatient(userService.selectById(diagnosis.getPatientId()));
            }
        }
        
        return diagnoses;
    }
} 