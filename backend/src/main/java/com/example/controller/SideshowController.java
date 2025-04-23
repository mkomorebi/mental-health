package com.example.controller;

import com.example.common.Result;
import com.example.entity.Sideshow;
import com.example.service.SideshowService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图控制器，处理与轮播图信息相关的请求。
 */
@RestController
@RequestMapping("/sideshow")
public class SideshowController {

    @Resource
    private SideshowService sideshowService;

    /**
     * 新增轮播图信息
     */
    @PostMapping("/add")
    public Result add(@RequestBody Sideshow sideshow, HttpServletRequest request) {
        try {
            // 获取当前用户的公司ID
            Integer companyId = (Integer) request.getAttribute("companyId");
            if (companyId == null) {
                return Result.error("未找到公司信息，请确保您已正确登录");
            }
            
            // 验证propagateId是否属于当前公司
            if (sideshow.getPropagateId() != null) {
                boolean isValid = sideshowService.validatePropagateCompany(sideshow.getPropagateId(), companyId);
                if (!isValid) {
                    return Result.error("所选宣传内容不属于您的公司");
                }
            }
            
            sideshowService.add(sideshow);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 修改轮播图信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Sideshow sideshow, HttpServletRequest request) {
        try {
            // 获取当前用户的公司ID
            Integer companyId = (Integer) request.getAttribute("companyId");
            if (companyId == null) {
                return Result.error("未找到公司信息，请确保您已正确登录");
            }
            
            // 验证轮播图是否属于当前公司
            Sideshow existing = sideshowService.selectById(sideshow.getId());
            if (existing == null) {
                return Result.error("未找到要修改的轮播图");
            }
            
            // 验证propagateId是否属于当前公司
            if (sideshow.getPropagateId() != null) {
                boolean isValid = sideshowService.validatePropagateCompany(sideshow.getPropagateId(), companyId);
                if (!isValid) {
                    return Result.error("所选宣传内容不属于您的公司");
                }
            }
            
            sideshowService.updateById(sideshow);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 删除单个轮播图信息
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        sideshowService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除轮播图信息
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        sideshowService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询单个轮播图信息
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Sideshow sideshow = sideshowService.selectById(id);
        return Result.success(sideshow);
    }

    /**
     * 查询所有轮播图信息
     */
    @GetMapping("/selectAll")
    public Result selectAll(Sideshow sideshow, HttpServletRequest request) {
        try {
            // 获取当前用户的公司ID
            Integer companyId = (Integer) request.getAttribute("companyId");
            if (companyId == null) {
                return Result.error("未找到公司信息，请确保您已正确登录");
            }
            
            // 同样的问题
            sideshow.setCompanyId(companyId);
            
            List<Sideshow> list = sideshowService.selectAll(sideshow);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询轮播图信息
     */
    @GetMapping("/selectPage")
    public Result selectPage(Sideshow sideshow,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             HttpServletRequest request) {
        try {
            // 获取当前用户的公司ID
            Integer companyId = (Integer) request.getAttribute("companyId");
            if (companyId == null) {
                return Result.error("未找到公司信息，请确保您已正确登录");
            }
            
            // 设置公司ID用于查询
            sideshow.setCompanyId(companyId);
            
            // 添加调试日志
            System.out.println("查询轮播图，公司ID: " + companyId + ", 查询条件: " + sideshow.getPropagateTitle());
            
            PageInfo<Sideshow> pageInfo = sideshowService.selectPage(sideshow, pageNum, pageSize);
            System.out.println("查询结果: " + pageInfo.getList().size() + " 条记录");
            
            return Result.success(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询轮播图失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据公司ID查询轮播图
     */
    @GetMapping("/selectByCompanyId")
    public Result selectByCompanyId(HttpServletRequest request) {
        try {
            Integer companyId = (Integer) request.getAttribute("companyId");
            if (companyId == null) {
                System.out.println("Warning: No company ID found in request attributes");
                return Result.error("未找到公司信息，请确保您已正确登录并分配到部门");
            }
            List<Sideshow> list = sideshowService.selectByCompanyId(companyId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取轮播图失败: " + e.getMessage());
        }
    }
}
