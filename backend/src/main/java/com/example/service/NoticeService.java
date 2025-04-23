package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Notice;
import com.example.mapper.NoticeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.example.utils.TokenUtils;

import java.util.List;

/**
 * NoticeService 业务层，用于处理与 Notice 实体相关的业务逻辑。
 * 提供对 Notice 的增、删、改、查等操作。
 */
@Service
public class NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private AdminService adminService;

    /**
     * 添加新的 Notice 记录。
     *
     * @param notice Notice 实体
     */
    public void add(Notice notice) {
        notice.setTime(DateUtil.now());
        noticeMapper.insert(notice);
    }

    public void updateById(Notice notice) {
        noticeMapper.updateById(notice);
    }

    public void deleteById(Integer id) {
        noticeMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            noticeMapper.deleteById(id);
        }
    }

    /**
     * 根据ID查询公告，并进行公司隔离
     */
    public Notice selectById(Integer id) {
        Notice notice = noticeMapper.selectById(id);
        
        // 获取当前用户信息
        String token = TokenUtils.getToken();
        if (token != null && !token.isEmpty()) {
            try {
                String audience = JWT.decode(token).getAudience().get(0);
                if (audience != null && audience.contains("-")) {
                    String role = audience.split("-")[1];
                    String userId = audience.split("-")[0];
                    
                    // 如果是员工，需要验证公告是否属于员工所在公司
                    if ("USER".equals(role) && notice != null) {
                        Integer employeeId = Integer.valueOf(userId);
                        Integer employeeCompanyId = employeeService.getEmployeeCompanyId(employeeId);
                        
                        // 获取公告所属公司ID
                        Integer noticeCompanyId = adminService.getAdminCompanyId(notice.getAdminId());
                        
                        // 如果员工公司ID与公告公司ID不匹配，返回null或抛出异常
                        if (employeeCompanyId == null || noticeCompanyId == null || 
                            !employeeCompanyId.equals(noticeCompanyId)) {
                            return null; // 或抛出无权限异常
                        }
                    }
                }
            } catch (Exception e) {
                // 记录异常，但不影响请求继续处理
                System.err.println("解析token失败: " + e.getMessage());
            }
        }
        
        return notice;
    }

    public List<Notice> selectAll(Notice notice) {
        return noticeMapper.selectAll(notice);
    }

    public PageInfo<Notice> selectPage(Notice notice, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> list = noticeMapper.selectAll(notice);
        return PageInfo.of(list);
    }

}
