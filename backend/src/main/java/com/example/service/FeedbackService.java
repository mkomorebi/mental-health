package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Feedback;
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
        feedbackMapper.insert(feedback);
    }

    public void updateById(Feedback feedback) {
        Account currentUser = TokenUtils.getCurrentUser();
        feedback.setReplyName(currentUser.getUsername());
        feedback.setReplyTime(DateUtil.now());
        feedback.setStatus("已回复");
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
        return feedbackMapper.selectAll(feedback);
    }

    public PageInfo<Feedback> selectPage(Feedback feedback, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            feedback.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Feedback> list = feedbackMapper.selectAll(feedback);
        return PageInfo.of(list);
    }

}
