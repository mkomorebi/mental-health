package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Doctor;
import com.example.entity.Account;
import com.example.entity.Employee;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.DoctorService;
import com.example.service.EmployeeService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 医生控制器，处理与医生相关的请求。
 */
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Resource
    private DoctorService doctorService;
    
    @Resource
    private AdminService adminService;

    @Resource
    private EmployeeService employeeService;

    /**
     * 新增医生
     */
    @PostMapping("/add")
    public Result add(@RequestBody Doctor doctor, HttpServletRequest request) {
        // 获取当前管理员的公司ID
        Integer adminCompanyId = (Integer) request.getAttribute("companyId");
        if (adminCompanyId != null) {
            // 如果是管理员，强制设置为管理员所属公司
            doctor.setCompanyId(adminCompanyId);
        } else if (TokenUtils.getCurrentRole().equals("ADMIN")) {
            // 如果是管理员但没有通过拦截器设置公司ID，尝试获取
            adminCompanyId = adminService.getAdminCompanyId();
            if (adminCompanyId != null) {
                doctor.setCompanyId(adminCompanyId);
            }
        }
        
        doctorService.add(doctor);
        return Result.success();
    }

    /**
     * 修改医生信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Doctor doctor, HttpServletRequest request) {
        // 获取当前管理员的公司ID
        Integer adminCompanyId = (Integer) request.getAttribute("companyId");
        
        // 如果是管理员，验证医生是否属于该管理员的公司
        if (adminCompanyId != null && TokenUtils.getCurrentRole().equals("ADMIN")) {
            // 检查医生是否属于该公司
            if (!doctorService.checkDoctorCompany(doctor.getId(), adminCompanyId)) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权修改其他公司的医生信息");
            }
            // 确保不能修改医生的公司ID
            doctor.setCompanyId(adminCompanyId);
        }
        
        doctorService.updateById(doctor);
        return Result.success();
    }

    /**
     * 提交医师资格证
     */
    @PutMapping("/submit")
    public Result submit(@RequestBody Doctor doctor) {
        // 医生自己提交认证信息，不需要公司ID验证
        doctorService.submit(doctor);
        return Result.success();
    }

    /**
     * 删除单个医生
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id, HttpServletRequest request) {
        // 获取当前管理员的公司ID
        Integer adminCompanyId = (Integer) request.getAttribute("companyId");
        
        // 如果是管理员，验证医生是否属于该管理员的公司
        if (adminCompanyId != null && TokenUtils.getCurrentRole().equals("ADMIN")) {
            // 检查医生是否属于该公司
            if (!doctorService.checkDoctorCompany(id, adminCompanyId)) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权删除其他公司的医生");
            }
        }
        
        doctorService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除医生
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids, HttpServletRequest request) {
        // 获取当前管理员的公司ID
        Integer adminCompanyId = (Integer) request.getAttribute("companyId");
        
        // 如果是管理员，验证所有医生是否属于该管理员的公司
        if (adminCompanyId != null && TokenUtils.getCurrentRole().equals("ADMIN")) {
            for (Integer id : ids) {
                // 检查医生是否属于该公司
                if (!doctorService.checkDoctorCompany(id, adminCompanyId)) {
                    throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权删除其他公司的医生");
                }
            }
        }
        
        doctorService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询单个医生
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id, HttpServletRequest request) {
        // 获取当前管理员的公司ID
        Integer adminCompanyId = (Integer) request.getAttribute("companyId");
        
        // 如果是管理员，验证医生是否属于该管理员的公司
        if (adminCompanyId != null && TokenUtils.getCurrentRole().equals("ADMIN")) {
            // 检查医生是否属于该公司
            if (!doctorService.checkDoctorCompany(id, adminCompanyId)) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权查看其他公司的医生");
            }
        }
        
        Doctor doctor = doctorService.selectById(id);
        return Result.success(doctor);
    }

    /**
     * 查询所有医生信息，支持按公司和状态筛选
     */
    @GetMapping("/selectAll")
    public Result selectAll(Doctor doctor, HttpServletRequest request) {
        try {
            // 获取当前用户角色
            String currentRole = TokenUtils.getCurrentRole();
            Account currentUser = TokenUtils.getCurrentUser();
            
            // 如果是普通用户，强制使用其所属公司ID
            if ("USER".equals(currentRole)) {
                Employee employee = employeeService.selectById(currentUser.getId());
                if (employee == null) {
                    return Result.error("500", "无法获取用户信息");
                }
                
                // 从department表获取公司ID
                Integer companyId = employeeService.getEmployeeCompanyId(employee.getId());
                
                if (companyId == null) {
                    return Result.error("500", "无法获取用户所属公司信息");
                }
                doctor.setCompanyId(companyId);
            } else if ("DOCTOR".equals(currentRole)) {
                Doctor currentDoctor = doctorService.selectById(currentUser.getId());
                if (currentDoctor != null && currentDoctor.getCompanyId() != null) {
                    doctor.setCompanyId(currentDoctor.getCompanyId());
                }
            } else if ("ADMIN".equals(currentRole)) {
                // 获取当前管理员的公司ID
                Integer adminCompanyId = (Integer) request.getAttribute("companyId");
                if (adminCompanyId == null) {
                    adminCompanyId = adminService.getAdminCompanyId();
                }
                if (adminCompanyId != null) {
                    doctor.setCompanyId(adminCompanyId);
                }
            }
            
            List<Doctor> list = doctorService.selectAll(doctor);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "获取医生列表失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询医生
     */
    @GetMapping("/selectPage")
    public Result selectPage(Doctor doctor,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             HttpServletRequest request) {
        // 获取当前管理员的公司ID
        Integer companyId = getCurrentCompanyId(request);
        if (companyId != null) {
            // 如果有公司ID，只查询该公司的医生
            doctor.setCompanyId(companyId);
        }
        
        PageInfo<Doctor> pageInfo = doctorService.selectPage(doctor, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 查询前4名医生
     */
    @GetMapping("/top4")
    public Result top4(HttpServletRequest request) {
        // 获取当前用户的公司ID
        Integer companyId = getCurrentCompanyId(request);
        
        List<Doctor> list = doctorService.top4(companyId);
        return Result.success(list);
    }

    /**
     * 获取医生的可用时间段
     */
    @GetMapping("/availableTimes")
    public Result getAvailableTimes(@RequestParam Integer doctorId, HttpServletRequest request) {
        // 获取当前用户的公司ID
        Integer companyId = getCurrentCompanyId(request);
        
        // 如果有公司ID，验证医生是否属于该公司
        if (companyId != null && "ADMIN".equals(TokenUtils.getCurrentRole())) {
            // 检查医生是否属于该公司
            if (!doctorService.checkDoctorCompany(doctorId, companyId)) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权查看其他公司的医生");
            }
        }
        
        // 这里应该从数据库或服务中获取医生的实际可用时间
        // 为了演示，我们返回一些模拟的时间段
        List<String> availableTimes = generateAvailableTimes(doctorId);
        return Result.success(availableTimes);
    }
    
    /**
     * 生成模拟的可用时间段
     * 实际应用中，这应该从数据库中查询医生的排班和已有预约
     */
    private List<String> generateAvailableTimes(Integer doctorId) {
        List<String> times = new ArrayList<>();
        
        // 获取当前日期
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 添加一些模拟的时间段
        times.add(formatter.format(today) + " 09:00:00 - " + formatter.format(today) + " 10:00:00");
        times.add(formatter.format(today) + " 14:00:00 - " + formatter.format(today) + " 15:00:00");
        times.add(formatter.format(today) + " 16:00:00 - " + formatter.format(today) + " 17:00:00");
        times.add(formatter.format(tomorrow) + " 09:00:00 - " + formatter.format(tomorrow) + " 10:00:00");
        times.add(formatter.format(tomorrow) + " 14:00:00 - " + formatter.format(tomorrow) + " 15:00:00");
        times.add(formatter.format(dayAfterTomorrow) + " 10:00:00 - " + formatter.format(dayAfterTomorrow) + " 11:00:00");
        
        return times;
    }

    /**
     * 获取当前用户的公司ID
     */
    private Integer getCurrentCompanyId(HttpServletRequest request) {
        String currentRole = TokenUtils.getCurrentRole();
        Account currentUser = TokenUtils.getCurrentUser();
        
        // 如果是管理员
        if ("ADMIN".equals(currentRole)) {
            Integer adminCompanyId = (Integer) request.getAttribute("companyId");
            if (adminCompanyId == null) {
                adminCompanyId = adminService.getAdminCompanyId();
            }
            return adminCompanyId;
        } 
        // 如果是普通用户
        else if ("USER".equals(currentRole) && currentUser != null) {
            Employee employee = employeeService.selectById(currentUser.getId());
            if (employee != null) {
                return employeeService.getEmployeeCompanyId(employee.getId());
            }
        } 
        // 如果是医生
        else if ("DOCTOR".equals(currentRole) && currentUser != null) {
            Doctor currentDoctor = doctorService.selectById(currentUser.getId());
            if (currentDoctor != null) {
                return currentDoctor.getCompanyId();
            }
        }
        
        return null;
    }

    /**
     * 获取医生证书文件列表
     */
    @GetMapping("/certificates/{id}")
    public Result getCertificates(@PathVariable Integer id, HttpServletRequest request) {
        // 获取当前管理员的公司ID
        Integer adminCompanyId = (Integer) request.getAttribute("companyId");
        
        // 如果是管理员，验证医生是否属于该管理员的公司
        if (adminCompanyId != null && TokenUtils.getCurrentRole().equals("ADMIN")) {
            // 检查医生是否属于该公司
            if (!doctorService.checkDoctorCompany(id, adminCompanyId)) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权查看其他公司的医生");
            }
        }
        
        Doctor doctor = doctorService.selectById(id);
        if (doctor == null) {
            return Result.error("404", "医生不存在");
        }
        
        String certificateStr = doctor.getCertificate();
        List<String> certificates = new ArrayList<>();
        
        if (certificateStr != null && !certificateStr.isEmpty()) {
            try {
                // 尝试解析JSON字符串
                ObjectMapper mapper = new ObjectMapper();
                certificates = mapper.readValue(certificateStr, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                // 如果解析失败，可能是旧格式，直接添加单个URL
                certificates.add(certificateStr);
            }
        }
        
        return Result.success(certificates);
    }
}
