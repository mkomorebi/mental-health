package com.example.controller;

import com.example.common.Result;
import com.example.entity.TestPaper;
import com.example.entity.TestRecord;
import com.example.service.TestRecordService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试记录控制器，处理与测试记录相关的请求。
 */
@RestController
@RequestMapping("/testRecord")
public class TestRecordController {

    @Resource
    private TestRecordService testRecordService;

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
        testRecordService.deleteById(id);
        return Result.success();
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
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<TestRecord> pageInfo = testRecordService.selectPage(testRecord, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 获取测试记录统计信息
     */
    @GetMapping("/metric")
    public Result metric() {
        return Result.success(testRecordService.metric());
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

}
