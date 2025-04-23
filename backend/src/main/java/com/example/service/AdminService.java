package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * AdminService 业务层，用于处理与 Admin 实体相关的业务逻辑。
 * 提供对 Admin 的增、删、改、查等操作。
 */
@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    /**
     * 添加新的 Admin 用户。
     *
     * @param admin Admin 实体
     */
    public void add(Admin admin) {
        Admin dbAdmin = adminMapper.selectByUsername(admin.getUsername());
        if (ObjectUtil.isNotNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(admin.getPassword())) {
            admin.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(admin.getName())) {
            admin.setName(admin.getUsername());
        }
        admin.setRole(RoleEnum.ADMIN.name());
        adminMapper.insert(admin);
    }

    public void updateById(Admin admin) {
        adminMapper.updateById(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            adminMapper.deleteById(id);
        }
    }

    public Admin selectById(Integer id) {
        return adminMapper.selectById(id);
    }

    public List<Admin> selectAll(Admin admin) {
        return adminMapper.selectAll(admin);
    }

    public PageInfo<Admin> selectPage(Admin admin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = adminMapper.selectAll(admin);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Admin login(Account account) {
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!dbAdmin.getPassword().equals(account.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String token = TokenUtils.createToken(dbAdmin.getId() + "-" + dbAdmin.getRole(), dbAdmin.getPassword());
        dbAdmin.setToken(token);
        return dbAdmin;
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbAdmin.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbAdmin.setPassword(account.getNewPassword());
        adminMapper.updateById(dbAdmin);
    }

    /**
     * 获取当前登录管理员的公司ID
     */
    public Integer getAdminCompanyId() {
        try {
            // 从 token 中获取当前用户 ID
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            
            String token = attributes.getRequest().getHeader("token");
            if (token == null || token.isEmpty()) {
                return null;
            }
            
            String audience = TokenUtils.decodeToken(token).getAudience().get(0);
            if (audience == null || !audience.contains("-")) {
                return null;
            }
            
            String[] parts = audience.split("-");
            if (parts.length < 2 || !"ADMIN".equals(parts[1])) {
                return null; // 不是管理员
            }
            
            Integer adminId = Integer.valueOf(parts[0]);
            Admin admin = adminMapper.selectById(adminId);
            if (admin != null) {
                System.out.println("AdminService: 获取到管理员公司ID = " + admin.getCompanyId() + " 给管理员ID = " + adminId);
                return admin.getCompanyId();
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("获取管理员公司ID失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取管理员的公司ID
     */
    public Integer getAdminCompanyId(Integer adminId) {
        if (adminId == null) {
            return null;
        }
        Admin admin = adminMapper.selectById(adminId);
        return admin != null ? admin.getCompanyId() : null;
    }

}
