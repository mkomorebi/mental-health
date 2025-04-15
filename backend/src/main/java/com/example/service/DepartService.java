package com.example.service;

import com.example.entity.Department;
import com.example.mapper.DepartMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DepartService 业务层，用于处理与 Department 实体相关的业务逻辑。
 * 提供对 Department 的增、删、改、查等操作。
 */
@Service
public class DepartService {

    @Resource
    private DepartMapper departMapper;

    /**
     * 添加新的 Department。
     *
     * @param department Department 实体
     * @return 插入的记录数
     */
    public int addDepartment(Department department) {
        return departMapper.insert(department);
    }

    public void updateDepartment(Department department) {
        departMapper.updateById(department);
    }

    public void deleteDepartment(Integer id) {
        departMapper.deleteById(id);
    }

    public PageInfo<Department> getDepartments(String name, int pageNum, int pageSize) {
        // 开启分页（PageHelper 作用于下一条 SQL 语句）
        PageHelper.startPage(pageNum, pageSize);
        List<Department> departmentList = departMapper.list(name);
        // 返回分页后的数据
        return new PageInfo<>(departmentList);
    }

    public PageInfo<Department> getDepartments(String name) {
        List<Department> departmentList = departMapper.list(name);
        // 返回分页后的数据
        return new PageInfo<>(departmentList);
    }

    public PageInfo<Department> getDepartments() {
        List<Department> departmentList = departMapper.alllist();
        // 返回分页后的数据
        return new PageInfo<>(departmentList);
    }
}
