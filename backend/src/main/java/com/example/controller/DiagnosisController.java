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

/**
 * 诊断控制器
 * 处理与诊断相关的HTTP请求
 */
@RestController
@RequestMapping("/diagnoses")
public class DiagnosisController {

    @Autowired
    private DiagnosisService diagnosisService;

    /**
     * 添加诊断记录
     */
    @PostMapping("/add")
    public Result add(@RequestBody Diagnosis diagnosis) {
        try {
            // 设置诊断日期为当前时间（如果未提供）
            if (diagnosis.getDiagnosisDate() == null) {
                diagnosis.setDiagnosisDate(new Date());
            }
            
            diagnosisService.createDiagnosis(diagnosis);
            return Result.success();
        } catch (Exception e) {
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
            return Result.success(diagnosis);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    /**
     * 分页查询诊断记录
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String symptoms,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer doctorId
    ) {
        try {
            // 解析日期
            Date start = null;
            Date end = null;
            if (startDate != null && !startDate.isEmpty()) {
                start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
                // 设置结束日期为当天的23:59:59
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(end);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                end = calendar.getTime();
            }
            
            // 使用PageHelper进行分页
            PageHelper.startPage(pageNum, pageSize);
            
            // 使用条件查询
            List<Diagnosis> diagnoses = diagnosisService.getDiagnosesByCondition(
                    doctorId, patientName, symptoms, start, end);
            
            // 创建PageInfo对象
            PageInfo<Diagnosis> pageInfo = new PageInfo<>(diagnoses);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageInfo.getList());
            result.put("total", pageInfo.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
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
            return Result.error("500", e.getMessage());
        }
    }

    /**
     * 获取患者的所有诊断记录
     */
    @GetMapping("/patient/{patientId}")
    public Result getPatientDiagnoses(@PathVariable Integer patientId) {
        try {
            List<Diagnosis> diagnoses = diagnosisService.getDiagnosesByPatientId(patientId);
            return Result.success(diagnoses);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    /**
     * 更新诊断记录
     */
    @PutMapping("/update")
    public Result update(@RequestBody Diagnosis diagnosis) {
        try {
            diagnosisService.updateDiagnosis(diagnosis);
            return Result.success();
        } catch (Exception e) {
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
            return Result.error("500", e.getMessage());
        }
    }
} 