package com.example.controller;

import com.example.common.Result;
import com.example.entity.Type;
import com.example.service.TypeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 类型控制器，处理与心理分类相关的请求。
 */
@RestController
@RequestMapping("/type")
public class TypeController {

    private static final Logger logger = Logger.getLogger(TypeController.class.getName());

    @Resource
    private TypeService typeService;
    
    @Resource
    private HttpServletRequest request;
    
    /**
     * 获取当前登录用户的公司ID
     */
    private Integer getCompanyId(HttpServletRequest request) {
        // 优先从请求属性中获取公司ID（由拦截器设置）
        Integer companyId = (Integer) request.getAttribute("companyId");
        if (companyId != null) {
            return companyId;
        }
        
        // 如果请求属性中没有，尝试从会话中获取
        Object userObj = request.getSession().getAttribute("user");
        if (userObj == null) {
            return null;
        }
        
        // 安全地进行类型转换
        try {
            if (userObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> user = (Map<String, Object>) userObj;
                Object companyIdObj = user.get("companyId");
                if (companyIdObj instanceof Integer) {
                    return (Integer) companyIdObj;
                } else if (companyIdObj != null) {
                    // 尝试将其他类型转换为Integer
                    try {
                        return Integer.valueOf(companyIdObj.toString());
                    } catch (NumberFormatException e) {
                        logger.warning("无法将companyId转换为Integer: " + companyIdObj);
                    }
                }
            }
        } catch (ClassCastException e) {
            logger.warning("类型转换异常: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * 新增心理分类
     */
    @PostMapping("/add")
    public Result add(@RequestBody Type type, HttpServletRequest request) {
        // 设置公司ID
        type.setCompanyId(getCompanyId(request));
        typeService.add(type);
        return Result.success();
    }

    /**
     * 修改心理分类
     */
    @PutMapping("/update")
    public Result update(@RequestBody Type type, HttpServletRequest request) {
        // 设置公司ID
        type.setCompanyId(getCompanyId(request));
        typeService.updateById(type);
        return Result.success();
    }

    /**
     * 删除单个心理分类
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id, HttpServletRequest request) {
        typeService.deleteById(id, getCompanyId(request));
        return Result.success();
    }

    /**
     * 批量删除心理分类
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids, HttpServletRequest request) {
        typeService.deleteBatch(ids, getCompanyId(request));
        return Result.success();
    }

    /**
     * 查询单个心理分类
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id, HttpServletRequest request) {
        Type type = typeService.selectById(id, getCompanyId(request));
        return Result.success(type);
    }

    /**
     * 查询所有心理分类
     */
    @GetMapping("/selectAll")
    public Result selectAll(Type type, HttpServletRequest request) {
        // 设置公司ID
        type.setCompanyId(getCompanyId(request));
        List<Type> list = typeService.selectAll(type);
        return Result.success(list);
    }

    /**
     * 分页查询心理分类
     */
    @GetMapping("/selectPage")
    public Result selectPage(Type type,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             HttpServletRequest request) {
        // 设置公司ID
        type.setCompanyId(getCompanyId(request));
        PageInfo<Type> pageInfo = typeService.selectPage(type, pageNum, pageSize);
        return Result.success(pageInfo);
    }
}
