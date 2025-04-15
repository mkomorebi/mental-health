package com.example.mapper;

import com.example.entity.Feedback;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * FeedbackMapper 接口，用于操作 Feedback 实体的数据库映射。
 * 提供对 Feedback 表的基本 CRUD 操作。
 */
public interface FeedbackMapper {

    int insert(Feedback feedback); // 插入新的 Feedback 记录

    void updateById(Feedback feedback); // 根据 ID 更新 Feedback 记录

    void deleteById(Integer id); // 根据 ID 删除 Feedback 记录

    @Select("select * from `feedback` where id = #{id}")
    Feedback selectById(Integer id); // 根据 ID 查询 Feedback 记录

    List<Feedback> selectAll(Feedback feedback); // 查询所有 Feedback 记录

    //根据用户ID和反馈类型查询记录
    @Select("select * from `feedback` where user_id = #{userId} and type = #{type}")
    List<Feedback> selectByUserIdAndType(Integer userId, String type);
    
    


}
