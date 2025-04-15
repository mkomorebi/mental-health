package com.example.mapper;

import com.example.entity.Topic;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * TopicMapper 接口，用于操作 Topic 实体的数据库映射。
 * 提供对 Topic 表的基本 CRUD 操作。
 */
public interface TopicMapper {

    int insert(Topic topic); // 插入新的 Topic 记录

    void updateById(Topic topic); // 根据 ID 更新 Topic 记录

    void deleteById(Integer id); // 根据 ID 删除 Topic 记录

    @Select("select * from `topic` where id = #{id}")
    Topic selectById(Integer id); // 根据 ID 查询 Topic 记录

    List<Topic> selectAll(Topic topic); // 查询所有 Topic 记录

}
