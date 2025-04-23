package com.example.service;

import com.example.entity.Sideshow;
import com.example.mapper.SideshowMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SideshowService 业务层，用于处理轮播图信息相关的业务逻辑。
 * 提供对 Sideshow 的增、删、改、查等操作。
 */
@Service
public class SideshowService {

    @Resource
    private SideshowMapper sideshowMapper;

    /**
     * 添加新的 Sideshow 记录。
     *
     * @param sideshow Sideshow 实体
     */
    public void add(Sideshow sideshow) {
        // 添加前验证
        if (sideshow.getPropagateId() == null) {
            throw new RuntimeException("宣传ID不能为空");
        }
        
        if (sideshow.getImg() == null || sideshow.getImg().trim().isEmpty()) {
            throw new RuntimeException("轮播图不能为空");
        }
        
        System.out.println("添加轮播图: propagateId=" + sideshow.getPropagateId() + ", img=" + sideshow.getImg());
        
        // 执行插入
        int result = sideshowMapper.insert(sideshow);
        
        if (result <= 0) {
            throw new RuntimeException("添加轮播图失败");
        }
        
        System.out.println("轮播图添加成功，ID: " + sideshow.getId());
    }

    public void updateById(Sideshow sideshow) {
        sideshowMapper.updateById(sideshow);
    }

    public void deleteById(Integer id) {
        sideshowMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            sideshowMapper.deleteById(id);
        }
    }

    public Sideshow selectById(Integer id) {
        return sideshowMapper.selectById(id);
    }

    public List<Sideshow> selectAll(Sideshow sideshow) {
        return sideshowMapper.selectAll(sideshow);
    }

    public PageInfo<Sideshow> selectPage(Sideshow sideshow, Integer pageNum, Integer pageSize) {
        System.out.println("SideshowService.selectPage - 查询参数: " + 
                          "companyId=" + sideshow.getCompanyId() + 
                          ", propagateTitle=" + sideshow.getPropagateTitle());
        
        PageHelper.startPage(pageNum, pageSize);
        List<Sideshow> list = sideshowMapper.selectAll(sideshow);
        
        System.out.println("查询结果数量: " + list.size());
        if (list.isEmpty()) {
            System.out.println("未找到轮播图数据");
        } else {
            System.out.println("找到轮播图数据，第一条: " + list.get(0).getId() + ", 标题: " + list.get(0).getPropagateTitle());
        }
        
        return PageInfo.of(list);
    }
    
    /**
     * 根据公司ID查询轮播图
     * @param companyId 公司ID
     * @return 轮播图列表
     */
    public List<Sideshow> selectByCompanyId(Integer companyId) {
        return sideshowMapper.selectByCompanyId(companyId);
    }

    /**
     * 验证宣传ID是否属于指定公司
     * @param propagateId 宣传ID
     * @param companyId 公司ID
     * @return 是否有效
     */
    public boolean validatePropagateCompany(Integer propagateId, Integer companyId) {
        // 这里需要添加一个mapper方法来验证
        return sideshowMapper.checkPropagateCompany(propagateId, companyId);
    }
}
