package com.example.controller;

import com.auth0.jwt.JWT;
import com.example.common.Result;
import com.example.entity.Notice;
import com.example.service.EmployeeService;
import com.example.service.NoticeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.example.service.AdminService;

import java.util.List;

/**
 * 通知控制器，处理与通知相关的请求。
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;
    
    @Resource
    private EmployeeService employeeService;

    @Resource
    private AdminService adminService;

    /**
     * 新增通知
     */
    @PostMapping("/add")
    public Result add(@RequestBody Notice notice, HttpServletRequest request) {
        // 从请求中获取公司ID
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            notice.setCompanyId(companyId);
        }
        
        // 从token中获取管理员ID
        String token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            try {
                String audience = JWT.decode(token).getAudience().get(0);
                if (audience != null && audience.contains("-")) {
                    String userId = audience.split("-")[0];
                    notice.setAdminId(Integer.valueOf(userId));
                }
            } catch (Exception e) {
                // 记录异常，但不影响请求继续处理
                System.err.println("获取管理员ID失败: " + e.getMessage());
            }
        }
        
        noticeService.add(notice);
        return Result.success();
    }

    /**
     * 修改通知
     */
    @PutMapping("/update")
    public Result update(@RequestBody Notice notice, HttpServletRequest request) {
        // 从请求中获取公司ID，确保只能修改自己公司的通知
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            // 先查询原通知
            Notice existingNotice = noticeService.selectById(notice.getId());
            if (existingNotice == null) {
                return Result.error("404", "通知不存在");
            }
            
            // 获取通知所属管理员的公司ID
            Integer noticeCompanyId = adminService.getAdminCompanyId(existingNotice.getAdminId());
            
            if (noticeCompanyId == null || !companyId.equals(noticeCompanyId)) {
                return Result.error("403", "无权修改其他公司的通知");
            }
            
            // 保持原有的管理员ID
            notice.setAdminId(existingNotice.getAdminId());
        }
        
        noticeService.updateById(notice);
        return Result.success();
    }

    /**
     * 删除单个通知
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id, HttpServletRequest request) {
        // 从请求中获取公司ID，确保只能删除自己公司的通知
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            Notice existingNotice = noticeService.selectById(id);
            if (existingNotice == null) {
                return Result.error("404", "通知不存在");
            }
            
            // 获取通知所属管理员的公司ID
            Integer noticeCompanyId = adminService.getAdminCompanyId(existingNotice.getAdminId());
            
            if (noticeCompanyId == null || !companyId.equals(noticeCompanyId)) {
                return Result.error("403", "无权删除其他公司的通知");
            }
        }
        
        noticeService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除通知
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids, HttpServletRequest request) {
        // 从请求中获取公司ID，确保只能删除自己公司的通知
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            for (Integer id : ids) {
                Notice existingNotice = noticeService.selectById(id);
                if (existingNotice == null) {
                    continue; // 跳过不存在的通知
                }
                
                // 获取通知所属管理员的公司ID
                Integer noticeCompanyId = adminService.getAdminCompanyId(existingNotice.getAdminId());
                
                if (noticeCompanyId == null || !companyId.equals(noticeCompanyId)) {
                    return Result.error("403", "无权删除其他公司的通知");
                }
            }
        }
        
        noticeService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询单个通知
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Notice notice = noticeService.selectById(id);
        return Result.success(notice);
    }

    /**
     * 查询所有通知
     */
    @GetMapping("/selectAll")
    public Result selectAll(Notice notice, HttpServletRequest request) {
        // 从请求中获取公司ID，确保只能查看自己公司的通知
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            notice.setCompanyId(companyId);
        } else {
            // 从token中获取用户角色和ID
            String token = request.getHeader("token");
            if (token != null && !token.isEmpty()) {
                try {
                    String audience = JWT.decode(token).getAudience().get(0);
                    if (audience != null && audience.contains("-")) {
                        String role = audience.split("-")[1];
                        
                        // 如果是员工，需要查询员工所属公司
                        if ("USER".equals(role)) {
                            String userId = audience.split("-")[0];
                            Integer employeeCompanyId = employeeService.getEmployeeCompanyId(Integer.valueOf(userId));
                            if (employeeCompanyId != null) {
                                notice.setCompanyId(employeeCompanyId);
                            }
                        }
                    }
                } catch (Exception e) {
                    // 记录异常，但不影响请求继续处理
                    System.err.println("解析token失败: " + e.getMessage());
                }
            }
        }
        
        List<Notice> list = noticeService.selectAll(notice);
        return Result.success(list);
    }

    /**
     * 分页查询通知
     */
    @GetMapping("/selectPage")
    public Result selectPage(Notice notice,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             HttpServletRequest request) {
        // 从请求中获取公司ID，确保只能查看自己公司的通知
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            notice.setCompanyId(companyId);
        }
        
        // 从token中获取用户角色和ID
        String token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            try {
                String audience = JWT.decode(token).getAudience().get(0);
                if (audience != null && audience.contains("-")) {
                    String role = audience.split("-")[1];
                    
                    // 如果是员工，需要查询员工所属公司
                    if ("USER".equals(role)) {
                        String userId = audience.split("-")[0];
                        // 这里需要通过员工ID查询其所属公司ID
                        Integer employeeCompanyId = getEmployeeCompanyId(Integer.valueOf(userId));
                        if (employeeCompanyId != null) {
                            notice.setCompanyId(employeeCompanyId);
                        }
                    }
                }
            } catch (Exception e) {
                // 记录异常，但不影响请求继续处理
                System.err.println("解析token失败: " + e.getMessage());
            }
        }
        
        PageInfo<Notice> pageInfo = noticeService.selectPage(notice, pageNum, pageSize);
        return Result.success(pageInfo);
    }
    
    /**
     * 获取员工所属公司ID
     * 这里需要通过员工->部门->公司的关系查询
     */
    private Integer getEmployeeCompanyId(Integer employeeId) {
        // 调用EmployeeService中的方法获取员工所属公司ID
        return employeeService.getEmployeeCompanyId(employeeId);
    }
}
