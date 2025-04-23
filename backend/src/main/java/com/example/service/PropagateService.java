package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HtmlUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Doctor;
import com.example.entity.Propagate;
import com.example.mapper.DoctorMapper;
import com.example.mapper.PropagateMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * PropagateService 业务层，用于处理健康宣传信息相关的业务逻辑。
 * 提供对 Propagate 的增、删、改、查等操作。
 */
@Service
public class PropagateService {

    @Resource
    private PropagateMapper propagateMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DoctorService doctorService;
    @Resource
    private PropagateTagService propagateTagService;

    /**
     * 获取当前请求中的公司ID
     */
    private Integer getCompanyIdFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object companyId = request.getAttribute("companyId");
            if (companyId != null) {
                return (Integer) companyId;
            }
        }
        return null;
    }

    /**
     * 添加新的 Propagate 记录。
     *
     * @param propagate Propagate 实体
     */
    public void add(Propagate propagate) {
        Account currentUser = TokenUtils.getCurrentUser();
        propagate.setDoctorId(currentUser.getId());
        propagate.setTime(DateUtil.now());
        
        // 设置初始状态
        if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
            propagate.setStatus(Propagate.STATUS_PENDING); // 医生发布需要审核
        } else if (RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            propagate.setStatus(Propagate.STATUS_APPROVED); // 管理员发布直接通过
        }
        
        // 初始化浏览量
        if (propagate.getNum() == null) {
            propagate.setNum(0);
        }
        
        // 确保img字段是字符串
        if (propagate.getImg() != null && !(propagate.getImg() instanceof String)) {
            propagate.setImg(propagate.getImg().toString());
        }
        
        // 设置公司ID
        Integer companyId = getCompanyIdFromRequest();
        if (companyId != null) {
            propagate.setCompanyId(companyId);
        } else if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
            // 如果是医生，从医生表获取公司ID
            Doctor doctor = doctorMapper.selectById(currentUser.getId());
            if (doctor != null && doctor.getCompanyId() != null) {
                propagate.setCompanyId(doctor.getCompanyId());
            }
        }
        
        // 使用tagId而不是tag字段
        // 不再需要处理tag字段，直接使用前端传来的tagId
        
        propagateMapper.insert(propagate);
    }

    public void updateById(Propagate propagate) {
        Account currentUser = TokenUtils.getCurrentUser();
        
        // 如果是医生修改，需要重新审核
        if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
            propagate.setStatus(Propagate.STATUS_PENDING);
        }
        
        // 确保img字段是字符串
        if (propagate.getImg() != null && !(propagate.getImg() instanceof String)) {
            propagate.setImg(propagate.getImg().toString());
        }
        
        // 验证操作权限
        Integer companyId = getCompanyIdFromRequest();
        if (companyId != null) {
            // 检查是否有权限修改该宣传
            if (!belongsToCompany(propagate.getId(), companyId)) {
                throw new RuntimeException("无权修改其他公司的宣传信息");
            }
            // 保持公司ID不变
            Propagate original = propagateMapper.selectById(propagate.getId());
            propagate.setCompanyId(original.getCompanyId());
        }
        
        // 使用tagId而不是tag字段
        // 不再需要处理tag字段，直接使用前端传来的tagId
        
        propagateMapper.updateById(propagate);
    }

    public void deleteById(Integer id) {
        // 验证操作权限
        Integer companyId = getCompanyIdFromRequest();
        if (companyId != null) {
            // 检查是否有权限删除该宣传
            if (!belongsToCompany(id, companyId)) {
                throw new RuntimeException("无权删除其他公司的宣传信息");
            }
        }
        
        propagateMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        Integer companyId = getCompanyIdFromRequest();
        
        for (Integer id : ids) {
            // 验证操作权限
            if (companyId != null) {
                // 检查是否有权限删除该宣传
                if (!belongsToCompany(id, companyId)) {
                    throw new RuntimeException("无权删除其他公司的宣传信息");
                }
            }
            propagateMapper.deleteById(id);
        }
    }

    public Propagate selectById(Integer id) {
        Propagate propagate = propagateMapper.selectById(id);
        
        // 如果宣传不存在，直接返回null
        if (propagate == null) {
            return null;
        }
        
        // 验证查看权限
        Integer companyId = getCompanyIdFromRequest();
        if (companyId != null) {
            // 检查是否有权限查看该宣传
            if (!belongsToCompany(id, companyId)) {
                throw new RuntimeException("无权查看其他公司的宣传信息");
            }
        }
        
        // 填充医生信息
        if (propagate.getDoctorId() != null) {
            Doctor doctor = doctorMapper.selectById(propagate.getDoctorId());
            if (doctor != null) {
                propagate.setDoctorName(doctor.getName());
                propagate.setDoctorAvatar(doctor.getAvatar());
            }
        }
        
        return propagate;
    }

    public List<Propagate> selectAll(Propagate propagate) {
        // 添加公司ID过滤
        Integer companyId = getCompanyIdFromRequest();
        if (companyId != null) {
            propagate.setCompanyId(companyId);
        }
        
        return propagateMapper.selectAll(propagate);
    }

    public List<String> selectAllTags() {
        return propagateMapper.selectAllTags();
    }

    public PageInfo<Propagate> selectPage(Propagate propagate, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
            propagate.setDoctorId(currentUser.getId());
        }
        
        // 添加公司ID过滤
        Integer companyId = getCompanyIdFromRequest();
        if (companyId != null) {
            propagate.setCompanyId(companyId);
        }
        
        PageHelper.startPage(pageNum, pageSize);
        List<Propagate> list = propagateMapper.selectAll(propagate);
        return PageInfo.of(list);
    }

    /**
     * 查询浏览量最高的三个宣传帖子
     * @param companyId 公司ID，可以为null
     * @return 浏览量最高的三个宣传帖子
     */
    public List<Propagate> selectTop3(Integer companyId) {
        // 获取宣传列表
        List<Propagate> propagates = propagateMapper.selectTop3(companyId);
        
        // 填充医生信息
        for (Propagate propagate : propagates) {
            if (propagate != null) {
                // 清理HTML标签
                propagate.setContent(HtmlUtil.cleanHtmlTag(propagate.getContent()));
                
                // 填充医生信息
                if (propagate.getDoctorId() != null) {
                    Doctor doctor = doctorMapper.selectById(propagate.getDoctorId());
                    if (doctor != null) {
                        propagate.setDoctorName(doctor.getName());
                        propagate.setDoctorAvatar(doctor.getAvatar());
                    }
                }
            }
        }
        
        return propagates;
    }

    public PageInfo<Propagate> selectPageFront(Propagate propagate, Integer pageNum, Integer pageSize) {
        // 添加状态过滤
        propagate.setStatus(Propagate.STATUS_APPROVED);
        
        // 添加公司ID过滤
        Integer companyId = getCompanyIdFromRequest();
        if (companyId != null) {
            propagate.setCompanyId(companyId);
        }
        
        PageHelper.startPage(pageNum, pageSize);
        
        // 如果没有指定排序方式，默认按时间降序排列
        if (propagate.getOrderBy() == null) {
            propagate.setOrderBy("time");
            propagate.setOrderType("desc");
        }
        
        List<Propagate> list = propagateMapper.selectAll(propagate);
        for (Propagate dbPropagate : list) {
            dbPropagate.setContent(HtmlUtil.cleanHtmlTag(dbPropagate.getContent()));
        }
        return PageInfo.of(list);
    }

    /**
     * 增加文章浏览量
     */
    public void incrementViews(Integer id) {
        Propagate propagate = propagateMapper.selectById(id);
        if (propagate != null) {
            // 验证查看权限
            Integer companyId = getCompanyIdFromRequest();
            if (companyId != null) {
                // 检查是否有权限查看该宣传
                if (!belongsToCompany(id, companyId)) {
                    throw new RuntimeException("无权查看其他公司的宣传信息");
                }
            }
            
            propagate.setNum(propagate.getNum() + 1);
            propagateMapper.updateById(propagate);
        }
    }

    /**
     * 检查宣传是否属于指定公司
     * 
     * @param propagateId 宣传ID
     * @param companyId 公司ID
     * @return 如果宣传属于指定公司，则返回true；否则返回false
     */
    public boolean belongsToCompany(Integer propagateId, Integer companyId) {
        if (propagateId == null || companyId == null) {
            return false;
        }
        
        // 获取宣传
        Propagate propagate = propagateMapper.selectById(propagateId);
        if (propagate == null) {
            return false;
        }
        
        // 如果宣传有公司ID字段，直接比较
        if (propagate.getCompanyId() != null) {
            return propagate.getCompanyId().equals(companyId);
        }
        
        // 如果宣传没有公司ID字段，通过医生ID关联查询
        if (propagate.getDoctorId() != null) {
            // 检查医生是否属于该公司
            Doctor doctor = doctorMapper.selectById(propagate.getDoctorId());
            return doctor != null && doctor.getCompanyId() != null && doctor.getCompanyId().equals(companyId);
        }
        
        return false;
    }

    /**
     * 根据公司ID查询宣传数据
     * @param companyId 公司ID
     * @return 宣传数据列表
     */
    public List<Propagate> selectByCompanyId(Integer companyId) {
        System.out.println("PropagateService.selectByCompanyId - 查询公司ID: " + companyId);
        
        // 创建查询条件对象
        Propagate propagate = new Propagate();
        propagate.setCompanyId(companyId);
        
        // 使用现有的selectAll方法，传入带有公司ID的查询条件
        List<Propagate> list = propagateMapper.selectAll(propagate);
        
        System.out.println("查询结果数量: " + list.size());
        return list;
    }

    // 添加审核方法
    public void updateStatus(Integer id, String status, String rejectReason) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (!RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            throw new RuntimeException("只有管理员可以审核宣传资料");
        }
        
        Propagate propagate = propagateMapper.selectById(id);
        if (propagate == null) {
            throw new RuntimeException("宣传资料不存在");
        }
        
        propagate.setStatus(status);
        propagateMapper.updateById(propagate);
    }
}
