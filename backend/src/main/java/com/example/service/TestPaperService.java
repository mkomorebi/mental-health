package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Doctor;
import com.example.entity.TestPaper;
import com.example.entity.Topic;
import com.example.exception.CustomException;
import com.example.mapper.DoctorMapper;
import com.example.mapper.TestPaperMapper;
import com.example.mapper.TopicMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.example.entity.Employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TestPaperService 业务层，用于处理试卷信息相关的业务逻辑。
 * 提供对 TestPaper 的增、删、改、查等操作。
 */
@Service
public class TestPaperService {

    @Resource
    private TestPaperMapper testPaperMapper;
    @Resource
    private TopicMapper topicMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private AdminService adminService;
    @Resource
    private DoctorService doctorService;
    @Resource
    private EmployeeService employeeService;

    /**
     * 添加新的 TestPaper 记录。
     *
     * @param testPaper TestPaper 实体
     */
    public void add(TestPaper testPaper) {
        // 校验一下前端传过来的参数
        checkTestPaper(testPaper);

        Account currentUser = TokenUtils.getCurrentUser();
        testPaper.setDoctorId(currentUser.getId());

        // 获取当前医生的公司ID
        Doctor currentDoctor = doctorMapper.selectById(currentUser.getId());
        if (currentDoctor == null || currentDoctor.getCompanyId() == null) {
            throw new CustomException("500", "无法确定当前医生所属公司");
        }
        Integer companyId = currentDoctor.getCompanyId();

        // 处理试卷中题目id的数据
        if (1 == testPaper.getFlag()) {
            List<Integer> idList = testPaper.getIdList();
            // 验证所选题目是否都属于当前公司
            for (Integer topicId : idList) {
                Topic topic = topicMapper.selectById(topicId, null);
                if (topic == null) {
                    throw new CustomException("500", "所选题目不存在，ID: " + topicId);
                }
                if (!companyId.equals(topic.getCompanyId())) {
                    throw new CustomException("500", "不能选择其他公司的题目，题目ID: " + topicId);
                }
            }
            testPaper.setIds(JSONUtil.toJsonStr(idList)); // "[1,2,3,4,5]"
        } else {
            // 自动出题 - 只从当前公司的题库中选择
            Topic queryTopic = new Topic();
            queryTopic.setTypeId(testPaper.getTypeId());
            queryTopic.setCompanyId(companyId);  // 只查询当前公司的题目
            List<Topic> topics = topicMapper.selectAll(queryTopic);
            
            if (topics.isEmpty()) {
                throw new CustomException("500", "当前公司在该分类下没有可用的题目");
            }

            List<Integer> list = topics.stream()
                .map(Topic::getId)
                .collect(Collectors.toList());
                
            Collections.shuffle(list);
            if (list.size() < testPaper.getNum()) {
                throw new CustomException("500", "您选择的分类题库里题目数量不足，请减少试卷题目数量或者联系管理员增加题库中该分类下面的题目数量");
            }
            testPaper.setIds(JSONUtil.toJsonStr(list.subList(0, testPaper.getNum())));
        }

        // 设置分数区间
        if (testPaper.getaLeft() != null && testPaper.getaRight() != null) {
            testPaper.setaRange(testPaper.getaLeft() + "~" + testPaper.getaRight());
        }
        if (testPaper.getbLeft() != null && testPaper.getbRight() != null) {
            testPaper.setbRange(testPaper.getbLeft() + "~" + testPaper.getbRight());
        }
        if (testPaper.getcLeft() != null && testPaper.getcRight() != null) {
            testPaper.setcRange(testPaper.getcLeft() + "~" + testPaper.getcRight());
        }

        testPaper.setTime(DateUtil.now());
        testPaper.setStatus("待审核");
        testPaperMapper.insert(testPaper);
    }

