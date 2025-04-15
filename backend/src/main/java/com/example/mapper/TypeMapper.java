package com.example.mapper;

import com.example.entity.Type;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * TypeMapper 接口，用于操作 Type 实体的数据库映射。
 * 提供对 Type 表的基本 CRUD 操作。
 */
public interface TypeMapper {

    int insert(Type type); // 插入新的 Type 记录

    void updateById(Type type); // 根据 ID 更新 Type 记录

    void deleteById(Integer id); // 根据 ID 删除 Type 记录

    @Select("select * from `type` where id = #{id}")
    Type selectById(Integer id); // 根据 ID 查询 Type 记录

    List<Type> selectAll(Type type); // 查询所有 Type 记录

}
