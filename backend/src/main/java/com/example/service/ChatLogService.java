package com.example.service;

import cn.hutool.core.lang.Assert;
import com.example.entity.ChatLog;
import com.example.mapper.ChatLogMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ChatLogService 业务层，用于处理与 ChatLog 实体相关的业务逻辑。
 * 提供对 ChatLog 的增、删、改、查等操作。
 */
@Service
public class ChatLogService {

    @Resource
    private ChatLogMapper chatLogMapper;

    /**
     * 添加新的 ChatLog 记录。
     *
     * @param chatLog ChatLog 实体
     */
    public void add(ChatLog chatLog) {
        Assert.notNull(chatLog.getId(), "ID不能为空");
        chatLogMapper.insert(chatLog);
    }

    public void updateById(ChatLog chatLog) {
        Assert.notNull(chatLog.getId(), "ID不能为空");
        chatLogMapper.updateById(chatLog);
    }

    public void deleteById(String id) {
        chatLogMapper.deleteById(id);
    }

    public void deleteBatch(List<String> ids) {
        for (String id : ids) {
            chatLogMapper.deleteById(id);
        }
    }

    public ChatLog selectById(Integer id) {
        return chatLogMapper.selectById(id);
    }

    public List<ChatLog> selectAll(Integer userId) {
        return chatLogMapper.selectAll(userId);
    }

    public PageInfo<ChatLog> selectPage(Integer pageNum, Integer pageSize,Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<ChatLog> list = chatLogMapper.selectAll(userId);
        return PageInfo.of(list);
    }

}
