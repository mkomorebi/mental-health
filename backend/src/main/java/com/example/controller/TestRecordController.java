package com.example.controller;

import com.example.common.Result;
import com.example.entity.TestPaper;
import com.example.entity.TestRecord;
import com.example.entity.resp.DepartmentStatsResp;
import com.example.service.TestRecordService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.Doctor;
import com.example.utils.TokenUtils;
import com.example.mapper.DoctorMapper;

import java.io.IOException;
import java.util.List;

/**
 * 测试记录控制器，处理与测试记录相关的请求。
 */
@RestController
@RequestMapping("/testRecord")
public class TestRecordController {

    @Resource
    private TestRecordService testRecordService;

    @Resource
    private DoctorMapper doctorMapper;

    /**
     * 新增测试记录
     */
    @PostMapping("/add")
    public Result add(@RequestBody TestPaper testPaper) {
        testRecordService.add(testPaper);
        return Result.success();
    }

    /**
     * 修改测试记录
     */
    @PutMapping("/update")
    public Result update(@RequestBody TestRecord testRecord) {
        testRecordService.updateById(testRecord);
        return Result.success();
    }

    /**
     * 删除单个测试记录
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            // 获取要删除的记录
            TestRecord record = testRecordService.selectById(id);
            if (record == null) {
                return Result.error("记录不存在");
            }

            // 检查当前用户是否有权限删除该记录
            Account currentUser = TokenUtils.getCurrentUser();
            Integer userCompanyId = null;

            if ("ADMIN".equals(currentUser.getRole())) {
                Admin admin = TokenUtils.getCurrentAdmin();
                if (admin != null) {
                    userCompanyId = admin.getCompanyId();
                }
            } else if ("DOCTOR".equals(currentUser.getRole())) {
                Doctor doctor = doctorMapper.selectById(currentUser.getId());
                if (doctor != null) {
                    userCompanyId = doctor.getCompanyId();
                }
            } else if ("USER".equals(currentUser.getRole())) {
                // 普通用户只能删除自己的记录
                if (!currentUser.getId().equals(record.getUserId())) {
                    return Result.error("您只能删除自己的测试记录");
                }
            }

            // 检查公司ID是否匹配（对管理员和医生）
            if (userCompanyId != null && !userCompanyId.equals(record.getCompanyId())) {
                return Result.error("您没有权限删除其他公司的测试记录");
            }

            testRecordService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            System.err.println("删除测试记录时发生错误: " + e.getMessage());
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除测试记录
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        testRecordService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询单个测试记录
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        TestRecord testRecord = testRecordService.selectById(id);
        return Result.success(testRecord);
    }

    /**
     * 查询所有测试记录
     */
    @GetMapping("/selectAll")
    public Result selectAll(TestRecord testRecord) {
        List<TestRecord> list = testRecordService.selectAll(testRecord);
        return Result.success(list);
    }

    /**
     * 分页查询测试记录
     */
    @GetMapping("/selectPage")
    public Result selectPage(TestRecord testRecord,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             HttpServletRequest request) {
        try {
            // 从请求属性中获取公司ID
            Integer companyId = (Integer) request.getAttribute("companyId");
            
            // 获取当前用户
            Account currentUser = TokenUtils.getCurrentUser();
            
            // 设置公司ID条件（对管理员和医生生效）
            if ("ADMIN".equals(currentUser.getRole()) || "DOCTOR".equals(currentUser.getRole())) {
                testRecord.setCompanyId(companyId);
            } else if ("USER".equals(currentUser.getRole())) {
                // 普通用户只能看到自己的记录
                testRecord.setUserId(currentUser.getId());
            }
            
            PageInfo<TestRecord> pageInfo = testRecordService.selectPage(testRecord, pageNum, pageSize);
            return Result.success(pageInfo);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取测试记录统计信息
     */
    @GetMapping("/metric")
    public Result metric(HttpServletRequest request) {
        // 从请求属性中获取公司ID
        Integer companyId = (Integer) request.getAttribute("companyId");
        return Result.success(testRecordService.metric(companyId));
    }

    /**
     * 查询近七天测试记录
     */
    @GetMapping("/selectRecentWeekPage")
    public Result selectRecentWeekPage(TestRecord testRecord,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<TestRecord> pageInfo = testRecordService.selectRecentWeekPage(testRecord, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 查询医生发布的测试记录
     */
    @GetMapping("/selectDoctorPage")
    public Result selectDoctorPage(
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String testPaperName,
        @RequestParam(required = false) String userName,
        @RequestParam(required = false) Integer departmentId,
        @RequestParam(required = false) Double minScore,
        @RequestParam(required = false) Double maxScore,
        @RequestParam(required = false) Integer doctorId) {
        
        if (doctorId == null) {
            return Result.error("医生ID不能为空");
        }
        
        // 创建查询条件，确保只返回该医生发布的测试记录
        TestRecord query = new TestRecord();
        if (testPaperName != null) query.setTestPaperName(testPaperName);
        if (userName != null) query.setUserName(userName);
        if (departmentId != null) query.setDepartmentId(departmentId);
        
        // 使用doctorId查询该医生发布的测试记录
        PageInfo<TestRecord> pageInfo = testRecordService.selectDoctorPage(
            pageNum, pageSize, query, minScore, maxScore, doctorId);
        
        return Result.success(pageInfo);
    }

    /**
     * 获取部门统计数据
     */
    @GetMapping("/departmentStats")
    public Result getDepartmentStats(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String paperTitle,
            @RequestParam(required = false) String publishTime) {
        
        PageInfo<DepartmentStatsResp> pageInfo = testRecordService.getDepartmentStats(
                pageNum, pageSize, departmentName, paperTitle, publishTime);
        
        return Result.success(pageInfo);
    }

    /**
     * 导出部门统计数据
     */
    @GetMapping("/exportDepartmentStats")
    public void exportDepartmentStats(
            HttpServletResponse response,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String paperTitle,
            @RequestParam(required = false) String publishTime) throws IOException {
        
        testRecordService.exportDepartmentStats(response, departmentName, paperTitle, publishTime);
    }

    /**
     * 根据用户ID查询测试记录
     */
    @GetMapping("/selectByUserId")
    public Result selectByUserId(
        @RequestParam Integer userId,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "50") Integer pageSize) {
        
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        
        // 创建查询条件
        TestRecord query = new TestRecord();
        query.setUserId(userId);
        
        // 查询该用户的测试记录
        PageInfo<TestRecord> pageInfo = testRecordService.selectPage(query, pageNum, pageSize);
        
        return Result.success(pageInfo);
    }

}
