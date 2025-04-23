package com.example.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.example.common.Result;
import com.example.entity.Employee;
import com.example.excel.DefaultExcelListener;
import com.example.excel.ExcelListener;
import com.example.excel.ExcelResult;
import com.example.excel.ExcelUtil;
import com.example.service.EmployeeService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工信息前端请求接口
 */
@RestController
@RequestMapping("/user")
public class EmployeeController {
    
    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @Resource
    private EmployeeService employeeService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Employee employee, HttpServletRequest request) {
        // 获取请求中的公司ID属性（由CompanyInterceptor设置）
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            employee.setCompanyId(companyId);
        }
        
        employeeService.add(employee);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Employee employee) {
        employeeService.updateById(employee);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        employeeService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        employeeService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 单个查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Employee employee = employeeService.selectById(id);
        return Result.success(employee);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Employee employee, HttpServletRequest request) {
        // 获取请求中的公司ID属性（由CompanyInterceptor设置）
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            employee.setCompanyId(companyId);
        }
        
        List<Employee> list = employeeService.selectAll(employee);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Employee employee,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             HttpServletRequest request) {
        // 获取请求中的公司ID属性（由CompanyInterceptor设置）
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            employee.setCompanyId(companyId);
        }
        
        PageInfo<Employee> pageInfo = employeeService.selectPage(employee, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 导出
     */
    @GetMapping("/export")
    public void export(Employee employee, HttpServletResponse response, HttpServletRequest request) {
        // 获取请求中的公司ID属性（由CompanyInterceptor设置）
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            employee.setCompanyId(companyId);
        }
        
        List<Employee> employees = employeeService.selectAll(employee);
        ExcelUtil.exportExcel(employees, "员工信息", Employee.class, response);
    }

    /**
     * 下载用户导入模板
     * 提供标准格式的Excel模板，用于用户批量导入
     */
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            log.info("开始下载用户导入模板");
            
            // 创建示例数据
            List<Employee> list = new ArrayList<>();
            
            // 添加示例数据，帮助用户理解如何填写
            Employee example1 = new Employee();
            example1.setUsername("zhangsan");
            example1.setName("张三");
            example1.setPhone("13800138000");
            example1.setEmail("zhangsan@example.com");
            example1.setDepartmentId(1);
            example1.setRole("USER");
            list.add(example1);
            
            Employee example2 = new Employee();
            example2.setUsername("lisi");
            example2.setName("李四");
            example2.setPhone("13900139000");
            example2.setEmail("lisi@example.com");
            example2.setDepartmentId(2);
            example2.setRole("USER");
            list.add(example2);
            
            // 导出模板
            ExcelUtil.exportExcel(list, "用户导入模板", Employee.class, response);
            log.info("用户导入模板下载成功");
        } catch (Exception e) {
            log.error("下载用户导入模板失败", e);
        }
    }

    /**
     * 导入用户数据
     * 支持批量导入用户，并提供详细的导入结果反馈
     * 
     * @param file 导入的Excel文件
     * @param request HTTP请求对象
     * @return 导入结果
     */
    @PostMapping(value = "/import")
    public Result importData(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        log.info("开始导入用户数据，文件名: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        
        // 获取请求中的公司ID属性（由CompanyInterceptor设置）
        Integer companyId = (Integer) request.getAttribute("companyId");
        
        // 创建导入结果统计
        int successCount = 0;
        int failCount = 0;
        StringBuilder errorMsg = new StringBuilder();
        
        try {
            // 使用ExcelListener处理导入，可以获取更详细的导入结果
            ExcelListener<Employee> listener = new DefaultExcelListener<>(true);
            EasyExcel.read(file.getInputStream(), Employee.class, listener).sheet().doRead();
            
            // 获取解析结果
            ExcelResult<Employee> excelResult = listener.getExcelResult();
            List<Employee> employees = excelResult.getList();
            
            // 检查是否有数据
            if (employees.isEmpty()) {
                log.warn("导入文件中没有有效数据");
                return Result.error("导入失败：文件中没有有效数据");
            }
            
            log.info("成功解析 {} 条数据，开始导入", employees.size());
            
            // 处理每条数据
            for (Employee employee : employees) {
                try {
                    // 设置默认值
                    employee.setId(null); // 确保ID为空，让数据库自动生成
                    
                    // 如果没有设置密码，默认使用用户名作为密码
                    if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
                        employee.setPassword(employee.getUsername());
                    }
                    
                    // 如果有公司ID，确保导入的员工属于该公司
                    if (companyId != null) {
                        employee.setCompanyId(companyId);
                    }
                    
                    // 添加到数据库
                    employeeService.add(employee);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    // 记录失败原因
                    errorMsg.append("行 ").append(failCount + successCount)
                           .append(" 导入失败: ").append(e.getMessage())
                           .append("<br/>");
                    log.error("导入用户失败: {}", e.getMessage());
                }
            }
            
            // 构建导入结果消息
            String resultMsg;
            if (failCount == 0) {
                resultMsg = "导入成功！共导入 " + successCount + " 条数据";
                log.info(resultMsg);
                return Result.success(resultMsg);
            } else {
                resultMsg = "部分导入成功！成功导入 " + successCount + " 条数据，失败 " + failCount + " 条";
                log.warn(resultMsg);
                
                // 创建包含错误信息的Map
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("analysis", errorMsg.toString());
                resultData.put("successCount", successCount);
                resultData.put("failCount", failCount);
                
                // 将Map转换为JSON字符串
                String resultDataJson = JSON.toJSONString(resultData);
                
                // 返回包含详细错误信息的结果
                return Result.error(resultMsg, resultDataJson);
            }
        } catch (Exception e) {
            log.error("导入用户数据异常", e);
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前员工所属的公司ID
     */
    @GetMapping("/getCompanyId")
    public Result getCompanyId() {
        // 获取当前登录用户ID
        Integer userId = TokenUtils.getCurrentUser().getId();
        if (userId == null) {
            return Result.error("401", "未登录或登录已过期");
        }
        
        // 获取员工信息
        Employee employee = employeeService.selectById(userId);
        if (employee == null) {
            return Result.error("404", "未找到员工信息");
        }
        
        // 获取员工所属部门的公司ID
        Integer companyId = employeeService.getEmployeeCompanyId(employee.getId());
        if (companyId == null) {
            return Result.error("404", "未找到公司信息");
        }
        
        return Result.success(companyId);
    }
}
