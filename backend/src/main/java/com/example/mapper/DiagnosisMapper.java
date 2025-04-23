package com.example.mapper;

import com.example.entity.Diagnosis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Date;

/**
 * 诊断结果数据库操作接口
 * 对应诊断表(diagnoses)的数据库操作
 */
@Mapper
public interface DiagnosisMapper {
    
    /**
     * 添加诊断记录
     * @param diagnosis 诊断信息
     * @return 影响的行数
     */
    int insert(Diagnosis diagnosis);
    
    /**
     * 根据ID查询诊断记录
     * @param diagnosisId 诊断ID
     * @return 诊断记录
     */
    Diagnosis selectById(Integer diagnosisId);
    
    /**
     * 根据医生ID查询诊断记录列表
     * @param doctorId 医生ID
     * @return 诊断记录列表
     */
    List<Diagnosis> selectByDoctorId(Integer doctorId);
    
    /**
     * 根据患者ID查询诊断记录列表
     * @param patientId 患者ID
     * @return 诊断记录列表
     */
    List<Diagnosis> selectByPatientId(Integer patientId);
    
    /**
     * 更新诊断记录
     * @param diagnosis 诊断信息
     * @return 影响的行数
     */
    int update(Diagnosis diagnosis);
    
    /**
     * 删除诊断记录
     * @param diagnosisId 诊断ID
     * @return 影响的行数
     */
    int deleteById(Integer diagnosisId);
    
    /**
     * 查询所有诊断记录
     * @return 诊断记录列表
     */
    List<Diagnosis> selectAll();
    
    /**
     * 根据条件和日期范围查询诊断记录
     * 
     * @param diagnosis 查询条件对象，包含患者ID、医生ID等
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param patientName 患者姓名（可选）
     * @return 诊断记录列表
     */
    List<Diagnosis> selectByCondition(
        @Param("diagnosis") Diagnosis diagnosis, 
        @Param("startDate") Date startDate, 
        @Param("endDate") Date endDate,
        @Param("patientName") String patientName
    );
} 