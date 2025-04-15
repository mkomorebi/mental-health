package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
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
import org.springframework.stereotype.Service;

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

    /**
     * 添加新的 Propagate 记录。
     *
     * @param propagate Propagate 实体
     */
    public void add(Propagate propagate) {
        Account currentUser = TokenUtils.getCurrentUser();
        propagate.setDoctorId(currentUser.getId());
        propagate.setTime(DateUtil.now());
        propagateMapper.insert(propagate);
    }

    public void updateById(Propagate propagate) {
        propagateMapper.updateById(propagate);
    }

    public void deleteById(Integer id) {
        propagateMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            propagateMapper.deleteById(id);
        }
    }

    public Propagate selectById(Integer id) {
        Propagate propagate = propagateMapper.selectById(id);
        Doctor doctor = doctorMapper.selectById(propagate.getDoctorId());
        if (ObjectUtil.isNotEmpty(doctor)) {
            propagate.setDoctorName(doctor.getName());
        }
        return propagate;
    }

    public List<Propagate> selectAll(Propagate propagate) {
        return propagateMapper.selectAll(propagate);
    }

    public List<String> selectAllTags() {return propagateMapper.selectAllTags();}

    public PageInfo<Propagate> selectPage(Propagate propagate, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
            propagate.setDoctorId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Propagate> list = propagateMapper.selectAll(propagate);
        return PageInfo.of(list);
    }

    public List<Propagate> selectTop3() {
        List<Propagate> propagates = propagateMapper.selectTop3();
        for (Propagate propagate : propagates) {
            propagate.setContent(HtmlUtil.cleanHtmlTag(propagate.getContent()));
            Doctor doctor = doctorMapper.selectById(propagate.getDoctorId());
            if (ObjectUtil.isNotEmpty(doctor)) {
                propagate.setDoctorName(doctor.getName());
                propagate.setDoctorAvatar(doctor.getAvatar());
            }
        }
        return propagates;
    }

    public PageInfo<Propagate> selectPageFront(Propagate propagate, Integer pageNum, Integer pageSize) {
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
            propagate.setNum(propagate.getNum() + 1);
            propagateMapper.updateById(propagate);
        }
    }
}
