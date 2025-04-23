package com.example.service;

import com.example.entity.Topic;
import com.example.mapper.TopicMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TopicService 业务层，用于处理题库信息相关的业务逻辑。
 * 提供对 Topic 的增、删、改、查等操作。
 */
@Service
public class TopicService {

    @Resource
    private TopicMapper topicMapper;

    /**
     * 添加新的 Topic 记录。
     *
     * @param topic Topic 实体
     */
    public void add(Topic topic) {
        topicMapper.insert(topic);
    }

    public void updateById(Topic topic) {
        topicMapper.updateById(topic);
    }

    public void deleteById(Integer id, Integer companyId) {
        topicMapper.deleteById(id, companyId);
    }

    public void deleteBatch(List<Integer> ids, Integer companyId) {
        for (Integer id : ids) {
            topicMapper.deleteById(id, companyId);
        }
    }

    public Topic selectById(Integer id, Integer companyId) {
        return topicMapper.selectById(id, companyId);
    }

    public List<Topic> selectAll(Topic topic) {
        return topicMapper.selectAll(topic);
    }

    public PageInfo<Topic> selectPage(Topic topic, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Topic> list = topicMapper.selectAll(topic);
        return PageInfo.of(list);
    }

}
