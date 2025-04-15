package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserService 业务层，用于处理用户信息相关的业务逻辑。
 * 提供对 User 的增、删、改、查等操作。
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;
    
    // 添加AdminService依赖，用于获取当前管理员信息
    @Resource
    private AdminService adminService;

    /**
     * 添加新的 User 用户。
     *
     * @param user User 实体
     */
    public void add(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (ObjectUtil.isNotNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(user.getPassword())) {
            user.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(user.getName())) {
            user.setName(user.getUsername());
        }
        user.setRole(RoleEnum.USER.name());
        
        // 如果是管理员添加用户，设置与管理员相同的公司ID
        if (TokenUtils.getCurrentAdmin() != null) {
            user.setCompanyId(adminService.getAdminCompanyId());
        }
        
        userMapper.insert(user);
    }

    public void updateById(User user) {
        // 如果是管理员操作，确保只能修改同公司的用户
        if (TokenUtils.getCurrentAdmin() != null) {
            User dbUser = userMapper.selectById(user.getId());
            if (dbUser == null || !adminService.getAdminCompanyId().equals(dbUser.getCompanyId())) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权修改其他公司的用户信息");
            }
        }
        userMapper.updateById(user);
    }

    public void deleteById(Integer id) {
        // 如果是管理员操作，确保只能删除同公司的用户
        if (TokenUtils.getCurrentAdmin() != null) {
            User dbUser = userMapper.selectById(id);
            if (dbUser == null || !adminService.getAdminCompanyId().equals(dbUser.getCompanyId())) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权删除其他公司的用户");
            }
        }
        userMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            deleteById(id);
        }
    }

    public User selectById(Integer id) {
        User user = userMapper.selectById(id);
        // 如果是管理员查询，确保只能查看同公司的用户
        if (TokenUtils.getCurrentAdmin() != null && user != null) {
            if (!adminService.getAdminCompanyId().equals(user.getCompanyId())) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权查看其他公司的用户信息");
            }
        }
        return user;
    }

    public List<User> selectAll(User user) {
        // 如果是管理员查询，添加公司ID过滤条件
        if (TokenUtils.getCurrentAdmin() != null) {
            user.setCompanyId(adminService.getAdminCompanyId());
        }
        return userMapper.selectAll(user);
    }

    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        // 如果是管理员查询，添加公司ID过滤条件
        if (TokenUtils.getCurrentAdmin() != null) {
            user.setCompanyId(adminService.getAdminCompanyId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll(user);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public User login(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!dbUser.getPassword().equals(account.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String token = TokenUtils.createToken(dbUser.getId() + "-" + dbUser.getRole(), dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbUser.setPassword(account.getNewPassword());
        userMapper.updateById(dbUser);
    }

    public void register(Account account) {
        User user = new User();
        BeanUtils.copyProperties(account, user);
        add(user);
    }
}
