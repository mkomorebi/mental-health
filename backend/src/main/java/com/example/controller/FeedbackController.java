package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Feedback;
import com.example.service.FeedbackService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.example.common.enums.RoleEnum;
import java.util.List;

/**
 * 反馈控制器，处理用户反馈相关的HTTP请求
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;

    /**
     * 添加新的反馈
     * @param feedback 反馈信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody Feedback feedback) {
        feedbackService.add(feedback);
        return Result.success();
    }

    /**
     * 更新反馈信息（主要用于管理员回复）
     * @param feedback 反馈信息
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result update(@RequestBody Feedback feedback) {
        feedbackService.updateById(feedback);
        return Result.success();
    }

    /**
     * 删除反馈
     * @param id 反馈ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        // 获取当前用户
        Account currentUser = TokenUtils.getCurrentUser();
        // 获取要删除的反馈
        Feedback feedback = feedbackService.selectById(id);
        
        if (feedback == null) {
            return Result.error("404", "反馈不存在");
        }
        
        // 检查权限
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            // 普通用户只能删除自己的反馈
            if (!feedback.getUserId().equals(currentUser.getId())) {
                return Result.error("403", "无权删除他人的反馈");
            }
        } else if (RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            // 管理员只能删除自己公司的员工的反馈
            // 这里需要通过用户ID查询用户所属公司，然后比较
            Integer companyId = (Integer) TokenUtils.getRequest().getAttribute("companyId");
            if (companyId != null) {
                // 通过反馈的用户ID查询该用户所属的公司
                Integer feedbackUserCompanyId = feedbackService.getUserCompanyId(feedback.getUserId());
                if (feedbackUserCompanyId != null && !companyId.equals(feedbackUserCompanyId)) {
                    return Result.error("403", "无权删除其他公司的反馈");
                }
            }
        }
        
        feedbackService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除反馈
     * @param ids 反馈ID列表
     * @return 操作结果
     */
    @PostMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        feedbackService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据ID查询反馈
     * @param id 反馈ID
     * @return 反馈信息
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        Feedback feedback = feedbackService.selectById(id);
        return Result.success(feedback);
    }

    /**
     * 查询所有反馈
     * @param feedback 查询条件
     * @return 反馈列表
     */
    @GetMapping("/selectAll")
    public Result selectAll(Feedback feedback) {
        List<Feedback> list = feedbackService.selectAll(feedback);
        return Result.success(list);
    }

    /**
     * 分页查询反馈
     * @param feedback 查询条件
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 分页结果
     */
    @GetMapping("/selectPage")
    public Result selectPage(Feedback feedback,
                            @RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Feedback> pageInfo = feedbackService.selectPage(feedback, pageNum, pageSize);
        return Result.success(pageInfo);
    }
}
