package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器，处理与管理员相关的请求。
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 新增管理员
     */
    @PostMapping("/add")
    public Result add(@RequestBody Admin admin) {
        // 超级管理员可以添加任何公司的管理员
        // 普通管理员只能添加自己公司的管理员
        Integer currentAdminCompanyId = adminService.getAdminCompanyId();
        if (currentAdminCompanyId != null && 
            admin.getCompanyId() != null && 
            !currentAdminCompanyId.equals(admin.getCompanyId())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权为其他公司添加管理员");
        }
        
        adminService.add(admin);
        return Result.success();
    }

    /**
     * 修改管理员信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Admin admin) {
        // 确保管理员只能修改自己公司的管理员
        Integer currentAdminCompanyId = adminService.getAdminCompanyId();
        if (currentAdminCompanyId != null) {
            Admin dbAdmin = adminService.selectById(admin.getId());
            if (dbAdmin != null && !currentAdminCompanyId.equals(dbAdmin.getCompanyId())) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权修改其他公司的管理员");
            }
        }
        
        adminService.updateById(admin);
        return Result.success();
    }

    /**
     * 删除单个管理员
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        // 确保管理员只能删除自己公司的管理员
        Integer currentAdminCompanyId = adminService.getAdminCompanyId();
        if (currentAdminCompanyId != null) {
            Admin dbAdmin = adminService.selectById(id);
            if (dbAdmin != null && !currentAdminCompanyId.equals(dbAdmin.getCompanyId())) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权删除其他公司的管理员");
            }
        }
        
        adminService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除管理员
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        adminService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询单个管理员
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Admin admin = adminService.selectById(id);
        return Result.success(admin);
    }

    /**
     * 查询所有管理员
     */
    @GetMapping("/selectAll")
    public Result selectAll(Admin admin) {
        // 如果是管理员查询，只查询自己公司的管理员
        Integer currentAdminCompanyId = adminService.getAdminCompanyId();
        if (currentAdminCompanyId != null) {
            admin.setCompanyId(currentAdminCompanyId);
        }
        
        List<Admin> list = adminService.selectAll(admin);
        return Result.success(list);
    }

    /**
     * 分页查询管理员
     */
    @GetMapping("/selectPage")
    public Result selectPage(Admin admin,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        // 如果是管理员查询，只查询自己公司的管理员
        Integer currentAdminCompanyId = adminService.getAdminCompanyId();
        if (currentAdminCompanyId != null) {
            admin.setCompanyId(currentAdminCompanyId);
        }
        
        PageInfo<Admin> pageInfo = adminService.selectPage(admin, pageNum, pageSize);
        return Result.success(pageInfo);
    }

}
