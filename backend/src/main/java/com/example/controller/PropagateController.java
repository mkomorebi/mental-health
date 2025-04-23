package com.example.controller;

import com.example.common.Result;
import com.example.entity.Propagate;
import com.example.service.PropagateService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 健康宣传控制器，处理与健康宣传信息相关的请求。
 */
@RestController
@RequestMapping("/propagate")
public class PropagateController {

    @Resource
    private PropagateService propagateService;

    /**
     * 新增健康宣传信息
     */
    @PostMapping("/add")
    public Result add(@RequestBody Propagate propagate) {
        try {
            // 确保 img 字段是字符串
            if (propagate.getImg() != null && !(propagate.getImg() instanceof String)) {
                propagate.setImg(propagate.getImg().toString());
            }
            
            propagateService.add(propagate);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改健康宣传信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Propagate propagate) {
        try {
            // 确保 img 字段是字符串
            if (propagate.getImg() != null && !(propagate.getImg() instanceof String)) {
                propagate.setImg(propagate.getImg().toString());
            }
            
            propagateService.updateById(propagate);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除单个健康宣传信息
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            propagateService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除健康宣传信息
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        try {
            propagateService.deleteBatch(ids);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询单个健康宣传信息
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        try {
            // 添加日志以便调试
            System.out.println("查询宣传详情，ID: " + id);
            
            Propagate propagate = propagateService.selectById(id);
            if (propagate == null) {
                return Result.error("未找到对应的宣传信息");
            }
            return Result.success(propagate);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取宣传详情失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有健康宣传信息
     */
    @GetMapping("/selectAll")
    public Result selectAll(Propagate propagate) {
        try {
            List<Propagate> list = propagateService.selectAll(propagate);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询所有健康宣传标签
     */
    @GetMapping("/getAllTags")
    public Result getAllTags() {
        try {
            List<String> list = propagateService.selectAllTags();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询健康宣传信息
     */
    @GetMapping("/selectPage")
    public Result selectPage(Propagate propagate,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            PageInfo<Propagate> pageInfo = propagateService.selectPage(propagate, pageNum, pageSize);
            return Result.success(pageInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询浏览量最高的三个宣传帖子
     */
    @GetMapping("/selectTop3")
    public Result selectTop3(HttpServletRequest request) {
        try {
            Integer companyId = (Integer) request.getAttribute("companyId");
            if (companyId == null) {
                System.out.println("Warning: No company ID found in request attributes for propagate/selectTop3");
                return Result.error("未找到公司信息，请确保您已正确登录并分配到部门");
            }
            List<Propagate> list = propagateService.selectTop3(companyId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取宣传信息失败: " + e.getMessage());
        }
    }

    /**
     * 前台分页查询健康宣传信息
     */
    @GetMapping("/selectPageFront")
    public Result selectPageFront(Propagate propagate,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(required = false) String orderBy,
                             @RequestParam(required = false) String orderType) {
        try {
            // 设置排序参数
            if (orderBy != null && orderType != null) {
                propagate.setOrderBy(orderBy);
                propagate.setOrderType(orderType);
            }
            
            PageInfo<Propagate> pageInfo = propagateService.selectPageFront(propagate, pageNum, pageSize);
            return Result.success(pageInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 增加文章浏览量
     */
    @PutMapping("/incrementViews/{id}")
    public Result incrementViews(@PathVariable Integer id) {
        try {
            propagateService.incrementViews(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前公司的宣传数据
     */
    @GetMapping("/selectByCompany")
    public Result selectByCompany(HttpServletRequest request) {
        try {
            Integer companyId = (Integer) request.getAttribute("companyId");
            if (companyId == null) {
                return Result.error("未找到公司信息，请确保您已正确登录");
            }
            
            System.out.println("查询公司宣传数据，公司ID: " + companyId);
            
            List<Propagate> list = propagateService.selectByCompanyId(companyId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询宣传数据失败: " + e.getMessage());
        }
    }

    /**
     * 审核宣传资料
     */
    @PutMapping("/audit/{id}")
    public Result audit(@PathVariable Integer id, 
                       @RequestParam String status,
                       @RequestParam(required = false) String rejectReason) {
        try {
            propagateService.updateStatus(id, status, rejectReason);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
