package com.example.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.entity.*;
import com.example.entity.resp.DepartmentHealthResponse;
import com.example.mapper.DepartMapper;
import com.example.mapper.EmployeeMapper;
import com.example.service.*;
import com.example.utils.TokenUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    private EmployeeService employeeService;
    @Resource
    private TestRecordService testRecordService;
    @Resource
    private TypeService typeService;
    @Resource
    private AdminService adminService;
    @Resource
    private DepartMapper departMapper;
    @Resource
    private EmployeeMapper employeeMapper;
    @Resource
    private DepartService departService;
    @Autowired
    private DepartmentHealthService departmentHealthService;

    /**
     * 获取当前管理员的公司ID，如果不是管理员则返回null
     */
    private Integer getCurrentCompanyId(HttpServletRequest request) {
        if (TokenUtils.getCurrentAdmin() != null) {
            return adminService.getAdminCompanyId();
        }
        return request != null ? (Integer) request.getAttribute("companyId") : null;
    }

    /**
     * 获取基础统计信息
     */
    @GetMapping("/base")
    public Result getBaseStatistics(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        Integer companyId = getCurrentCompanyId(request);

        // 获取当前数据
        int topicNum = topicService.selectAll(new Topic()).size();
        
        // 获取公司的测试卷数量
        int testPaperNum;
        if (companyId != null) {
            // 通过医生ID关联查询属于该公司的测试卷
            List<Integer> companyDoctorIds = doctorService.selectAll(new Doctor()).stream()
                .filter(d -> companyId.equals(d.getCompanyId()))
                .map(Doctor::getId)
                .collect(Collectors.toList());
            
            if (!companyDoctorIds.isEmpty()) {
                testPaperNum = testPaperService.selectAll(new TestPaper()).stream()
                    .filter(paper -> paper.getDoctorId() != null && companyDoctorIds.contains(paper.getDoctorId()))
                    .collect(Collectors.toList()).size();
            } else {
                testPaperNum = 0;
            }
        } else {
            testPaperNum = testPaperService.selectAll(new TestPaper()).size();
        }
        
        // 获取公司的医生数量
        int doctorNum;
        if (companyId != null) {
            Doctor query = new Doctor();
            query.setCompanyId(companyId);
            doctorNum = doctorService.selectAll(query).size();
        } else {
            doctorNum = doctorService.selectAll(new Doctor()).size();
        }
        
        // 获取公司的宣传数量
        int propagateNum;
        if (companyId != null) {
            // 通过医生ID关联查询属于该公司的宣传
            List<Integer> companyDoctorIds = doctorService.selectAll(new Doctor()).stream()
                .filter(d -> companyId.equals(d.getCompanyId()))
                .map(Doctor::getId)
                .collect(Collectors.toList());
            
            if (!companyDoctorIds.isEmpty()) {
                propagateNum = propagateService.selectAll(new Propagate()).stream()
                    .filter(prop -> prop.getDoctorId() != null && companyDoctorIds.contains(prop.getDoctorId()))
                    .collect(Collectors.toList()).size();
            } else {
                propagateNum = 0;
            }
        } else {
            propagateNum = propagateService.selectAll(new Propagate()).size();
        }
        
        // 获取公司的员工数量
        int userNum;
        if (companyId != null) {
            // 获取该公司下的所有部门ID
            List<Integer> departmentIds = departMapper.getDepartmentIdsByCompanyId(companyId);
            if (departmentIds.isEmpty()) {
                userNum = 0;
            } else {
                userNum = employeeMapper.selectByDepartmentIds(departmentIds).size();
            }
        } else {
            userNum = employeeService.selectAll(new Employee()).size();
        }

        // 计算变化率（模拟数据，实际应从历史记录中计算）
        Random random = new Random();
        int employeeChange = random.nextInt(21) - 10; // -10 到 10 的随机数
        int doctorChange = random.nextInt(21) - 10;
        int testPaperChange = random.nextInt(21) - 10;
        int propagateChange = random.nextInt(21) - 10;

        resultMap.put("topicNum", topicNum);
        resultMap.put("testPaperNum", testPaperNum);
        resultMap.put("doctorNum", doctorNum);
        resultMap.put("propagateNum", propagateNum);
        resultMap.put("userNum", userNum);
        
        // 添加变化率
        resultMap.put("employeeChange", employeeChange);
        resultMap.put("doctorChange", doctorChange);
        resultMap.put("testPaperChange", testPaperChange);
        resultMap.put("propagateChange", propagateChange);

        return Result.success(resultMap);
    }

    /**
     * 获取最近7天的测试记录统计
     */
    @GetMapping("/line")
    public Result line(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Long> yList = new ArrayList<>();
        Integer companyId = getCurrentCompanyId(request);

        // 初始化xList和yList
        Date today = new Date();
        DateTime start = DateUtil.offsetDay(today, -7);
        List<String> xList = DateUtil.rangeToList(start, today, DateField.DAY_OF_YEAR).stream().map(DateUtil::formatDate).toList();

        List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());
        
        // 如果是管理员，只统计本公司的测试记录
        if (companyId != null) {
            // 获取该公司下的所有部门ID
            List<Integer> departmentIds = departMapper.getDepartmentIdsByCompanyId(companyId);
            if (!departmentIds.isEmpty()) {
                testRecords = testRecords.stream()
                    .filter(record -> record.getDepartmentId() != null && departmentIds.contains(record.getDepartmentId()))
                    .collect(Collectors.toList());
            } else {
                testRecords = new ArrayList<>();
            }
        }
        
        for (String day : xList) {
            long count = testRecords.stream()
                .filter(x -> x.getTime() != null && x.getTime().contains(day))
                .count();
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
    public Result bar(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> xList = new ArrayList<>();
        List<Long> yList = new ArrayList<>();
        Integer companyId = getCurrentCompanyId(request);

        // 初始化xList和yList
        List<Type> types = typeService.selectAll(new Type());
        List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());
        
        // 如果是管理员，只统计本公司的测试记录
        if (companyId != null) {
            // 获取该公司下的所有部门ID
            List<Integer> departmentIds = departMapper.getDepartmentIdsByCompanyId(companyId);
            if (!departmentIds.isEmpty()) {
                testRecords = testRecords.stream()
                    .filter(record -> record.getDepartmentId() != null && departmentIds.contains(record.getDepartmentId()))
                    .collect(Collectors.toList());
            } else {
                testRecords = new ArrayList<>();
            }
        }

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
    public Result pie(HttpServletRequest request) {
        List<Map<String, Object>> list = new ArrayList<>();
        Integer companyId = getCurrentCompanyId(request);
        
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
    public Result getHealthTrend(String range, HttpServletRequest request) {
        System.out.println("接收到健康趋势请求，时间范围: " + range);
        
        Map<String, Object> resultMap = new HashMap<>();
        List<String> xAxis = new ArrayList<>();
        List<Integer> healthy = new ArrayList<>();
        List<Integer> subHealthy = new ArrayList<>();
        List<Integer> unhealthy = new ArrayList<>();
        Integer companyId = getCurrentCompanyId(request);
        
        System.out.println("当前公司ID: " + companyId);

        // 根据时间范围确定日期
        Date today = new Date();
        DateTime start;
        DateField dateField;
        
        if ("week".equals(range)) {
            start = DateUtil.offsetDay(today, -7);
            dateField = DateField.DAY_OF_YEAR;
        } else if ("quarter".equals(range)) {
            start = DateUtil.offsetMonth(today, -3);
            dateField = DateField.DAY_OF_YEAR; // 使用与其他视图相同的字段
        } else {
            // 默认为月
            start = DateUtil.offsetMonth(today, -1);
            dateField = DateField.DAY_OF_YEAR;
        }
        
        // 获取日期范围
        xAxis = DateUtil.rangeToList(start, today, dateField).stream().map(DateUtil::formatDate).toList();
        System.out.println("生成的日期范围: " + xAxis);
        
        // 获取所有测试记录
        List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());
        System.out.println("原始测试记录数量: " + testRecords.size());
        
        // 如果是管理员，只统计本公司的测试记录
        if (companyId != null) {
            // 获取该公司下的所有部门ID
            List<Integer> departmentIds = departMapper.getDepartmentIdsByCompanyId(companyId);
            System.out.println("公司部门ID列表: " + departmentIds);
            
            if (!departmentIds.isEmpty()) {
                testRecords = testRecords.stream()
                    .filter(record -> record.getDepartmentId() != null && departmentIds.contains(record.getDepartmentId()))
                    .collect(Collectors.toList());
            } else {
                testRecords = new ArrayList<>();
            }
            System.out.println("过滤后测试记录数量: " + testRecords.size());
        }
        
        // 按日期统计不同健康状态的人数
        for (String day : xAxis) {
            // 使用更精确的日期匹配
            List<TestRecord> dayRecords = testRecords.stream()
                    .filter(x -> x.getTime() != null && x.getTime().startsWith(day))
                    .toList();
            
           // System.out.println("日期 " + day + " 的记录数: " + dayRecords.size());
            
            // 根据分数划分健康状态
            long healthyCount = dayRecords.stream().filter(x -> x.getScore() >= 80).count();
            long subHealthyCount = dayRecords.stream().filter(x -> x.getScore() >= 60 && x.getScore() < 80).count();
            long unhealthyCount = dayRecords.stream().filter(x -> x.getScore() < 60).count();
            
            //System.out.println("日期 " + day + " 的健康/亚健康/不健康人数: " + healthyCount + "/" + subHealthyCount + "/" + unhealthyCount);
            
            healthy.add((int) healthyCount);
            subHealthy.add((int) subHealthyCount);
            unhealthy.add((int) unhealthyCount);
        }
        
        resultMap.put("xAxis", xAxis);
        resultMap.put("healthy", healthy);
        resultMap.put("subHealthy", subHealthy);
        resultMap.put("unhealthy", unhealthy);
        
        //System.out.println("返回数据: xAxis=" + xAxis.size() + ", healthy=" + healthy.size());
        return Result.success(resultMap);
    }

    /**
     * 获取部门健康状况数据
     */
    @GetMapping("/department-health")
    public Result getDepartmentHealth(HttpServletRequest request) {
        try {
            // 使用统一的方法获取公司ID
            Integer companyId = getCurrentCompanyId(request);
            if (companyId == null) {
                return Result.error("未找到公司信息");
            }
            
            DepartmentHealthResponse response = departmentHealthService.getDepartmentHealthData(companyId);
            return Result.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取部门健康数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取高风险员工列表
     */
    @GetMapping("/high-risk-employees")
    public Result getHighRiskEmployees(HttpServletRequest request) {
        try {
            // 获取最近30天的测试记录
            Date today = new Date();
            DateTime thirtyDaysAgo = DateUtil.offsetDay(today, -30);
            String startDate = DateUtil.formatDate(thirtyDaysAgo);
            Integer companyId = getCurrentCompanyId(request);
            
            List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());
            
            // 如果是管理员，只统计本公司的测试记录
            if (companyId != null) {
                // 获取该公司下的所有部门ID
                List<Integer> departmentIds = departMapper.getDepartmentIdsByCompanyId(companyId);
                if (!departmentIds.isEmpty()) {
                    // 获取该公司的所有员工ID
                    List<Employee> employees = employeeMapper.selectByDepartmentIds(departmentIds);
                    List<Integer> employeeIds = employees.stream()
                        .map(Employee::getId)
                        .collect(Collectors.toList());
                    
                    // 过滤出该公司员工的测试记录
                    testRecords = testRecords.stream()
                        .filter(record -> record.getUserId() != null && employeeIds.contains(record.getUserId()))
                        .collect(Collectors.toList());
                } else {
                    testRecords = new ArrayList<>();
                }
            }
            
            // 筛选出30天内的记录
            List<TestRecord> recentRecords = testRecords.stream()
                    .filter(record -> record.getTime() != null && record.getTime().compareTo(startDate) >= 0)
                    .toList();
            
            // 按用户ID分组测试记录
            Map<Integer, List<TestRecord>> recordsByUser = recentRecords.stream()
                    .collect(Collectors.groupingBy(TestRecord::getUserId));
            
            // 获取当前时间
            long currentTime = System.currentTimeMillis();
            
            // 计算每个用户的加权平均分数
            Map<Integer, Double> userWeightedScores = new HashMap<>();
            
            for (Map.Entry<Integer, List<TestRecord>> entry : recordsByUser.entrySet()) {
                Integer userId = entry.getKey();
                List<TestRecord> userRecords = entry.getValue();
                
                double weightedSum = 0;
                double weightSum = 0;
                
                // 对每条测试记录计算加权分数
                for (TestRecord record : userRecords) {
                    try {
                        // 标准化分数 (确保所有分数都在0-100范围内)
                        double normalizedScore = normalizeScore(record);
                        
                        // 解析时间字符串
                        long testTime = parseTimeString(record.getTime());
                        
                        // 计算时间权重因子 (越近的测试权重越高)
                        double timeFactor = calculateTimeFactor(testTime, currentTime);
                        
                        // 计算加权分数
                        double weight = 1.0 * timeFactor;
                        weightedSum += normalizedScore * weight;
                        weightSum += weight;
                    } catch (Exception e) {
                        // 时间解析错误，使用默认权重
                        double normalizedScore = normalizeScore(record);
                        weightedSum += normalizedScore;
                        weightSum += 1.0;
                    }
                }
                
                // 计算加权平均分
                if (weightSum > 0) {
                    userWeightedScores.put(userId, weightedSum / weightSum);
                }
            }
            
            // 筛选出分数低于50的高风险员工 (与DepartmentHealthService保持一致)
            List<Map<String, Object>> highRiskEmployees = new ArrayList<>();
            
            for (Map.Entry<Integer, Double> entry : userWeightedScores.entrySet()) {
                Integer userId = entry.getKey();
                Double weightedScore = entry.getValue();
                
                if (weightedScore < 50) {
                    Map<String, Object> map = new HashMap<>();
                    // 查询员工信息
                    Employee employee = employeeMapper.selectById(userId);
                    if (employee != null) {
                        map.put("id", employee.getId());
                        map.put("name", employee.getName());
                        
                        // 获取部门信息
                        if (employee.getDepartmentId() != null) {
                            Department department = departMapper.selectById(employee.getDepartmentId());
                            if (department != null) {
                                map.put("department", department.getName());
                                map.put("departmentId", department.getId());
                            } else {
                                map.put("department", "未知部门");
                                map.put("departmentId", null);
                            }
                        } else {
                            map.put("department", "未分配部门");
                            map.put("departmentId", null);
                        }
                    } else {
                        // 尝试从测试记录中获取用户名
                        String userName = recordsByUser.get(userId).stream()
                                .map(TestRecord::getUserName)
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse("未知员工");
                        
                        map.put("id", userId);
                        map.put("name", userName);
                        map.put("department", "未知部门");
                        map.put("departmentId", null);
                    }
                    
                    // 添加加权平均分
                    map.put("score", Math.round(weightedScore * 100) / 100.0);
                    
                    // 获取最近的测试记录
                    TestRecord latestRecord = recordsByUser.get(userId).stream()
                            .max(Comparator.comparing(TestRecord::getTime))
                            .orElse(null);
                    
                    if (latestRecord != null) {
                        map.put("testTime", latestRecord.getTime());
                        map.put("testPaperName", latestRecord.getTestPaperName());
                        map.put("latestScore", latestRecord.getScore());
                    } else {
                        map.put("testTime", "未知");
                        map.put("testPaperName", "未知");
                        map.put("latestScore", 0);
                    }
                    
                    highRiskEmployees.add(map);
                }
            }
            
            // 按加权平均分排序 (从低到高)
            highRiskEmployees.sort(Comparator.comparingDouble(e -> ((Double) e.get("score"))));
            
            return Result.success(highRiskEmployees);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取高风险员工数据失败: " + e.getMessage());
        }
    }

    /**
     * 标准化测试分数 (确保所有分数都在0-100范围内)
     */
    private double normalizeScore(TestRecord record) {
        Integer score = record.getScore();
        if (score == null) return 0;
        
        // 获取测试卷总分
        Integer totalScore = null;
        if (record.getTestPaperId() != null) {
            TestPaper testPaper = testPaperService.selectById(record.getTestPaperId());
            if (testPaper != null) {
                totalScore = testPaper.getScore();
            }
        }
        
        // 如果无法获取总分，假设总分为100
        if (totalScore == null || totalScore <= 0) {
            totalScore = 100;
        }
        
        // 标准化为0-100分
        return (score * 100.0) / totalScore;
    }

    /**
     * 解析时间字符串为毫秒时间戳
     */
    private long parseTimeString(String timeStr) throws Exception {
        // 尝试多种可能的时间格式
        String[] possibleFormats = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "MM/dd/yyyy HH:mm:ss",
            "MM-dd-yyyy HH:mm:ss"
        };
        
        for (String format : possibleFormats) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
                return sdf.parse(timeStr).getTime();
            } catch (Exception e) {
                // 尝试下一种格式
                continue;
            }
        }
        
        // 如果所有格式都失败，抛出异常
        throw new Exception("无法解析时间字符串: " + timeStr);
    }

    /**
     * 计算时间权重因子
     * 使用指数衰减函数，使得越近的数据权重越高
     */
    private double calculateTimeFactor(long testTime, long currentTime) {
        // 计算天数差
        long daysDiff = (currentTime - testTime) / (1000 * 60 * 60 * 24);
        // 使用指数衰减函数
        return Math.exp(-0.05 * daysDiff); // 衰减系数可以调整
    }

    /**
     * 获取最新评估完成情况
     */
    @GetMapping("/recent-assessments")
    public Result getRecentAssessments(HttpServletRequest request) {
        // 获取最近的测试记录
        List<TestRecord> testRecords = testRecordService.selectAll(new TestRecord());
        Integer companyId = getCurrentCompanyId(request);
        
        // 如果是管理员，只统计本公司的测试记录
        if (companyId != null) {
            // 获取该公司下的所有部门ID
            List<Integer> departmentIds = departMapper.getDepartmentIdsByCompanyId(companyId);
            if (!departmentIds.isEmpty()) {
                testRecords = testRecords.stream()
                    .filter(record -> record.getDepartmentId() != null && departmentIds.contains(record.getDepartmentId()))
                    .collect(Collectors.toList());
            } else {
                testRecords = new ArrayList<>();
            }
        }
        
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
                    // 可以添加头像字段，如果有的话
                    // map.put("avatar", record.getAvatar());
                    return map;
                })
                .collect(Collectors.toList());
        
        return Result.success(recentAssessments);
    }
}
