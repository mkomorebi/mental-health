package com.example.service;

import com.example.entity.Account;
import com.example.entity.Doctor;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * DoctorService 接口，定义与医生相关的业务逻辑操作。
 */
public interface DoctorService {

    /**
     * 添加医生
     */
    void add(Doctor doctor);

    /**
     * 根据ID删除医生
     */
    void deleteById(Integer id);

    /**
     * 批量删除医生
     */
    void deleteBatch(List<Integer> ids);

    /**
     * 更新医生信息
     */
    void updateById(Doctor doctor);

    /**
     * 提交医生认证信息
     */
    void submit(Doctor doctor);

    /**
     * 根据ID查询医生
     */
    Doctor selectById(Integer id);

    /**
     * 根据用户名查询医生
     */
    Doctor selectByUsername(String username);

    /**
     * 查询所有医生
     */
    List<Doctor> selectAll(Doctor doctor);

    /**
     * 分页查询医生
     */
    PageInfo<Doctor> selectPage(Doctor doctor, Integer pageNum, Integer pageSize);

    /**
     * 获取前4名医生
     */
    List<Doctor> top4();
    
    /**
     * 获取指定公司的前4名医生
     */
    List<Doctor> top4(Integer companyId);
    
    /**
     * 检查医生是否属于指定公司
     */
    boolean checkDoctorCompany(Integer doctorId, Integer companyId);
    
    /**
     * 医生登录
     */
    Doctor login(Account account);
    
    /**
     * 医生注册
     */
    void register(Account account);
    
    /**
     * 修改医生密码
     */
    void updatePassword(Account account);

    /**
     * 获取医生的公司ID
     */
    Integer getDoctorCompanyId(Integer doctorId);
}
