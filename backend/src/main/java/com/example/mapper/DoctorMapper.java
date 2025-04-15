package com.example.mapper;

import com.example.entity.Doctor;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * DoctorMapper 接口，用于操作 Doctor 实体的数据库映射。
 * 提供对 Doctor 表的基本 CRUD 操作。
 */
public interface DoctorMapper {

    int insert(Doctor doctor); // 插入新的 Doctor 记录

    void updateById(Doctor doctor); // 根据 ID 更新 Doctor 记录

    void deleteById(Integer id); // 根据 ID 删除 Doctor 记录

    @Select("select * from `doctor` where id = #{id}")
    Doctor selectById(Integer id); // 根据 ID 查询 Doctor 记录

    @Select("select * from `doctor` where username = #{username}")
    Doctor selectByUsername(String username); // 根据用户名查询 Doctor 记录

    List<Doctor> selectAll(Doctor doctor); // 查询所有 Doctor 记录

    @Select("select * from doctor where status = '审批通过'")
    List<Doctor> allPassDoctor(); // 查询所有审批通过的 Doctor 记录
}
