package com.example.mapper;

import com.example.entity.Sideshow;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * SideshowMapper 接口，用于操作 Sideshow 实体的数据库映射。
 * 提供对 Sideshow 表的基本 CRUD 操作。
 */
public interface SideshowMapper {

    int insert(Sideshow sideshow); // 插入新的 Sideshow 记录

    void updateById(Sideshow sideshow); // 根据 ID 更新 Sideshow 记录

    void deleteById(Integer id); // 根据 ID 删除 Sideshow 记录

    @Select("select * from `sideshow` where id = #{id}")
    Sideshow selectById(Integer id); // 根据 ID 查询 Sideshow 记录

    List<Sideshow> selectAll(Sideshow sideshow); // 查询所有 Sideshow 记录
}
