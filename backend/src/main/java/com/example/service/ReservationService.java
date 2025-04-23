package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Employee;
import com.example.entity.Reservation;
import com.example.entity.Doctor;
import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.mapper.ReservationMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Objects;
import java.util.ArrayList;

/**
 * ReservationService 业务层，用于处理预约信息相关的业务逻辑。
 * 提供对 Reservation 的增、删、改、查等操作。
 */
@Service
public class ReservationService {

    @Resource
    private ReservationMapper reservationMapper;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private DoctorService doctorService;

    @Resource
    private AdminService adminService;

    /**
     * 添加新的 Reservation 记录。
     *
     * @param reservation Reservation 实体
     */
    public void add(Reservation reservation) {
        Account currentUser = TokenUtils.getCurrentUser();
        Employee employee = employeeService.selectById(currentUser.getId());
        Doctor doctor = doctorService.selectById(reservation.getDoctorId());
        
        // 检查医生是否属于同一公司且状态为审批通过
        if (doctor == null || !doctor.getCompanyId().equals(employee.getCompanyId()) 
            || !"审批通过".equals(doctor.getStatus())) {
            throw new CustomException("500", "只能预约本公司审批通过的医生");
        }
        
        // 检查时间段是否已被预约
        List<Reservation> existingReservations = reservationMapper.selectByDoctorIdAndTimeRange(
            reservation.getDoctorId(), 
            reservation.getStart(), 
            reservation.getEnd()
        );
        
        if (!existingReservations.isEmpty()) {
            throw new CustomException("500", "该时间段已被预约");
        }

        // 检查用户是否已有待审核或已通过的预约
        List<Reservation> userReservations = reservationMapper.selectByUserIdAndDoctorId(
            currentUser.getId(), 
            reservation.getDoctorId()
        );
        
        long activeReservations = userReservations.stream()
            .filter(r -> "待审核".equals(r.getStatus()) || "审核通过".equals(r.getStatus()))
            .count();
            
        if (activeReservations > 0) {
            throw new CustomException("500", "您已经预约过该医生，请耐心等待审核");
        }

        reservation.setUserId(currentUser.getId());
        reservation.setTime(DateUtil.now());
        reservation.setStatus("待审核");
        
        reservationMapper.insert(reservation);
    }

    public void updateById(Reservation reservation) {
        reservationMapper.updateById(reservation);
    }

