package com.example.mapper;

import com.example.entity.Reservation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ReservationMapper 接口，用于操作 Reservation 实体的数据库映射。
 * 提供对 Reservation 表的基本 CRUD 操作。
 */
public interface ReservationMapper {

    int insert(Reservation reservation); // 插入新的 Reservation 记录

    void updateById(Reservation reservation); // 根据 ID 更新 Reservation 记录

    void deleteById(Integer id); // 根据 ID 删除 Reservation 记录

    @Select("select * from `reservation` where id = #{id}")
    Reservation selectById(Integer id); // 根据 ID 查询 Reservation 记录

    List<Reservation> selectAll(Reservation reservation); // 查询所有 Reservation 记录

    @Select("select * from reservation where user_id = #{userId} and doctor_id = #{doctorId}")
    List<Reservation> selectByUserIdAndDoctorId(@Param("userId") Integer userId, @Param("doctorId") Integer doctorId); // 根据用户 ID 和医生 ID 查询 Reservation 记录

    @Select("select * from reservation where doctor_id = #{doctorId} and (status = '审核通过' or status = '已结束')")
    List<Reservation> selectByDoctorIdAndPass(Integer doctorId); // 根据医生 ID 查询审核通过或已结束的 Reservation 记录
}
