package com.example.controller;

import com.example.common.Result;
import com.example.entity.Reservation;
import com.example.service.ReservationService;
import com.example.service.UserService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * 预约控制器，处理与预约信息相关的请求。
 */
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Resource
    private ReservationService reservationService;

    @Resource
    private UserService userService;

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
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Reservation> pageInfo = reservationService.selectPage(reservation, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 获取医生的可用时间段
     */
    @GetMapping("/availableTimes")
    public Result getAvailableTimes(@RequestParam Integer doctorId) {
        // 这里应该从数据库或服务中获取医生的实际可用时间
        // 为了演示，我们返回一些模拟的时间段
        List<String> availableTimes = generateAvailableTimes(doctorId);
        return Result.success(availableTimes);
    }
    
    /**
     * 生成模拟的可用时间段
     * 实际应用中，这应该从数据库中查询医生的排班和已有预约
     */
    private List<String> generateAvailableTimes(Integer doctorId) {
        List<String> times = new ArrayList<>();
        
        // 获取当前日期
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate tomorrow = today.plusDays(1);
        java.time.LocalDate dayAfterTomorrow = today.plusDays(2);
        
        // 添加一些模拟的时间段
        times.add(today + " 09:00:00 - " + today + " 10:00:00");
        times.add(today + " 14:00:00 - " + today + " 15:00:00");
        times.add(today + " 16:00:00 - " + today + " 17:00:00");
        times.add(tomorrow + " 09:00:00 - " + tomorrow + " 10:00:00");
        times.add(tomorrow + " 14:00:00 - " + tomorrow + " 15:00:00");
        times.add(dayAfterTomorrow + " 10:00:00 - " + dayAfterTomorrow + " 11:00:00");
        
        return times;
    }

    /**
     * 获取医生的已审核通过的预约患者列表
     */
    @GetMapping("/getApprovedPatients")
    public Result getApprovedPatients(@RequestParam Integer doctor_id) {
        try {
            // 查询该医生的所有审核通过的预约
            List<Map<String, Object>> approvedPatients = reservationService.getApprovedPatientsByDoctorId(doctor_id);
            return Result.success(approvedPatients);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}
