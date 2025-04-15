package com.example.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.entity.*;
import com.example.service.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计控制器，处理与统计信息相关的请求。
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Resource
    private TopicService topicService;
    @Resource
    private TestPaperService testPaperService;
    @Resource
    private DoctorService doctorService;
    @Resource
    private PropagateService propagateService;
    @Resource
    private UserService userService;
    @Resource
    private TestRecordService testRecordService;
    @Resource
    private TypeService typeService;

    /**
     * 获取基础统计信息
     */
    @GetMapping("/base")
    public Result base() {
        Map<String, Integer> resultMap = new HashMap<>();

        // 获取题库中所有题目的数量
        resultMap.put("topicNum", topicService.selectAll(new Topic()).size());
        // 获取所有的心理测试卷的数量
        resultMap.put("testPaperNum", testPaperService.selectAll(new TestPaper()).size());
        // 获取所有入驻心理医生的数量
        resultMap.put("doctorNum", doctorService.selectAll(new Doctor()).size());
        // 获取所有心理宣传的数量
        resultMap.put("propagateNum", propagateService.selectAll(new Propagate()).size());
        // 获取所有用户的数量
        resultMap.put("userNum", userService.selectAll(new User()).size());

        return Result.success(resultMap);
    }

    /**
     * 获取最近7天的测试记录统计
     */
    @GetMapping("/line")
    public Result line() {
        Map<String, Object> resultMap = new HashMap<>();
        List<Long> yList = new ArrayList<>();

        // 初始化xList和yList
        Date today = new Date();
        DateTime start = DateUtil.offsetDay(today, -7);
        List<String> xList = DateUtil.rangeToList(start, today, DateField.DAY_OF_YEAR).stream().map(DateUtil::formatDate).toList();

        List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());
        for (String day : xList) {
            long count = testRecords.stream().filter(x -> x.getTime().contains(day)).count();
            yList.add(count);
        }

        resultMap.put("xAxis", xList);
        resultMap.put("yAxis", yList);
        return Result.success(resultMap);
    }

    /**
     * 获取各类型测试记录的统计信息
     */
    @GetMapping("/bar")
    public Result bar() {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> xList = new ArrayList<>();
        List<Long> yList = new ArrayList<>();

        // 初始化xList和yList
        List<Type> types = typeService.selectAll(new Type());
        List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());

        for (Type type : types) {
            xList.add(type.getTitle());
            yList.add(testRecords.stream().filter(x -> x.getTypeName().equals(type.getTitle())).count());
        }

        resultMap.put("xAxis", xList);
        resultMap.put("yAxis", yList);
        return Result.success(resultMap);
    }

    /**
     * 获取各类型题目的统计信息
     */
    @GetMapping("/pie")
    public Result pie() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Type> types = typeService.selectAll(new Type());
        List<Topic> topics = topicService.selectAll(new Topic());
        for (Type type : types) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", type.getTitle());
            map.put("value", topics.stream().filter(x -> x.getTypeId().equals(type.getId())).count());
            list.add(map);
        }
        return Result.success(list);
    }

    /**
     * 获取心理健康测试趋势数据
     */
    @GetMapping("/health-trend")
    public Result healthTrend(String range) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> xAxis = new ArrayList<>();
        List<Long> healthy = new ArrayList<>();
        List<Long> subHealthy = new ArrayList<>();
        List<Long> unhealthy = new ArrayList<>();

        // 根据时间范围确定日期
        Date today = new Date();
        DateTime start;
        DateField dateField;
        
        if ("week".equals(range)) {
            start = DateUtil.offsetDay(today, -7);
            dateField = DateField.DAY_OF_YEAR;
        } else if ("quarter".equals(range)) {
            start = DateUtil.offsetMonth(today, -3);
            dateField = DateField.WEEK_OF_YEAR;
        } else {
            // 默认为月
            start = DateUtil.offsetMonth(today, -1);
            dateField = DateField.DAY_OF_YEAR;
        }
        
        // 获取日期范围
        xAxis = DateUtil.rangeToList(start, today, dateField).stream().map(DateUtil::formatDate).toList();
        
        // 获取所有测试记录
        List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());
        
        // 按日期统计不同健康状态的人数
        for (String day : xAxis) {
            List<TestRecord> dayRecords = testRecords.stream()
                    .filter(x -> x.getTime().contains(day))
                    .toList();
            
            // 根据分数划分健康状态
            long healthyCount = dayRecords.stream().filter(x -> x.getScore() >= 80).count();
            long subHealthyCount = dayRecords.stream().filter(x -> x.getScore() >= 60 && x.getScore() < 80).count();
            long unhealthyCount = dayRecords.stream().filter(x -> x.getScore() < 60).count();
            
            healthy.add(healthyCount);
            subHealthy.add(subHealthyCount);
            unhealthy.add(unhealthyCount);
        }
        
        resultMap.put("xAxis", xAxis);
        resultMap.put("healthy", healthy);
        resultMap.put("subHealthy", subHealthy);
        resultMap.put("unhealthy", unhealthy);
        
        return Result.success(resultMap);
    }

    /**
     * 获取部门健康状况数据
     */
    @GetMapping("/department-health")
    public Result departmentHealth() {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> departments = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
        
        // 获取所有测试记录
        List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());
        
        // 按部门分组 - 使用departmentName而不是department
        Map<String, List<TestRecord>> departmentRecords = testRecords.stream()
                .filter(record -> record.getDepartmentName() != null && !record.getDepartmentName().isEmpty())
                .collect(Collectors.groupingBy(TestRecord::getDepartmentName));
        
        // 计算每个部门的平均分数
        for (Map.Entry<String, List<TestRecord>> entry : departmentRecords.entrySet()) {
            String department = entry.getKey();
            List<TestRecord> records = entry.getValue();
            
            // 计算平均分
            double avgScore = records.stream()
                    .mapToInt(TestRecord::getScore)
                    .average()
                    .orElse(0);
            
            departments.add(department);
            scores.add(Math.round(avgScore * 10) / 10.0); // 保留一位小数
        }
        
        resultMap.put("departments", departments);
        resultMap.put("scores", scores);
        
        return Result.success(resultMap);
    }

    /**
     * 获取高风险员工列表
     */
    @GetMapping("/high-risk-employees")
    public Result highRiskEmployees() {
        // 获取最近30天的测试记录
        Date today = new Date();
        DateTime thirtyDaysAgo = DateUtil.offsetDay(today, -30);
        String startDate = DateUtil.formatDate(thirtyDaysAgo);
        
        List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());
        
        // 筛选出30天内的记录
        List<TestRecord> recentRecords = testRecords.stream()
                .filter(record -> record.getTime() != null && record.getTime().compareTo(startDate) >= 0)
                .toList();
        
        // 筛选出分数低于60的高风险员工
        List<Map<String, Object>> highRiskEmployees = recentRecords.stream()
                .filter(record -> record.getScore() < 60)
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", record.getUserName());
                    map.put("department", record.getDepartmentName()); // 使用departmentName
                    map.put("score", record.getScore());
                    map.put("testTime", record.getTime());
                    return map;
                })
                .collect(Collectors.toList());
        
        return Result.success(highRiskEmployees);
    }

    /**
     * 获取最新评估完成情况
     */
    @GetMapping("/recent-assessments")
    public Result recentAssessments() {
        // 获取最近的测试记录
        List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());
        
        // 计算7天前的日期
        Date today = new Date();
        DateTime sevenDaysAgo = DateUtil.offsetDay(today, -7);
        String startDate = DateUtil.formatDate(sevenDaysAgo);
        
        // 按时间排序，取最近7天的记录
        List<Map<String, Object>> recentAssessments = testRecords.stream()
                .filter(record -> record.getTime() != null && record.getTime().compareTo(startDate) >= 0)
                .sorted((a, b) -> b.getTime().compareTo(a.getTime()))
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", record.getUserName());
                    map.put("time", record.getTime());
                    map.put("assessmentName", record.getTestPaperName());
                    return map;
                })
                .collect(Collectors.toList());
        
        return Result.success(recentAssessments);
    }
}
