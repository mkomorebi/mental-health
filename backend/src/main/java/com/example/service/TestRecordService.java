package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.Doctor;
import com.example.entity.TestPaper;
import com.example.entity.TestRecord;
import com.example.entity.Topic;
import com.example.entity.resp.MetricResp;
import com.example.entity.resp.DepartmentStatsResp;
import com.example.entity.resp.TestPaperStatsResp;
import com.example.exception.CustomException;
import com.example.mapper.TestPaperMapper;
import com.example.mapper.TestRecordMapper;
import com.example.mapper.DoctorMapper;
import com.example.utils.TokenUtils;
import com.example.utils.SpringContextUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.ArrayList;
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
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private AdminService adminService;

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
            // 获取要删除的记录
            TestRecord record = testRecordMapper.selectById(id);
            if (record == null) {
                throw new CustomException("500", "记录不存在");
            }

            // 获取当前用户
            Account currentUser = TokenUtils.getCurrentUser();
            Integer userCompanyId = null;

            // 根据角色获取用户所属公司ID
            if ("ADMIN".equals(currentUser.getRole())) {
                Admin admin = TokenUtils.getCurrentAdmin();
                if (admin != null) {
                    userCompanyId = admin.getCompanyId();
                }
            } else if ("DOCTOR".equals(currentUser.getRole())) {
                Doctor doctor = doctorMapper.selectById(currentUser.getId());
                if (doctor != null) {
                    userCompanyId = doctor.getCompanyId();
                }
            }

            // 如果是管理员或医生，验证公司ID
            if (userCompanyId != null) {
                TestRecord fullRecord = testRecordMapper.selectAll(record).stream().findFirst().orElse(null);
                if (fullRecord == null || !userCompanyId.equals(fullRecord.getCompanyId())) {
                    throw new CustomException("500", "您没有权限删除其他公司的测试记录");
                }
            } else if ("USER".equals(currentUser.getRole())) {
                // 普通用户只能删除自己的记录
                if (!currentUser.getId().equals(record.getUserId())) {
                    throw new CustomException("500", "您只能删除自己的测试记录");
                }
            }

            System.out.println("尝试删除测试记录，ID: " + id);
            testRecordMapper.deleteById(id);
            System.out.println("测试记录删除成功，ID: " + id);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("删除测试记录失败，ID: " + id);
            e.printStackTrace();
            throw new CustomException("500", "删除失败：" + e.getMessage());
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
        
        // 获取当前用户的公司ID
        Integer companyId = null;
        
        try {
            if ("ADMIN".equals(currentUser.getRole())) {
                // 使用 AdminService 获取公司ID
                companyId = adminService.getAdminCompanyId();
                if (companyId == null) {
                    throw new CustomException("500", "无法确定管理员所属公司");
                }
            } else if ("DOCTOR".equals(currentUser.getRole())) {
                Doctor doctor = doctorMapper.selectById(currentUser.getId());
                if (doctor != null) {
                    companyId = doctor.getCompanyId();
                }
                if (companyId == null) {
                    throw new CustomException("500", "无法确定医生所属公司");
                }
            } else if ("USER".equals(currentUser.getRole())) {
                // 普通用户只能看到自己的记录
                testRecord.setUserId(currentUser.getId());
            }

            // 设置公司ID条件（对管理员和医生生效）
            if (companyId != null) {
                testRecord.setCompanyId(companyId);
                System.out.println("正在按公司ID过滤测试记录: " + companyId + ", 用户角色: " + currentUser.getRole());
            }

            if ("DOCTOR".equals(currentUser.getRole())) {
                testRecord.setDoctorId(currentUser.getId());
            }

            PageHelper.startPage(pageNum, pageSize);
            List<TestRecord> list = testRecordMapper.selectAll(testRecord);
            
            return PageInfo.of(list);
            
        } catch (Exception e) {
            System.err.println("查询测试记录时发生错误: " + e.getMessage());
            throw new CustomException("500", "查询测试记录失败：" + e.getMessage());
        }
    }

    public List<MetricResp> metric(Integer companyId) {
        return testRecordMapper.metric(companyId);
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

        try {
            // 获取医生所属的公司ID
            Doctor doctor = doctorMapper.selectById(doctorId);
            if (doctor == null || doctor.getCompanyId() == null) {
                throw new CustomException("500", "无法确定医生所属公司");
            }

            query.setCompanyId(doctor.getCompanyId());
            System.out.println("医生页面查询测试记录，公司ID: " + doctor.getCompanyId());

            PageHelper.startPage(pageNum, pageSize);
            List<TestRecord> list = testRecordMapper.selectDoctorRecords(query, minScore, maxScore, doctorId);

            return new PageInfo<>(list);
        } catch (Exception e) {
            System.err.println("查询医生测试记录时发生错误: " + e.getMessage());
            throw new CustomException("500", "查询医生测试记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取部门统计数据
     */
    public PageInfo<DepartmentStatsResp> getDepartmentStats(
            Integer pageNum, Integer pageSize, 
            String departmentName, String paperTitle, String publishTime) {
        
        // 获取当前用户所属公司ID
        Integer companyId = null;
        Account currentUser = TokenUtils.getCurrentUser();
        
        // 如果是管理员，获取其公司ID
        if ("ADMIN".equals(currentUser.getRole())) {
            Admin admin = TokenUtils.getCurrentAdmin();
            if (admin != null && admin.getCompanyId() != null) {
                companyId = admin.getCompanyId();
            } else {
                // 从请求上下文获取公司ID
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    companyId = (Integer) attributes.getRequest().getAttribute("companyId");
                }
            }
            
            // 如果仍然无法获取公司ID，尝试从AdminService获取
            if (companyId == null) {
                try {
                    AdminService adminService = SpringContextUtil.getBean(AdminService.class);
                    companyId = adminService.getAdminCompanyId();
                } catch (Exception e) {
                    System.err.println("获取AdminService失败: " + e.getMessage());
                }
            }
            
            // 记录日志
            System.out.println("TestRecordService.getDepartmentStats: 管理员ID = " + 
                              (admin != null ? admin.getId() : "未知") + 
                              ", 公司ID = " + companyId);
        }
        
        // 如果无法获取公司ID，抛出异常
        if (companyId == null) {
            throw new CustomException("500", "无法确定您所属的公司，请联系系统管理员");
        }
        
        // 查询部门统计数据
        List<DepartmentStatsResp> stats = testRecordMapper.getDepartmentStats(
                companyId, departmentName, paperTitle, publishTime);
        
        // 手动分页
        int total = stats.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<DepartmentStatsResp> pageList = startIndex < total 
                ? stats.subList(startIndex, endIndex) 
                : new ArrayList<>();
        
        PageInfo<DepartmentStatsResp> pageInfo = new PageInfo<>(pageList);
        pageInfo.setTotal(total);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        
        return pageInfo;
    }

    /**
     * 导出部门统计数据
     */
    public void exportDepartmentStats(
            HttpServletResponse response, 
            String departmentName, String paperTitle, String publishTime) throws IOException {
        
        // 获取当前用户所属公司ID
        Integer companyId = null;
        Account currentUser = TokenUtils.getCurrentUser();
        
        // 如果是管理员，获取其公司ID
        if ("ADMIN".equals(currentUser.getRole())) {
            Admin admin = TokenUtils.getCurrentAdmin();
            if (admin != null && admin.getCompanyId() != null) {
                companyId = admin.getCompanyId();
            } else {
                // 从请求上下文获取公司ID
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    companyId = (Integer) attributes.getRequest().getAttribute("companyId");
                }
            }
            
            // 如果仍然无法获取公司ID，尝试从AdminService获取
            if (companyId == null) {
                try {
                    AdminService adminService = SpringContextUtil.getBean(AdminService.class);
                    companyId = adminService.getAdminCompanyId();
                } catch (Exception e) {
                    System.err.println("获取AdminService失败: " + e.getMessage());
                }
            }
        }
        
        // 如果无法获取公司ID，抛出异常
        if (companyId == null) {
            throw new CustomException("500", "无法确定您所属的公司，请联系系统管理员");
        }
        
        // 查询部门统计数据
        List<DepartmentStatsResp> stats = testRecordMapper.getDepartmentStats(
                companyId, departmentName, paperTitle, publishTime);
        
        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("部门统计数据");
        
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("部门名称");
        headerRow.createCell(1).setCellValue("用户数");
        headerRow.createCell(2).setCellValue("问卷名称");
        headerRow.createCell(3).setCellValue("发布时间");
        headerRow.createCell(4).setCellValue("完成率");
        headerRow.createCell(5).setCellValue("平均分");
        
        // 填充数据
        int rowNum = 1;
        for (DepartmentStatsResp dept : stats) {
            if (dept.getTestPapers() != null && !dept.getTestPapers().isEmpty()) {
                for (TestPaperStatsResp paper : dept.getTestPapers()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(dept.getDepartmentName());
                    row.createCell(1).setCellValue(dept.getUserNum());
                    row.createCell(2).setCellValue(paper.getPaperTitle());
                    row.createCell(3).setCellValue(paper.getPublishTime());
                    row.createCell(4).setCellValue(paper.getCompletionRate() * 100 + "%");
                    row.createCell(5).setCellValue(paper.getAvgScore());
                }
            } else {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dept.getDepartmentName());
                row.createCell(1).setCellValue(dept.getUserNum());
                row.createCell(2).setCellValue("暂无问卷数据");
                row.createCell(3).setCellValue("");
                row.createCell(4).setCellValue("0%");
                row.createCell(5).setCellValue(0);
            }
        }
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=department_stats.xlsx");
        
        // 写入响应
        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
