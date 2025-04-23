package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Employee;
import com.example.exception.CustomException;
import com.example.mapper.DepartMapper;
import com.example.mapper.EmployeeMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * EmployeeService 业务层，用于处理员工信息相关的业务逻辑。
 * 提供对 Employee 的增、删、改、查等操作。
 */
@Service
public class EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;
    
    @Resource
    private DepartMapper departMapper;
    
    @Resource
    private AdminService adminService;

    @Resource
    private DepartService departService;

    /**
     * 添加新的员工。
     *
     * @param employee Employee 实体
     */
    public void add(Employee employee) {
        Employee dbEmployee = employeeMapper.selectByUsername(employee.getUsername());
        if (ObjectUtil.isNotNull(dbEmployee)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(employee.getPassword())) {
            employee.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(employee.getName())) {
            employee.setName(employee.getUsername());
        }
        employee.setRole(RoleEnum.USER.name());
        
        // 如果是管理员添加员工，需要验证部门是否属于管理员的公司
        if (TokenUtils.getCurrentAdmin() != null) {
            Integer adminCompanyId = adminService.getAdminCompanyId();
            Integer departmentCompanyId = departMapper.getCompanyIdByDepartmentId(employee.getDepartmentId());
            
            if (!adminCompanyId.equals(departmentCompanyId)) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权为其他公司的部门添加员工");
            }
        }

        employeeMapper.insert(employee);
    }

    public void updateById(Employee employee) {
        // 如果是管理员操作，确保只能修改同公司的员工
        if (TokenUtils.getCurrentAdmin() != null) {
            Integer adminCompanyId = adminService.getAdminCompanyId();
            Employee dbEmployee = employeeMapper.selectById(employee.getId());
            
            if (dbEmployee == null) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "员工不存在");
            }
            
            // 检查员工是否属于管理员的公司
            Integer departmentCompanyId = departMapper.getCompanyIdByDepartmentId(dbEmployee.getDepartmentId());
            if (!adminCompanyId.equals(departmentCompanyId)) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权修改其他公司的员工信息");
            }
            
            // 如果要修改部门，确保新部门也属于同一公司
            if (employee.getDepartmentId() != null && !employee.getDepartmentId().equals(dbEmployee.getDepartmentId())) {
                Integer newDepartmentCompanyId = departMapper.getCompanyIdByDepartmentId(employee.getDepartmentId());
                if (!adminCompanyId.equals(newDepartmentCompanyId)) {
                    throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无法将员工转移到其他公司的部门");
                }
            }
        }
        
        employeeMapper.updateById(employee);
    }

    public void deleteById(Integer id) {
        // 如果是管理员操作，确保只能删除同公司的员工
        if (TokenUtils.getCurrentAdmin() != null) {
            Integer adminCompanyId = adminService.getAdminCompanyId();
            Employee dbEmployee = employeeMapper.selectById(id);
            
            if (dbEmployee == null) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "员工不存在");
            }
            
            // 检查员工是否属于管理员的公司
            Integer departmentCompanyId = departMapper.getCompanyIdByDepartmentId(dbEmployee.getDepartmentId());
            if (!adminCompanyId.equals(departmentCompanyId)) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权删除其他公司的员工");
            }
        }
        
        employeeMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        // 如果是管理员操作，需要验证所有ID都属于管理员的公司
        if (TokenUtils.getCurrentAdmin() != null) {
            Integer adminCompanyId = adminService.getAdminCompanyId();
            
            for (Integer id : ids) {
                Employee dbEmployee = employeeMapper.selectById(id);
                if (dbEmployee == null) {
                    continue; // 跳过不存在的员工
                }
                
                // 检查员工是否属于管理员的公司
                Integer departmentCompanyId = departMapper.getCompanyIdByDepartmentId(dbEmployee.getDepartmentId());
                if (!adminCompanyId.equals(departmentCompanyId)) {
                    throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权删除其他公司的员工");
                }
            }
        }
        
        for (Integer id : ids) {
            employeeMapper.deleteById(id);
        }
    }

    public Employee selectById(Integer id) {
        Employee employee = employeeMapper.selectById(id);
        
        // 如果是管理员查询，确保只能查看同公司的员工
        if (TokenUtils.getCurrentAdmin() != null && employee != null) {
            Integer adminCompanyId = adminService.getAdminCompanyId();
            Integer departmentCompanyId = departMapper.getCompanyIdByDepartmentId(employee.getDepartmentId());
            
            if (!adminCompanyId.equals(departmentCompanyId)) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权查看其他公司的员工信息");
            }
        }
        
        return employee;
    }

    public List<Employee> selectAll(Employee employee) {
        // 如果是管理员查询，添加公司过滤条件
        if (TokenUtils.getCurrentAdmin() != null) {
            Integer adminCompanyId = adminService.getAdminCompanyId();
            employee.setCompanyId(adminCompanyId);
        }
        
        return employeeMapper.selectAll(employee);
    }

    public PageInfo<Employee> selectPage(Employee employee, Integer pageNum, Integer pageSize) {
        // 如果是管理员查询，添加公司过滤条件
        if (TokenUtils.getCurrentAdmin() != null) {
            Integer adminCompanyId = adminService.getAdminCompanyId();
            employee.setCompanyId(adminCompanyId);
        }
        
        PageHelper.startPage(pageNum, pageSize);
        List<Employee> list = employeeMapper.selectAll(employee);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Employee login(Account account) {
        Employee dbEmployee = employeeMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbEmployee)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!dbEmployee.getPassword().equals(account.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String token = TokenUtils.createToken(dbEmployee.getId() + "-" + dbEmployee.getRole(), dbEmployee.getPassword());
        dbEmployee.setToken(token);
        return dbEmployee;
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Employee dbEmployee = employeeMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbEmployee)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbEmployee.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        
        // 如果是管理员操作，确保只能修改同公司的员工密码
        if (TokenUtils.getCurrentAdmin() != null) {
            Integer adminCompanyId = adminService.getAdminCompanyId();
            Integer departmentCompanyId = departMapper.getCompanyIdByDepartmentId(dbEmployee.getDepartmentId());
            
            if (!adminCompanyId.equals(departmentCompanyId)) {
                throw new CustomException(ResultCodeEnum.PARAM_ERROR, "无权修改其他公司员工的密码");
            }
        }
        
        dbEmployee.setPassword(account.getNewPassword());
        employeeMapper.updateById(dbEmployee);
    }

    public void register(Account account) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(account, employee);
        add(employee);
    }

    /**
     * 按公司ID统计员工数量
     */
    public int countByCompanyId(Integer companyId) {
        if (companyId == null) {
            return 0;
        }
        return employeeMapper.countByCompanyId(companyId);
    }

    /**
     * 按公司ID统计心理咨询师数量
     */
    public int countDoctorsByCompanyId(Integer companyId) {
        if (companyId == null) {
            return 0;
        }
        return employeeMapper.countDoctorsByCompanyId(companyId);
    }

    /**
     * 获取员工所属的公司ID
     */
    public Integer getEmployeeCompanyId(Integer employeeId) {
        // 直接使用 EmployeeMapper 中的方法，保持一致性
        return employeeMapper.getEmployeeCompanyId(employeeId);
    }

    /**
     * 根据姓名查询用户
     * @param name 用户姓名
     * @return 用户列表
     */
    public List<Employee> selectByName(String name) {
        if (name == null || name.isEmpty()) {
            return new ArrayList<>();
        }
        return employeeMapper.selectByName(name);
    }
}
