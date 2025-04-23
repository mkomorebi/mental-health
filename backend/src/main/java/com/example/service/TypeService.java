package com.example.service;


import com.example.entity.Type;
import com.example.mapper.TypeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TypeService 业务层，用于处理心理分类相关的业务逻辑。
 * 提供对 Type 的增、删、改、查等操作。
 */
@Service
public class TypeService {

    @Resource
    private TypeMapper typeMapper;

    /**
     * 添加新的 Type 记录。
     *
     * @param type Type 实体
     */
    public void add(Type type) {
        typeMapper.insert(type);
    }

    public void updateById(Type type) {
        typeMapper.updateById(type);
    }

    public void deleteById(Integer id, Integer companyId) {
        typeMapper.deleteById(id, companyId);
    }

    public void deleteBatch(List<Integer> ids, Integer companyId) {
        for (Integer id : ids) {
            typeMapper.deleteById(id, companyId);
        }
    }

    public Type selectById(Integer id, Integer companyId) {
        return typeMapper.selectById(id, companyId);
    }

    public List<Type> selectAll(Type type) {
        return typeMapper.selectAll(type);
    }

    public PageInfo<Type> selectPage(Type type, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Type> list = typeMapper.selectAll(type);
        return PageInfo.of(list);
    }

}
