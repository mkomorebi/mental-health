package com.example.controller;

import com.example.common.Result;
import com.example.entity.TestPaper;
import com.example.service.TestPaperService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.example.exception.CustomException;
import com.example.mapper.DoctorMapper;

import java.util.List;

/**
 * 试卷控制器，处理与试卷信息相关的请求。
 */
@RestController
@RequestMapping("/testPaper")
public class TestPaperController {

    @Resource
    private TestPaperService testPaperService;

    @Resource
    private DoctorMapper doctorMapper;

    /**
     * 新增试卷
     */
    @PostMapping("/add")
    public Result add(@RequestBody TestPaper testPaper) {
        testPaperService.add(testPaper);
        return Result.success();
    }

    /**
     * 修改试卷
     */
    @PutMapping("/update")
    public Result update(@RequestBody TestPaper testPaper) {
        testPaperService.updateById(testPaper);
        return Result.success();
    }

    /**
     * 删除单个试卷
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            testPaperService.deleteById(id);
            return Result.success();
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除试卷
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        testPaperService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询单个试卷
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        try {
            TestPaper testPaper = testPaperService.selectById(id);
            return Result.success(testPaper);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有试卷
     */
    @GetMapping("/selectAll")
    public Result selectAll(TestPaper testPaper) {
        List<TestPaper> list = testPaperService.selectAll(testPaper);
        return Result.success(list);
    }

    /**
     * 查询所有试卷（降序）
     */
    @GetMapping("/selectAllDesc")
    public Result selectAllDesc() {
        List<TestPaper> list = testPaperService.selectAllDesc();
        return Result.success(list);
    }

    /**
     * 分页查询试卷
     */
    @GetMapping("/selectPage")
    public Result selectPage(TestPaper testPaper,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            PageInfo<TestPaper> pageInfo = testPaperService.selectPage(testPaper, pageNum, pageSize);
            return Result.success(pageInfo);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "查询失败：" + e.getMessage());
        }
    }
}
