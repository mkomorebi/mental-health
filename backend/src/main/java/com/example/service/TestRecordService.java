package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Account;
import com.example.entity.TestPaper;
import com.example.entity.TestRecord;
import com.example.entity.Topic;
import com.example.entity.resp.MetricResp;
import com.example.exception.CustomException;
import com.example.mapper.TestPaperMapper;
import com.example.mapper.TestRecordMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TestRecordService 业务层，用于处理测试记录相关的业务逻辑。
 * 提供对 TestRecord 的增、删、改、查等操作。
 */
@Service
public class TestRecordService {

    @Resource
    private TestRecordMapper testRecordMapper;
    @Resource
    private TestPaperMapper testPaperMapper;

    /**
     * 添加新的 TestRecord 记录。
     *
     * @param testPaper TestPaper 实体
     */
    public void add(TestPaper testPaper) {
        // 校验用户是否将所有题目都答完了
        List<Topic> topicList = testPaper.getTopicList();
        List<Topic> collect = topicList.stream().filter(x -> ObjectUtil.isNotEmpty(x.getUserAnswer())).toList();
        if (topicList.size() != collect.size()) {
            throw new CustomException("500", "请您务必答完所有题目才能提交");
        }

        Account currentUser = TokenUtils.getCurrentUser();
        // 添加日志
        System.out.println("添加测试记录 - 当前用户ID: " + currentUser.getId());
        System.out.println("添加测试记录 - 当前用户角色: " + currentUser.getRole());
        
        TestRecord testRecord = new TestRecord();
        testRecord.setTestPaperId(testPaper.getId());
        testRecord.setDoctorId(testPaper.getDoctorId());
        testRecord.setUserId(currentUser.getId());
        testRecord.setTime(DateUtil.now());

        // 计算用户获得的总分数
        Integer score = 0;
        for (Topic topic : topicList) {
            String userAnswer = topic.getUserAnswer();
            if (userAnswer.equals(topic.getaName())) {
                score += topic.getaScore();
            }
            if (userAnswer.equals(topic.getbName())) {
                score += topic.getbScore();
            }
            if (userAnswer.equals(topic.getcName())) {
                score += topic.getcScore();
            }
            if (userAnswer.equals(topic.getdName())) {
                score += topic.getdScore();
            }
        }
        testRecord.setScore(score);

        // 根据试卷的分数区间，获取到对应分数的结果
        String aRange = testPaper.getaRange();
        String bRange = testPaper.getbRange();
        String cRange = testPaper.getcRange();
        if (score >= Integer.parseInt(aRange.split("~")[0]) && score <= Integer.parseInt(aRange.split("~")[1])) {
            testRecord.setResult(testPaper.getaAnswer());
        }
        if (score >= Integer.parseInt(bRange.split("~")[0]) && score <= Integer.parseInt(bRange.split("~")[1])) {
            testRecord.setResult(testPaper.getbAnswer());
        }
        if (score >= Integer.parseInt(cRange.split("~")[0]) && score <= Integer.parseInt(cRange.split("~")[1])) {
            testRecord.setResult(testPaper.getcAnswer());
        }

        // 添加日志
        System.out.println("保存测试记录 - 用户ID: " + testRecord.getUserId());
        System.out.println("保存测试记录 - 医生ID: " + testRecord.getDoctorId());
        System.out.println("保存测试记录 - 分数: " + testRecord.getScore());
        
        testRecordMapper.insert(testRecord);

        // 测试题的测试人数+1
        testPaper.setTestNum(testPaper.getTestNum() + 1);
        testPaperMapper.updateById(testPaper);
    }

    public void updateById(TestRecord testRecord) {
        testRecordMapper.updateById(testRecord);
    }

    @Transactional
    public void deleteById(Integer id) {
        try {
            System.out.println("尝试删除测试记录，ID: " + id);
            testRecordMapper.deleteById(id);
            System.out.println("测试记录删除成功，ID: " + id);
        } catch (Exception e) {
            System.err.println("删除测试记录失败，ID: " + id);
            e.printStackTrace();
            throw e; // 重新抛出异常，让上层处理
        }
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            testRecordMapper.deleteById(id);
        }
    }

    public TestRecord selectById(Integer id) {
        return testRecordMapper.selectById(id);
    }

    public List<TestRecord> selectAll(TestRecord testRecord) {
        return testRecordMapper.selectAll(testRecord);
    }

    public PageInfo<TestRecord> selectPage(TestRecord testRecord, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        
        if ("DOCTOR".equals(currentUser.getRole())) {
            testRecord.setDoctorId(currentUser.getId());
        }
        if ("USER".equals(currentUser.getRole())) {
            testRecord.setUserId(currentUser.getId());
        }
        
        PageHelper.startPage(pageNum, pageSize);
        List<TestRecord> list = testRecordMapper.selectAll(testRecord);
        
        return PageInfo.of(list);
    }

    public List<MetricResp> metric() {
        return testRecordMapper.metric();
    }

    public PageInfo<TestRecord> selectRecentWeekPage(TestRecord testRecord, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        
        if ("DOCTOR".equals(currentUser.getRole())) {
            testRecord.setDoctorId(currentUser.getId());
        }
        if ("USER".equals(currentUser.getRole())) {
            testRecord.setUserId(currentUser.getId());
        }
        
        PageHelper.startPage(pageNum, pageSize);
        List<TestRecord> list = testRecordMapper.selectRecentWeek(testRecord);
        
        return PageInfo.of(list);
    }

    public PageInfo<TestRecord> selectDoctorPage(
        Integer pageNum, Integer pageSize, TestRecord query, 
        Double minScore, Double maxScore, Integer doctorId) {
        
        PageHelper.startPage(pageNum, pageSize);
        List<TestRecord> list = testRecordMapper.selectDoctorRecords(
            query, minScore, maxScore, doctorId);
        
        return new PageInfo<>(list);
    }

}
