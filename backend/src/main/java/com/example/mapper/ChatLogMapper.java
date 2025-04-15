package com.example.mapper;

import com.example.entity.ChatLog;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ChatLogMapper 接口，用于操作 ChatLog 实体的数据库映射。
 * 提供对 ChatLog 表的基本 CRUD 操作。
 */
public interface ChatLogMapper {

    int insert(ChatLog chatLog); // 插入新的 ChatLog 记录

    void updateById(ChatLog chatLog); // 根据 ID 更新 ChatLog 记录

    void deleteById(String id); // 根据 ID 删除 ChatLog 记录

    @Select("select * from `chat_log` where id = #{id}")
    ChatLog selectById(Integer id); // 根据 ID 查询 ChatLog 记录

    List<ChatLog> selectAll(Integer userId); // 根据用户 ID 查询所有 ChatLog 记录

}
