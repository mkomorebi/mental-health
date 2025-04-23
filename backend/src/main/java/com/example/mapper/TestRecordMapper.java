package com.example.mapper;

import com.example.entity.TestRecord;
import com.example.entity.resp.MetricResp;
import com.example.entity.resp.DepartmentStatsResp;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TestRecordMapper 接口，用于操作 TestRecord 实体的数据库映射。
 * 提供对 TestRecord 表的基本 CRUD 操作。
 */
public interface TestRecordMapper {

    int insert(TestRecord testRecord); // 插入新的 TestRecord 记录

    void updateById(TestRecord testRecord); // 根据 ID 更新 TestRecord 记录

    void deleteById(Integer id); // 根据 ID 删除 TestRecord 记录

    @Select("select * from `test_record` where id = #{id}")
    TestRecord selectById(Integer id); // 根据 ID 查询 TestRecord 记录

    List<TestRecord> selectAll(TestRecord testRecord); // 查询所有 TestRecord 记录

    List<MetricResp> metric(@Param("companyId") Integer companyId); // 查询指标响应

    List<TestRecord> selectRecentWeek(TestRecord testRecord);

    List<TestRecord> selectDoctorRecords(
        @Param("query") TestRecord query, 
        @Param("minScore") Double minScore, 
        @Param("maxScore") Double maxScore, 
        @Param("doctorId") Integer doctorId);

    /**
     * 获取部门统计数据
     */
    List<DepartmentStatsResp> getDepartmentStats(
        @Param("companyId") Integer companyId,
        @Param("departmentName") String departmentName,
        @Param("paperTitle") String paperTitle,
        @Param("publishTime") String publishTime);
}
