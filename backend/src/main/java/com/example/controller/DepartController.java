package com.example.controller;

import com.example.common.Result;
import com.example.entity.Department;
import com.example.service.DepartService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 部门控制器，处理与部门相关的请求。
 */
@RestController
@RequestMapping("/departments")
public class DepartController {

    @Autowired
    private DepartService departService;

    /**
     * 新增部门
     */
    @PostMapping("/add")
    public Result addDepartment(@RequestBody Department department) {
        // 检查部门名称是否已存在（在同一公司内）
        PageInfo<Department> departments = departService.getDepartments(department.getName());
        if (departments.getTotal() > 0) {
            // 检查是否有相同名称的部门
            boolean hasSameName = false;
            for (Department dept : departments.getList()) {
                if (dept.getName().equals(department.getName())) {
                    hasSameName = true;
                    break;
                }
            }
            if (hasSameName) {
                return Result.error("此部门已存在");
            }
        }
        departService.addDepartment(department);
        return Result.success("部门添加成功");
    }

    /**
     * 修改部门信息
     */
    @PutMapping("/update")
    public Result updateDepartment(@RequestBody Department department) {
        departService.updateDepartment(department);
        return Result.success("部门修改成功");
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteDepartment(@PathVariable Integer id) {
        departService.deleteDepartment(id);
        return Result.success("部门删除成功");
    }

    /**
     * 批量删除部门
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatchDepartment(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            departService.deleteDepartment(id);
        }
        return Result.success("批量删除成功");
    }

    /**
     * 获取部门列表
     */
    @GetMapping("/list")
    public Result getDepartments(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        
        // 从请求属性中获取公司ID
        Integer companyId = (Integer) request.getAttribute("companyId");
        System.out.println("DepartController: 从请求属性中获取公司ID = " + companyId);
        
        // 将公司ID传递给Service层
        PageInfo<Department> result = departService.getDepartments(name, pageNum, pageSize, companyId);
        System.out.println("DepartController: 查询到部门数量 = " + result.getList().size());
        
        return Result.success(result);
    }

    /**
     * 查询所有部门
     */
    @GetMapping("/selectAll")
    public Result getDepartments(HttpServletRequest request) {
        // 从请求属性中获取公司ID
        Integer companyId = (Integer) request.getAttribute("companyId");
        System.out.println("DepartController.selectAll: 从请求属性中获取公司ID = " + companyId);
        
        PageInfo<Department> result = departService.getDepartments();
        System.out.println("DepartController.selectAll: 查询到部门数量 = " + result.getList().size());
        
        return Result.success(result);
    }
}
