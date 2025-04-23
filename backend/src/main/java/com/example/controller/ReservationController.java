package com.example.controller;

import com.example.common.Result;
import com.example.entity.Employee;
import com.example.entity.Reservation;
import com.example.service.ReservationService;
import com.example.service.EmployeeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import com.example.entity.Account;
import com.example.utils.TokenUtils;
import java.util.Arrays;

/**
 * 预约控制器，处理与预约信息相关的请求。
 */
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Resource
    private ReservationService reservationService;

    @Resource
    private EmployeeService employeeService;

    /**
     * 新增预约信息
     */
    @PostMapping("/add")
    public Result add(@RequestBody Reservation reservation) {
        // 设置初始状态为待审核
        if (reservation.getStatus() == null) {
            reservation.setStatus("待审核");
        }
        
        reservationService.add(reservation);
        return Result.success();
    }

    /**
     * 修改预约信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Reservation reservation) {
        reservationService.updateById(reservation);
        return Result.success();
    }

    /**
     * 删除单个预约信息
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        reservationService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除预约信息
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        reservationService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询单个预约信息
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Reservation reservation = reservationService.selectById(id);
        return Result.success(reservation);
    }

    /**
     * 查询所有预约信息
     */
    @GetMapping("/selectAll")
    public Result selectAll(Reservation reservation) {
        List<Reservation> list = reservationService.selectAll(reservation);
        return Result.success(list);
    }

    /**
     * 分页查询预约信息
     */
    @GetMapping("/selectPage")
    public Result selectPage(Reservation reservation,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(required = false) String userName,
                             @RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate) {
        // 设置额外的查询参数
        if (userName != null && !userName.isEmpty()) {
            reservation.setUserName(userName);
        }
        
        // 设置日期范围参数
        if (startDate != null && !startDate.isEmpty()) {
            reservation.setStartDate(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            reservation.setEndDate(endDate);
        }
        
        PageInfo<Reservation> pageInfo = reservationService.selectPage(reservation, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 获取医生的可用时间段
     */
    @GetMapping("/availableTimes")
    public Result getAvailableTimes(
        @RequestParam Integer doctorId,
        @RequestParam(required = false) String date
    ) {
        try {
            // 获取该医生当天所有的预约
            List<Reservation> existingReservations = reservationService.selectByDoctorIdAndTimeRange(
                doctorId,
                date + " 00:00:00",
                date + " 23:59:59"
            );

            // 生成所有可能的时间段
            List<String> allTimeSlots = generateTimeSlots(date);
            
            // 过滤掉已被预约的时间段
            List<String> availableTimeSlots = allTimeSlots.stream()
                .filter(timeSlot -> {
                    String[] times = timeSlot.split(" - ");
                    String start = times[0];
                    String end = times[1];
                    
                    return existingReservations.stream()
                        .noneMatch(r -> isTimeOverlap(r.getStart(), r.getEnd(), start, end));
                })
                .collect(Collectors.toList());

            return Result.success(availableTimeSlots);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
    
    private List<String> generateTimeSlots(String date) {
        List<String> slots = new ArrayList<>();
        
        // 生成工作时间 9:00 - 17:00 的时间段，每段1小时
        int startHour = 9;
        int endHour = 17;
        
        for (int hour = startHour; hour < endHour; hour++) {
            String start = String.format("%s %02d:00:00", date, hour);
            String end = String.format("%s %02d:00:00", date, hour + 1);
            slots.add(start + " - " + end);
        }
        
        return slots;
    }

    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        return (start2.compareTo(start1) >= 0 && start2.compareTo(end1) < 0) ||
               (end2.compareTo(start1) > 0 && end2.compareTo(end1) <= 0) ||
               (start2.compareTo(start1) <= 0 && end2.compareTo(end1) >= 0);
    }

    /**
     * 获取已审核通过的预约患者列表
     */
    @GetMapping("/getApprovedPatients")
    public Result getApprovedPatients(@RequestParam(value = "doctor_id", required = true) Integer doctorId) {
        try {
            System.out.println("获取医生ID为 " + doctorId + " 的已审核通过预约患者");
            
            // 验证医生ID是否有效
            if (doctorId == null || doctorId <= 0) {
                return Result.error("400", "无效的医生ID");
            }
            
            // 创建查询条件
            Reservation query = new Reservation();
            query.setDoctorId(doctorId);
            query.setStatus("审核通过");
            
            // 查询符合条件的预约
            List<Reservation> approvedReservations = reservationService.selectAll(query);
            System.out.println("找到审核通过的预约数量: " + approvedReservations.size());
            
            // 提取患者信息
            List<Map<String, Object>> patientList = new ArrayList<>();
            for (Reservation reservation : approvedReservations) {
                // 打印完整的预约信息，用于调试
                System.out.println("预约信息: id=" + reservation.getId() + 
                                  ", userId=" + reservation.getUserId() + 
                                  ", doctorId=" + reservation.getDoctorId() + 
                                  ", status=" + reservation.getStatus());
                
                // 确保预约的用户ID不为空
                Integer userId = reservation.getUserId();
                if (userId == null) {
                    System.out.println("预约记录中用户ID为空，尝试从userName获取关联信息");
                    // 如果有userName但没有userId，尝试通过userName查找用户
                    if (reservation.getUserName() != null && !reservation.getUserName().isEmpty()) {
                        List<Employee> employees = employeeService.selectByName(reservation.getUserName());
                        if (!employees.isEmpty()) {
                            userId = employees.get(0).getId();
                            System.out.println("通过userName找到用户ID: " + userId);
                        }
                    }
                    
                    if (userId == null) {
                        System.out.println("无法确定用户ID，跳过此预约");
                        continue;
                    }
                }
                
                // 获取患者信息
                Employee patient = employeeService.selectById(userId);
                if (patient != null) {
                    Map<String, Object> patientInfo = new HashMap<>();
                    patientInfo.put("id", patient.getId());
                    patientInfo.put("name", patient.getName() != null ? patient.getName() : "未知患者");
                    patientInfo.put("phone", patient.getPhone() != null ? patient.getPhone() : "无电话");
                    patientInfo.put("reservation_time", reservation.getStart() != null ? reservation.getStart() : "");
                    patientInfo.put("reservation_id", reservation.getId()); // 添加预约ID，便于后续操作
                    patientList.add(patientInfo);
                    System.out.println("添加患者: " + patient.getName() + ", ID: " + patient.getId());
                } else {
                    System.out.println("未找到ID为 " + userId + " 的患者");
                }
            }
            
            System.out.println("返回患者列表数量: " + patientList.size());
            return Result.success(patientList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", e.getMessage());
        }
    }

    /**
     * 获取医生工作台数据
     */
    @GetMapping("/doctorDashboard")
    public Result getDoctorDashboard(@RequestParam(required = false, defaultValue = "week") String timeRange) {
        try {
            // 获取当前登录的医生ID
            Account currentUser = TokenUtils.getCurrentUser();
            if (!"DOCTOR".equals(currentUser.getRole())) {
                return Result.error("403", "只有医生可以访问此接口");
            }
            
            // 验证时间范围参数
            if (!Arrays.asList("week", "month", "year").contains(timeRange)) {
                timeRange = "week"; // 默认为周
            }
            
            Integer doctorId = currentUser.getId();
            System.out.println("获取医生工作台数据: doctorId=" + doctorId + ", timeRange=" + timeRange);
            
            Map<String, Object> dashboardData = reservationService.getDoctorDashboardStats(doctorId, timeRange);
            
            // 添加调试日志
            System.out.println("医生工作台数据: " + dashboardData);
            if (dashboardData.containsKey("recentAppointments")) {
                List<Map<String, Object>> appointments = (List<Map<String, Object>>) dashboardData.get("recentAppointments");
                System.out.println("今日预约数量: " + appointments.size());
                System.out.println("今日预约状态分布:");
                
                // 统计不同状态的预约数量
                Map<String, Integer> statusCounts = new HashMap<>();
                for (Map<String, Object> appointment : appointments) {
                    String status = (String) appointment.get("status");
                    statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
                    
                    System.out.println("预约ID: " + appointment.get("id") + 
                                      ", 患者: " + appointment.get("patientName") + 
                                      ", 时间: " + appointment.get("time") +
                                      ", 持续时间: " + appointment.get("duration") +
                                      ", 状态: " + status);
                }
                System.out.println("状态统计: " + statusCounts);
            }
            
            return Result.success(dashboardData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", e.getMessage());
        }
    }
}
