package com.example.mapper;

import com.example.entity.TestPaper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * TestPaperMapper 接口，用于操作 TestPaper 实体的数据库映射。
 * 提供对 TestPaper 表的基本 CRUD 操作。
 */
public interface TestPaperMapper {

    int insert(TestPaper testPaper); // 插入新的 TestPaper 记录

    void updateById(TestPaper testPaper); // 根据 ID 更新 TestPaper 记录

    void deleteById(Integer id); // 根据 ID 删除 TestPaper 记录

    @Select("select * from `test_paper` where id = #{id}")
    TestPaper selectById(Integer id); // 根据 ID 查询 TestPaper 记录

    List<TestPaper> selectAll(TestPaper testPaper); // 查询所有 TestPaper 记录

    @Select("select * from test_paper where status = '审核通过' order by test_num desc limit 8")
    List<TestPaper> selectAllDesc(); // 查询审核通过的 TestPaper 记录，按测试编号降序排列
}
