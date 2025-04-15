package com.example.mapper;

import com.example.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DepartMapper 接口，用于操作 Department 实体的数据库映射。
 * 提供对 Department 表的基本 CRUD 操作。
 */
public interface DepartMapper {

    int insert(Department department); // 插入新的 Department 记录

    void updateById(Department department); // 根据 ID 更新 Department 记录

    void deleteById(Integer id); // 根据 ID 删除 Department 记录

    List<Department> list(@Param("name") String name); // 根据名称查询 Department 记录

    List<Department> alllist(); // 查询所有 Department 记录
}
