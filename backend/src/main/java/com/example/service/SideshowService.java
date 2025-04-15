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
        sideshowMapper.insert(sideshow);
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
        PageHelper.startPage(pageNum, pageSize);
        List<Sideshow> list = sideshowMapper.selectAll(sideshow);
        return PageInfo.of(list);
    }

}