    private void checkTestPaper(TestPaper testPaper) {
        // 基本字段验证
        if (ObjectUtil.isEmpty(testPaper.getTitle())
                || ObjectUtil.isEmpty(testPaper.getImg())
                || ObjectUtil.isEmpty(testPaper.getContent())
                || ObjectUtil.isEmpty(testPaper.getTypeId())
                || ObjectUtil.isEmpty(testPaper.getNum())
                || ObjectUtil.isEmpty(testPaper.getScore())
                || ObjectUtil.isEmpty(testPaper.getaLeft())
                || ObjectUtil.isEmpty(testPaper.getbLeft())
                || ObjectUtil.isEmpty(testPaper.getcLeft())
                || ObjectUtil.isEmpty(testPaper.getaRight())
                || ObjectUtil.isEmpty(testPaper.getbRight())
                || ObjectUtil.isEmpty(testPaper.getcRight())
                || ObjectUtil.isEmpty(testPaper.getaAnswer())
                || ObjectUtil.isEmpty(testPaper.getbAnswer())
                || ObjectUtil.isEmpty(testPaper.getcAnswer())
        ) {
            throw new CustomException("500", "数据不完整，请填写所有需要的数据");
        }

        // 验证分数区间的合法性
        if (testPaper.getaLeft() >= testPaper.getaRight() ||
            testPaper.getbLeft() >= testPaper.getbRight() ||
            testPaper.getcLeft() >= testPaper.getcRight()) {
            throw new CustomException("500", "分数区间设置错误：右边界必须大于左边界");
        }

        // 验证分数区间的连续性
        if (testPaper.getaRight() + 1 != testPaper.getbLeft() ||
            testPaper.getbRight() + 1 != testPaper.getcLeft()) {
            throw new CustomException("500", "分数区间必须连续，不能有空隙或重叠");
        }

        // 验证最高分数区间是否匹配总分
        if (testPaper.getcRight() != testPaper.getScore()) {
            throw new CustomException("500", "最高分数区间的右边界必须等于试卷总分");
        }

        if (1 == testPaper.getFlag()) {
            if (ObjectUtil.isEmpty(testPaper.getIdList())) {
                throw new CustomException("500", "数据不完整，请选择具体您要出的题目");
            }
        }
    }

    public void updateById(TestPaper testPaper) {
        testPaperMapper.updateById(testPaper);
    }

