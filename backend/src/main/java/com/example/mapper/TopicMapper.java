package com.example.mapper;

import com.example.entity.Topic;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * TopicMapper 接口，用于操作 Topic 实体的数据库映射。
 * 提供对 Topic 表的基本 CRUD 操作。
 */
public interface TopicMapper {

    int insert(Topic topic); // 插入新的 Topic 记录

    void updateById(Topic topic); // 根据 ID 更新 Topic 记录

    void deleteById(@Param("id") Integer id, @Param("companyId") Integer companyId); // 根据 ID 和公司ID删除 Topic 记录

    @Select("select * from `topic` where id = #{id} and (company_id = #{companyId} or #{companyId} is null)")
    Topic selectById(@Param("id") Integer id, @Param("companyId") Integer companyId); // 根据 ID 和公司ID查询 Topic 记录

    List<Topic> selectAll(Topic topic); // 查询所有 Topic 记录

}