    public void deleteById(Integer id) {
        reservationMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            reservationMapper.deleteById(id);
        }
    }

    public Reservation selectById(Integer id) {
        return reservationMapper.selectById(id);
    }

    public List<Reservation> selectAll(Reservation reservation) {
        System.out.println("ReservationService.selectAll 查询条件: " + 
                          "doctorId=" + reservation.getDoctorId() + 
                          ", status=" + reservation.getStatus() + 
                          ", userId=" + reservation.getUserId());
        List<Reservation> result = reservationMapper.selectAll(reservation);
        System.out.println("查询结果数量: " + (result != null ? result.size() : 0));
        return result;
    }

    public PageInfo<Reservation> selectPage(Reservation reservation, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        
        // 获取当前用户所属公司ID
        Integer companyId = null;
        if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
            Doctor doctor = doctorService.selectById(currentUser.getId());
            companyId = doctor.getCompanyId();
            reservation.setDoctorId(currentUser.getId());
        } else if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            Employee employee = employeeService.selectById(currentUser.getId());
            companyId = employee.getCompanyId();
            reservation.setUserId(currentUser.getId());
        } else if (RoleEnum.ADMIN.name().equals(currentUser.getRole())) {
            Admin admin = adminService.selectById(currentUser.getId());
            companyId = admin.getCompanyId();
        }
        
        // 设置公司ID用于查询
        reservation.setCompanyId(companyId);
        
        // 日期范围和用户名参数已经在Controller中设置到reservation对象中
        
        PageHelper.startPage(pageNum, pageSize);
        List<Reservation> list = reservationMapper.selectAll(reservation);
        return PageInfo.of(list);
    }

    /**
     * 获取医生的已审核通过的预约患者列表
     * @param doctorId 医生ID
     * @return 患者信息列表
     */
    public List<Map<String, Object>> getApprovedPatientsByDoctorId(Integer doctorId) {
        return reservationMapper.selectApprovedPatientsByDoctorId(doctorId);
    }

    /**
     * 根据医生ID和时间范围查询预约
     * @param doctorId 医生ID
     * @param start 开始时间
     * @param end 结束时间
     * @return 预约列表
     */
    public List<Reservation> selectByDoctorIdAndTimeRange(Integer doctorId, String start, String end) {
        return reservationMapper.selectByDoctorIdAndTimeRange(doctorId, start, end);
    }

    /**
     * 获取医生工作台统计数据
     * @param doctorId 医生ID
     * @param timeRange 时间范围（week, month, year）
     * @return 统计数据
     */
    public Map<String, Object> getDoctorDashboardStats(Integer doctorId, String timeRange) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 获取当前日期
            String today = DateUtil.today();
            System.out.println("当前日期: " + today);
            String todayStart = today + " 00:00:00";
            String todayEnd = today + " 23:59:59";
            
            // 根据时间范围确定开始和结束日期
            DateTime now = DateUtil.date();
            DateTime rangeStart;
            DateTime rangeEnd = DateUtil.endOfDay(now);
            String rangeStartStr;
            String rangeEndStr = DateUtil.format(rangeEnd, "yyyy-MM-dd HH:mm:ss");
            
            // 根据时间范围设置开始时间
            switch (timeRange) {
                case "month":
                    rangeStart = DateUtil.beginOfMonth(now);
                    break;
                case "year":
                    rangeStart = DateUtil.beginOfYear(now);
                    break;
                case "week":
                default:
                    rangeStart = DateUtil.beginOfWeek(now);
                    break;
            }
            
            rangeStartStr = DateUtil.format(rangeStart, "yyyy-MM-dd HH:mm:ss");
            
            // 查询今日预约数
            List<Reservation> todayReservations = reservationMapper.selectByDoctorIdAndTimeRange(
                doctorId, todayStart, todayEnd);
            stats.put("todayAppointments", todayReservations.size());
            
            // 查询指定时间范围内的患者数
            List<Reservation> rangeReservations = reservationMapper.selectByDoctorIdAndTimeRange(
                doctorId, rangeStartStr, rangeEndStr);
            
            // 统计不重复的患者ID
            Set<Integer> rangePatients = rangeReservations.stream()
                .map(Reservation::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            stats.put("weeklyPatients", rangePatients.size());
            
            // 获取预约状态分布
            Map<String, Integer> statusCounts = new HashMap<>();
            statusCounts.put("pending", 0);
            statusCounts.put("approved", 0);
            statusCounts.put("completed", 0);
            
            for (Reservation reservation : rangeReservations) {
                String status = reservation.getStatus();
                if ("待审核".equals(status)) {
                    statusCounts.put("pending", statusCounts.get("pending") + 1);
                } else if ("审核通过".equals(status)) {
                    statusCounts.put("approved", statusCounts.get("approved") + 1);
                } else if ("已结束".equals(status) || "已完成".equals(status)) {
                    statusCounts.put("completed", statusCounts.get("completed") + 1);
                }
            }
            stats.put("appointmentStatus", statusCounts);
            
            // 获取今日预约列表
            List<Map<String, Object>> todayAppointmentList = new ArrayList<>();
            for (Reservation reservation : todayReservations) {
                // 只显示待审核和审核通过状态的预约
                String status = reservation.getStatus();
                if (!"待审核".equals(status) && !"审核通过".equals(status)) {
                    continue; // 跳过非待审核和审核通过的预约
                }
                
                if (reservation.getUserId() != null) {
                    Employee patient = employeeService.selectById(reservation.getUserId());
                    if (patient != null) {
                        Map<String, Object> appointmentInfo = new HashMap<>();
                        
                        // 提取时间部分 (格式: "2023-05-15 09:00:00" -> "09:00")
                        String startTime = reservation.getStart();
                        String timeOnly = "未知时间";
                        if (startTime != null && startTime.length() >= 16) {
                            timeOnly = startTime.substring(11, 16);
                        }
                        
                        // 计算持续时间（分钟）
                        int duration = 45; // 默认45分钟
                        if (reservation.getStart() != null && reservation.getEnd() != null) {
                            try {
                                DateTime startDt = DateUtil.parse(reservation.getStart());
                                DateTime endDt = DateUtil.parse(reservation.getEnd());
                                long diffMinutes = DateUtil.between(startDt, endDt, DateUnit.MINUTE);
                                duration = (int) diffMinutes;
                                // 添加日志以便调试
                                System.out.println("计算持续时间: " + reservation.getStart() + " 到 " + 
                                                  reservation.getEnd() + " = " + duration + "分钟");
                            } catch (Exception e) {
                                System.err.println("计算持续时间出错: " + e.getMessage());
                                // 出错时使用默认值
                            }
                        }
                        
                        appointmentInfo.put("id", reservation.getId());
                        appointmentInfo.put("time", timeOnly);
                        appointmentInfo.put("duration", duration);
                        appointmentInfo.put("patientName", patient.getName());
                        appointmentInfo.put("patientAvatar", patient.getAvatar());
                        appointmentInfo.put("status", status);
                        
                        // 所有预约都是面对面咨询
                        appointmentInfo.put("type", "inperson");
                        appointmentInfo.put("typeText", "面对面咨询");
                        
                        todayAppointmentList.add(appointmentInfo);
                    }
                }
            }
            
            // 打印调试信息
            System.out.println("今日预约数量(全部): " + todayReservations.size());
            System.out.println("今日预约数量(待审核和审核通过): " + todayAppointmentList.size());
            System.out.println("今日预约列表: " + todayAppointmentList);
            System.out.println("预约状态分布: " + statusCounts);
            
            stats.put("recentAppointments", todayAppointmentList);
            
        } catch (Exception e) {
            e.printStackTrace();
            // 返回空数据而不是抛出异常
            stats.put("todayAppointments", 0);
            stats.put("weeklyPatients", 0);
            stats.put("appointmentStatus", Map.of("pending", 0, "approved", 0, "completed", 0));
            stats.put("recentAppointments", new ArrayList<>());
        }
        
        return stats;
    }

}
