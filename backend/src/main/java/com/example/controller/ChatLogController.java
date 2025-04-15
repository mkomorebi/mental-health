package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.common.Result;
import com.example.entity.ChatLog;
import com.example.entity.req.ChatReq;
import com.example.entity.resp.ChatResp;
import com.example.service.ChatLogService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天记录控制器，处理与聊天记录相关的请求。
 */
@RestController
@RequestMapping("/chatLog")
public class ChatLogController {

    @Resource
    private ChatLogService chatLogService;

    /**
     * 新增聊天记录
     */
    @PostMapping("/add")
    public Result add(@RequestBody ChatReq chatLog) {
        ChatLog bean = BeanUtil.toBean(chatLog, ChatLog.class);
        bean.setConversation(JSONObject.toJSONString(chatLog.getConversation()));
        chatLogService.add(bean);
        return Result.success();
    }

    /**
     * 修改聊天记录
     */
    @PutMapping("/update")
    public Result update(@RequestBody ChatReq chatLog) {
        ChatLog bean = BeanUtil.toBean(chatLog, ChatLog.class);
        bean.setConversation(JSONObject.toJSONString(chatLog.getConversation()));
        chatLogService.updateById(bean);
        return Result.success();
    }

    /**
     * 删除单个聊天记录
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        chatLogService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除聊天记录
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<String> ids) {
        chatLogService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询单个聊天记录
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        return Result.success(chatLogService.selectById(id));
    }

    /**
     * 查询所有聊天记录
     */
    @GetMapping("/selectAll")
    public Result selectAll(Integer userId) {
        List<ChatLog> chatLogs = chatLogService.selectAll(userId);
        List<ChatResp> result = chatLogs.stream().map(chatLog -> {
            ChatResp bean = BeanUtil.toBean(chatLog, ChatResp.class);
            bean.setConversation(JSONObject.parseArray(chatLog.getConversation()));
            return bean;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 分页查询聊天记录
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize, Integer userId) {
        PageInfo<ChatLog> pageInfo = chatLogService.selectPage(pageNum, pageSize, userId);
        return Result.success(pageInfo);
    }
}
