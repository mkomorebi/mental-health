package com.example.mapper;

import com.example.entity.Admin;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AdminMapper 接口，用于操作 Admin 实体的数据库映射。
 * 提供对 Admin 表的基本 CRUD 操作。
 */
public interface AdminMapper {

    int insert(Admin admin); // 插入新的 Admin 记录

    void updateById(Admin admin); // 根据 ID 更新 Admin 记录

    void deleteById(Integer id); // 根据 ID 删除 Admin 记录

    @Select("select * from `admin` where id = #{id}")
    Admin selectById(Integer id); // 根据 ID 查询 Admin 记录

    @Select("select * from `admin` where username = #{username}")
    Admin selectByUsername(String username); // 根据用户名查询 Admin 记录

    List<Admin> selectAll(Admin admin); // 查询所有 Admin 记录

}
