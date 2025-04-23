package com.example.mapper;

import com.example.entity.Notice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * NoticeMapper 接口，用于操作 Notice 实体的数据库映射。
 * 提供对 Notice 表的基本 CRUD 操作。
 */
public interface NoticeMapper {

    int insert(Notice notice); // 插入新的 Notice 记录

    void updateById(Notice notice); // 根据 ID 更新 Notice 记录

    void deleteById(Integer id); // 根据 ID 删除 Notice 记录

    Notice selectById(Integer id); // 根据 ID 查询 Notice 记录

    List<Notice> selectAll(Notice notice); // 查询所有 Notice 记录
    
    // 添加按公司ID统计公告数量的方法
    Integer countByCompanyId(@Param("companyId") Integer companyId);
}
