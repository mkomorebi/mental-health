package com.example.service;

import com.example.entity.Department;
import com.example.mapper.DepartMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DepartService 业务层，用于处理与 Department 实体相关的业务逻辑。
 * 提供对 Department 的增、删、改、查等操作。
 */
@Service
public class DepartService {

    @Resource
    private DepartMapper departMapper;
    
    @Resource
    private AdminService adminService;
    
    @Resource
    private HttpServletRequest request;

    /**
     * 添加新的 Department。
     *
     * @param department Department 实体
     * @return 插入的记录数
     */
    public int addDepartment(Department department) {
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            department.setCompanyId(companyId);
        } else {
            Integer adminCompanyId = adminService.getAdminCompanyId();
            if (adminCompanyId != null) {
                department.setCompanyId(adminCompanyId);
            }
        }
        return departMapper.insert(department);
    }

    public void updateDepartment(Department department) {
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            department.setCompanyId(companyId);
        } else {
            Integer adminCompanyId = adminService.getAdminCompanyId();
            if (adminCompanyId != null) {
                department.setCompanyId(adminCompanyId);
            }
        }
        departMapper.updateById(department);
    }

    public void deleteDepartment(Integer id) {
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId == null) {
            companyId = adminService.getAdminCompanyId();
        }
        departMapper.deleteById(id, companyId);
    }

    public PageInfo<Department> getDepartments(String name, int pageNum, int pageSize, Integer companyId) {
        if (companyId == null) {
            companyId = (Integer) request.getAttribute("companyId");
            
            if (companyId == null) {
                companyId = adminService.getAdminCompanyId();
            }
        }
        
        PageHelper.startPage(pageNum, pageSize);
        List<Department> departmentList = departMapper.list(name, companyId);
        return new PageInfo<>(departmentList);
    }

    public PageInfo<Department> getDepartments(String name) {
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId == null) {
            companyId = adminService.getAdminCompanyId();
        }
        
        List<Department> departmentList = departMapper.list(name, companyId);
        return new PageInfo<>(departmentList);
    }

    public PageInfo<Department> getDepartments() {
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId == null) {
            companyId = adminService.getAdminCompanyId();
        }
        
        List<Department> departmentList = departMapper.alllist(companyId);
        return new PageInfo<>(departmentList);
    }

    /**
     * 根据公司ID获取所有部门名称
     */
    public List<String> getDepartmentNamesByCompanyId(Integer companyId) {
        if (companyId == null) {
            return new ArrayList<>();
        }
        
        List<Department> departments = departMapper.alllist(companyId);
        return departments.stream()
            .map(Department::getName)
            .collect(Collectors.toList());
    }

    /**
     * 根据ID查询部门
     */
    public Department selectById(Integer id) {
        if (id == null) {
            return null;
        }
        return departMapper.selectById(id);
    }
}
