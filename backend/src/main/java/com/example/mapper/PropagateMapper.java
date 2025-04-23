package com.example.mapper;

import com.example.entity.Propagate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * PropagateMapper 接口，用于操作 Propagate 实体的数据库映射。
 * 提供对 Propagate 表的基本 CRUD 操作。
 */
public interface PropagateMapper {

    int insert(Propagate propagate); // 插入新的 Propagate 记录

    void updateById(Propagate propagate); // 根据 ID 更新 Propagate 记录

    void deleteById(Integer id); // 根据 ID 删除 Propagate 记录

    Propagate selectById(Integer id); // 根据 ID 查询 Propagate 记录

    List<String> selectAllTags(); // 查询所有标签

    List<Propagate> selectAll(Propagate propagate); // 查询所有 Propagate 记录

    /**
     * 查询浏览量最高的三个宣传帖子
     * @param companyId 公司ID，用于筛选特定公司的宣传
     * @return 浏览量最高的三个宣传帖子
     */
    List<Propagate> selectTop3(@Param("companyId") Integer companyId);
}