    public void deleteById(Integer id) {
        // 检查权限
        TestPaper testPaper = testPaperMapper.selectById(id);
        if (testPaper == null) {
            throw new CustomException("500", "试卷不存在");
        }

        Account currentUser = TokenUtils.getCurrentUser();
        Integer userCompanyId = null;

        try {
            if (RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
                userCompanyId = adminService.getAdminCompanyId();
            } else if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
                Doctor doctor = doctorMapper.selectById(currentUser.getId());
                if (doctor != null) {
                    userCompanyId = doctor.getCompanyId();
                }
            }

            // 检查公司ID是否匹配
            if (userCompanyId != null) {
                Doctor paperDoctor = doctorMapper.selectById(testPaper.getDoctorId());
                if (paperDoctor == null || !userCompanyId.equals(paperDoctor.getCompanyId())) {
                    throw new CustomException("500", "您没有权限删除其他公司的试卷");
                }
            }

            testPaperMapper.deleteById(id);
            
        } catch (Exception e) {
            System.err.println("删除试卷时发生错误: " + e.getMessage());
            throw new CustomException("500", "删除试卷失败：" + e.getMessage());
        }
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            testPaperMapper.deleteById(id);
        }
    }

    public TestPaper selectById(Integer id) {
        TestPaper testPaper = testPaperMapper.selectById(id);
        if (testPaper == null) {
            throw new CustomException("500", "试卷不存在");
        }

        // 检查当前用户是否有权限访问该试卷
        Account currentUser = TokenUtils.getCurrentUser();
        
        // 如果是员工角色，检查试卷状态
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            if (!"审核通过".equals(testPaper.getStatus())) {
                throw new CustomException("500", "该试卷尚未通过审核，暂时无法访问");
            }
        }

        Integer userCompanyId = null;

        try {
            if (RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
                userCompanyId = adminService.getAdminCompanyId();
            } else if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
                Doctor doctor = doctorMapper.selectById(currentUser.getId());
                if (doctor != null) {
                    userCompanyId = doctor.getCompanyId();
                }
            }

            // 如果是管理员或医生，检查公司ID是否匹配
            if (userCompanyId != null) {
                Doctor paperDoctor = doctorMapper.selectById(testPaper.getDoctorId());
                if (paperDoctor == null || !userCompanyId.equals(paperDoctor.getCompanyId())) {
                    throw new CustomException("500", "您没有权限访问其他公司的试卷");
                }
            }

            List<Topic> topicList = getTopics(testPaper);
            testPaper.setTopicList(topicList);
            return testPaper;
            
        } catch (Exception e) {
            System.err.println("查询试卷详情时发生错误: " + e.getMessage());
            throw new CustomException("500", "查询试卷详情失败：" + e.getMessage());
        }
    }

    public List<TestPaper> selectAll(TestPaper testPaper) {
        // 如果是员工角色，需要根据员工所属公司过滤试卷
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            // 获取员工所属公司ID
            Employee employee = employeeService.selectById(currentUser.getId());
            if (employee != null && employee.getCompanyId() != null) {
                testPaper.setCompanyId(employee.getCompanyId());
            }
        }
        
        // 如果是管理员或医生，保持原有的公司过滤逻辑
        else if (RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            Integer companyId = adminService.getAdminCompanyId();
            if (companyId != null) {
                testPaper.setCompanyId(companyId);
            }
        } else if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
            Doctor doctor = doctorMapper.selectById(currentUser.getId());
            if (doctor != null) {
                testPaper.setCompanyId(doctor.getCompanyId());
            }
        }

        return testPaperMapper.selectAll(testPaper);
    }

    public PageInfo<TestPaper> selectPage(TestPaper testPaper, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        
        try {
            // 获取当前用户的公司ID
            Integer companyId = null;
            
            if (RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
                companyId = adminService.getAdminCompanyId();
                if (companyId == null) {
                    throw new CustomException("500", "无法确定管理员所属公司");
                }
            } else if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
                testPaper.setDoctorId(currentUser.getId());
                Doctor doctor = doctorMapper.selectById(currentUser.getId());
                if (doctor != null) {
                    companyId = doctor.getCompanyId();
                }
                if (companyId == null) {
                    throw new CustomException("500", "无法确定医生所属公司");
                }
            }

            // 设置公司ID条件（对管理员和医生生效）
            if (companyId != null) {
                testPaper.setCompanyId(companyId);
                System.out.println("正在按公司ID过滤测试问卷: " + companyId + ", 用户角色: " + currentUser.getRole());
            }

            PageHelper.startPage(pageNum, pageSize);
            List<TestPaper> list = testPaperMapper.selectAll(testPaper);
            
            // 获取每个试卷的题目列表
            for (TestPaper paper : list) {
                List<Topic> topicList = getTopics(paper);
                paper.setTopicList(topicList);
            }
            
            return PageInfo.of(list);
        } catch (Exception e) {
            System.err.println("查询测试问卷时发生错误: " + e.getMessage());
            throw new CustomException("500", "查询测试问卷失败：" + e.getMessage());
        }
    }

    private List<Topic> getTopics(TestPaper paper) {
        String ids = paper.getIds();
        List<Integer> idList = JSONUtil.toList(ids, Integer.class);
        List<Topic> topicList = new ArrayList<>();
        for (Integer id : idList) {
            Topic topic = topicMapper.selectById(id, null);
            if (ObjectUtil.isNotEmpty(topic)) {
                topicList.add(topic);
            }
        }
        return topicList;
    }

    public List<TestPaper> selectAllDesc() {
        return testPaperMapper.selectAllDesc();
    }

    /**
     * 检查测试卷是否属于指定公司
     * 
     * @param testPaperId 测试卷ID
     * @param companyId 公司ID
     * @return 如果测试卷属于指定公司，则返回true；否则返回false
     */
    public boolean belongsToCompany(Integer testPaperId, Integer companyId) {
        if (testPaperId == null || companyId == null) {
            return false;
        }
        
        // 获取测试卷
        TestPaper testPaper = testPaperMapper.selectById(testPaperId);
        if (testPaper == null) {
            return false;
        }
        
        // 如果测试卷有公司ID字段，直接比较
        if (testPaper.getCompanyId() != null) {
            return testPaper.getCompanyId().equals(companyId);
        }
        
        // 如果测试卷没有公司ID字段，通过医生ID关联查询
        if (testPaper.getDoctorId() != null) {
            // 检查医生是否属于该公司
            return doctorService.checkDoctorCompany(testPaper.getDoctorId(), companyId);
        }
        
        return false;
    }
}
