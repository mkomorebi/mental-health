package com.example.controller;

import com.example.common.Result;
import com.example.entity.Department;
import com.example.service.DepartService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        PageInfo<Department> departments = departService.getDepartments(department.getName());
        if (departments.getTotal() > 0) {
            return Result.error("此部门已存在");
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
     * 获取部门列表
     */
    @GetMapping("/list")
    public Result getDepartments(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(departService.getDepartments(name, pageNum, pageSize));
    }

    /**
     * 查询所有部门
     */
    @GetMapping("/selectAll")
    public Result getDepartments() {
        return Result.success(departService.getDepartments());
    }
}
