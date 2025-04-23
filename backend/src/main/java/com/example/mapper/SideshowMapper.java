package com.example.mapper;

import com.example.entity.Sideshow;

import java.util.List;

/**
 * SideshowMapper 接口，用于操作 Sideshow 实体的数据库映射。
 * 提供对 Sideshow 表的基本 CRUD 操作。
 */
public interface SideshowMapper {

    /**
     * 插入新的 Sideshow 记录
     * @param sideshow Sideshow 实体
     * @return 影响的行数
     */
    int insert(Sideshow sideshow);

    void updateById(Sideshow sideshow); // 根据 ID 更新 Sideshow 记录

    void deleteById(Integer id); // 根据 ID 删除 Sideshow 记录

    Sideshow selectById(Integer id); // 根据 ID 查询 Sideshow 记录

    List<Sideshow> selectAll(Sideshow sideshow); // 查询所有 Sideshow 记录
    
    // 根据公司ID查询轮播图
    List<Sideshow> selectByCompanyId(Integer companyId);

    /**
     * 检查宣传ID是否属于指定公司
     * @param propagateId 宣传ID
     * @param companyId 公司ID
     * @return 是否属于
     */
    
    boolean checkPropagateCompany(Integer propagateId, Integer companyId);
}
