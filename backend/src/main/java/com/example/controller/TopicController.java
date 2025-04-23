package com.example.controller;

import com.example.common.Result;
import com.example.entity.Topic;
import com.example.service.TopicService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题库控制器，处理与题库信息相关的请求。
 */
@RestController
@RequestMapping("/topic")
public class TopicController {

    @Resource
    private TopicService topicService;

    /**
     * 从请求中获取当前用户的公司ID
     */
    private Integer getCompanyIdFromRequest(HttpServletRequest request) {
        // 从请求属性中获取公司ID，这是由CompanyInterceptor设置的
        Object companyIdObj = request.getAttribute("companyId");
        if (companyIdObj instanceof Integer) {
            return (Integer) companyIdObj;
        }
        return null;
    }

    /**
     * 新增题目
     */
    @PostMapping("/add")
    public Result add(@RequestBody Topic topic, HttpServletRequest request) {
        Integer companyId = getCompanyIdFromRequest(request);
        if (companyId == null) {
            return Result.error("401", "未登录或无权限");
        }
        topic.setCompanyId(companyId);
        topicService.add(topic);
        return Result.success();
    }

    /**
     * 修改题目
     */
    @PutMapping("/update")
    public Result update(@RequestBody Topic topic, HttpServletRequest request) {
        Integer companyId = getCompanyIdFromRequest(request);
        if (companyId == null) {
            return Result.error("401", "未登录或无权限");
        }
        topic.setCompanyId(companyId);
        topicService.updateById(topic);
        return Result.success();
    }

    /**
     * 删除单个题目
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id, HttpServletRequest request) {
        Integer companyId = getCompanyIdFromRequest(request);
        if (companyId == null) {
            return Result.error("401", "未登录或无权限");
        }
        topicService.deleteById(id, companyId);
        return Result.success();
    }

    /**
     * 批量删除题目
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids, HttpServletRequest request) {
        Integer companyId = getCompanyIdFromRequest(request);
        if (companyId == null) {
            return Result.error("401", "未登录或无权限");
        }
        topicService.deleteBatch(ids, companyId);
        return Result.success();
    }

    /**
     * 查询单个题目
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id, HttpServletRequest request) {
        Integer companyId = getCompanyIdFromRequest(request);
        Topic topic = topicService.selectById(id, companyId);
        return Result.success(topic);
    }

    /**
     * 查询所有题目
     */
    @GetMapping("/selectAll")
    public Result selectAll(Topic topic, HttpServletRequest request) {
        Integer companyId = getCompanyIdFromRequest(request);
        if (companyId != null) {
            topic.setCompanyId(companyId);
        }
        List<Topic> list = topicService.selectAll(topic);
        return Result.success(list);
    }

    /**
     * 分页查询题目
     */
    @GetMapping("/selectPage")
    public Result selectPage(Topic topic,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             HttpServletRequest request) {
        Integer companyId = getCompanyIdFromRequest(request);
        if (companyId != null) {
            topic.setCompanyId(companyId);
        }
        PageInfo<Topic> pageInfo = topicService.selectPage(topic, pageNum, pageSize);
        return Result.success(pageInfo);
    }
}
