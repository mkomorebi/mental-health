package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Employee;
import com.example.entity.Feedback;
import com.example.mapper.EmployeeMapper;
import com.example.mapper.FeedbackMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FeedbackService 业务层，用于处理用户反馈相关的业务逻辑。
 * 提供对 Feedback 的增、删、改、查等操作。
 */
@Service
public class FeedbackService {

    @Resource
    private FeedbackMapper feedbackMapper;
    
    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 添加新的 Feedback 记录。
     *
     * @param feedback Feedback 实体
     */
    public void add(Feedback feedback) {
        Account currentUser = TokenUtils.getCurrentUser();
        feedback.setUserId(currentUser.getId());
        feedback.setTime(DateUtil.now());
        feedback.setStatus("待回复");
        
        // 确保 imageUrls 字段被正确设置
        if (feedback.getImageUrls() != null) {
            // 去除空白字符
            String trimmedUrls = feedback.getImageUrls().trim();
            // 如果为空字符串则设为 null
            feedback.setImageUrls(trimmedUrls.isEmpty() ? null : trimmedUrls);
        }
        
        feedbackMapper.insert(feedback);
    }

    public void updateById(Feedback feedback) {
        Account currentUser = TokenUtils.getCurrentUser();
        feedback.setReplyName(currentUser.getUsername());
        feedback.setReplyTime(DateUtil.now());
        feedback.setStatus("已回复");
        
        // 设置回复人ID，确保当前用户是管理员
        if (RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            feedback.setReplyId(currentUser.getId());
        }
        
        feedbackMapper.updateById(feedback);
    }

    public void deleteById(Integer id) {
        feedbackMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            feedbackMapper.deleteById(id);
        }
    }

    public Feedback selectById(Integer id) {
        return feedbackMapper.selectById(id);
    }

    public List<Feedback> selectAll(Feedback feedback) {
        // 添加公司隔离
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            // 从请求属性中获取公司ID
            Integer companyId = (Integer) TokenUtils.getRequest().getAttribute("companyId");
            if (companyId != null) {
                feedback.setCompanyId(companyId);
            }
        }
        return feedbackMapper.selectAll(feedback);
    }

    public PageInfo<Feedback> selectPage(Feedback feedback, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            feedback.setUserId(currentUser.getId());
        } else if (RoleEnum.ADMIN.name().equals(currentUser.getRole()) || 
                   RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
            // 从请求属性中获取公司ID
            Integer companyId = (Integer) TokenUtils.getRequest().getAttribute("companyId");
            if (companyId != null) {
                feedback.setCompanyId(companyId);
            }
        }
        
        PageHelper.startPage(pageNum, pageSize);
        List<Feedback> list = feedbackMapper.selectAll(feedback);
        return PageInfo.of(list);
    }

    /**
     * 根据用户ID获取用户所属公司ID
     * @param userId 用户ID
     * @return 公司ID
     */
    public Integer getUserCompanyId(Integer userId) {
        Employee employee = employeeMapper.selectById(userId);
        if (employee != null && employee.getDepartmentId() != null) {
            return employeeMapper.getCompanyIdByDepartmentId(employee.getDepartmentId());
        }
        return null;
    }
}
