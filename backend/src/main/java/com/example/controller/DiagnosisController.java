package com.example.controller;

import com.example.common.Result;
import com.example.entity.Diagnosis;
import com.example.service.DiagnosisService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 诊断控制器
 * 处理与诊断相关的HTTP请求
 */
@RestController
@RequestMapping("/diagnoses")
public class DiagnosisController {
    
    private static final Logger logger = LoggerFactory.getLogger(DiagnosisController.class);

    @Autowired
    private DiagnosisService diagnosisService;

    /**
     * 添加诊断记录
     */
    @PostMapping("/add")
    public Result add(@RequestBody Diagnosis diagnosis) {
        try {
            // 打印接收到的数据，用于调试
            logger.info("接收到的诊断数据: {}", diagnosis);
            
            // 设置诊断日期为当前时间（如果未提供）
            if (diagnosis.getDiagnosisDate() == null) {
                diagnosis.setDiagnosisDate(new Date());
            }
            
            diagnosisService.createDiagnosis(diagnosis);
            return Result.success();
        } catch (Exception e) {
            logger.error("添加诊断记录失败", e);
            return Result.error("500", e.getMessage());
        }
    }

    /**
     * 获取诊断记录详情
     */
    @GetMapping("/detail/{id}")
    public Result getDetail(@PathVariable Integer id) {
        try {
            Diagnosis diagnosis = diagnosisService.getDiagnosisById(id);
            if (diagnosis == null) {
                return Result.error("404", "诊断记录不存在");
            }
            
            logger.info("获取到诊断记录: {}", diagnosis);
            return Result.success(diagnosis);
        } catch (Exception e) {
            logger.error("获取诊断记录详情失败", e);
            return Result.error("500", e.getMessage());
        }
    }

    /**
     * 分页查询诊断记录
     */
    @GetMapping("/page")
    public Result page(
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) Integer doctorId,
        @RequestParam(required = false) String patientName,
        @RequestParam(required = false) String symptoms,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate
    ) {
        try {
            logger.info("查询参数: doctorId={}, patientName={}, symptoms={}, startDate={}, endDate={}", 
                      doctorId, patientName, symptoms, startDate, endDate);
            
            // 验证医生ID
            if (doctorId == null || doctorId <= 0) {
                return Result.error("400", "无效的医生ID");
            }
            
            // 转换日期格式
            Date start = null;
            Date end = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            if (startDate != null && !startDate.isEmpty()) {
                try {
                    start = sdf.parse(startDate);
                } catch (Exception e) {
                    logger.warn("开始日期格式错误: {}", startDate);
                }
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                try {
                    // 设置为当天的结束时间
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(sdf.parse(endDate));
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    end = calendar.getTime();
                } catch (Exception e) {
                    logger.warn("结束日期格式错误: {}", endDate);
                }
            }
            
            // 调用服务层方法进行分页查询
            PageHelper.startPage(pageNum, pageSize);
            List<Diagnosis> diagnoses = diagnosisService.getDiagnosesByCondition(
                doctorId, patientName, symptoms, start, end);
            
            PageInfo<Diagnosis> pageInfo = new PageInfo<>(diagnoses);
            
            logger.info("查询结果数量: {}", pageInfo.getTotal());
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", pageInfo.getTotal());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("list", pageInfo.getList());
            
            return Result.success(result);
        } catch (Exception e) {
            logger.error("分页查询诊断记录失败", e);
            return Result.error("500", e.getMessage());
        }
    }

    /**
     * 获取医生的所有诊断记录
     */
    @GetMapping("/doctor/{doctorId}")
    public Result getDoctorDiagnoses(@PathVariable Integer doctorId) {
        try {
            List<Diagnosis> diagnoses = diagnosisService.getDiagnosesByDoctorId(doctorId);
            return Result.success(diagnoses);
        } catch (Exception e) {
            logger.error("获取医生诊断记录失败", e);
            return Result.error("500", e.getMessage());
        }
    }

    /**
     * 获取患者的诊断记录
     */
    @GetMapping("/patient/{patientId}")
    public Result getPatientDiagnoses(@PathVariable Integer patientId) {
        try {
            List<Diagnosis> diagnoses = diagnosisService.getDiagnosesByPatientId(patientId);
            return Result.success(diagnoses);
        } catch (Exception e) {
            return Result.error("获取患者诊断记录失败: " + e.getMessage());
        }
    }

    /**
     * 更新诊断记录
     */
    @PutMapping("/update")
    public Result update(@RequestBody Diagnosis diagnosis) {
        try {
            logger.info("更新诊断记录: {}", diagnosis);
            diagnosisService.updateDiagnosis(diagnosis);
            return Result.success();
        } catch (Exception e) {
            logger.error("更新诊断记录失败", e);
            return Result.error("500", e.getMessage());
        }
    }

    /**
     * 删除诊断记录
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            boolean success = diagnosisService.deleteDiagnosis(id);
            if (success) {
                return Result.success();
            } else {
                return Result.error("404", "诊断记录不存在");
            }
        } catch (Exception e) {
            logger.error("删除诊断记录失败", e);
            return Result.error("500", e.getMessage());
        }
    }
} 