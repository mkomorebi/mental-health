package com.example.controller;

import com.example.common.Result;
import com.example.entity.Doctor;
import com.example.service.DoctorService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 医生控制器，处理与医生相关的请求。
 */
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Resource
    private DoctorService doctorService;

    /**
     * 新增医生
     */
    @PostMapping("/add")
    public Result add(@RequestBody Doctor doctor) {
        doctorService.add(doctor);
        return Result.success();
    }

    /**
     * 修改医生信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Doctor doctor) {
        doctorService.updateById(doctor);
        return Result.success();
    }

    /**
     * 提交医师资格证
     */
    @PutMapping("/submit")
    public Result submit(@RequestBody Doctor doctor) {
        doctorService.submit(doctor);
        return Result.success();
    }

    /**
     * 删除单个医生
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        doctorService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除医生
     */
    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        doctorService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询单个医生
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Doctor doctor = doctorService.selectById(id);
        return Result.success(doctor);
    }

    /**
     * 查询所有医生
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Doctor> list = doctorService.selectAll(new Doctor());
        return Result.success(list);
    }

    /**
     * 分页查询医生
     */
    @GetMapping("/selectPage")
    public Result selectPage(Doctor doctor,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Doctor> pageInfo = doctorService.selectPage(doctor, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 查询前4名医生
     */
    @GetMapping("/top4")
    public Result top4() {
        List<Doctor> list = doctorService.top4();
        return Result.success(list);
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
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 添加一些模拟的时间段
        times.add(formatter.format(today) + " 09:00:00 - " + formatter.format(today) + " 10:00:00");
        times.add(formatter.format(today) + " 14:00:00 - " + formatter.format(today) + " 15:00:00");
        times.add(formatter.format(today) + " 16:00:00 - " + formatter.format(today) + " 17:00:00");
        times.add(formatter.format(tomorrow) + " 09:00:00 - " + formatter.format(tomorrow) + " 10:00:00");
        times.add(formatter.format(tomorrow) + " 14:00:00 - " + formatter.format(tomorrow) + " 15:00:00");
        times.add(formatter.format(dayAfterTomorrow) + " 10:00:00 - " + formatter.format(dayAfterTomorrow) + " 11:00:00");
        
        return times;
    }
}
