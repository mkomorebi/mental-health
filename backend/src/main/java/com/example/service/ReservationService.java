package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Reservation;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.ReservationMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReservationService 业务层，用于处理预约信息相关的业务逻辑。
 * 提供对 Reservation 的增、删、改、查等操作。
 */
@Service
public class ReservationService {

    @Resource
    private ReservationMapper reservationMapper;

    @Resource
    private UserService userService;

    /**
     * 添加新的 Reservation 记录。
     *
     * @param reservation Reservation 实体
     */
    public void add(Reservation reservation) {
        // 不使用 timeRange 字段，直接使用 start 和 end
        // 如果需要，可以在这里添加其他业务逻辑
        
        Account currentUser = TokenUtils.getCurrentUser();
        reservation.setUserId(currentUser.getId());
        reservation.setTime(DateUtil.now());
        reservation.setStatus("待审核");
        // 做一下数据的校验：同一个用户预约同一个医生这种场景，在以下情况下不允许重复预约。
        // 该用户预约过该医生，但是预约记录里面的状态是"待审核"或者"审核通过"（反之："已结束"或者"审核拒绝"可预约）
        List<Reservation> list = reservationMapper.selectByUserIdAndDoctorId(currentUser.getId(), reservation.getDoctorId());
        long count = list.stream().filter(v -> "待审核".equals(v.getStatus()) || "审核通过".equals(v.getStatus())).count();
        if (count > 0) {
            throw new CustomException("500", "您已经预约过该医生，请耐心等待该医生审核");
        }

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
        return reservationMapper.selectAll(reservation);
    }

    public PageInfo<Reservation> selectPage(Reservation reservation, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.DOCTOR.name().equals(currentUser.getRole())) {
            reservation.setDoctorId(currentUser.getId());
        }
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            reservation.setUserId(currentUser.getId());
        }
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
        // 创建查询条件
        Reservation query = new Reservation();
        query.setDoctorId(doctorId);
        query.setStatus("审核通过"); // 注意：这里使用"审核通过"而不是"已通过"
        
        // 查询符合条件的预约
        List<Reservation> approvedReservations = selectAll(query);
        
        // 提取患者信息
        List<Map<String, Object>> patientList = new ArrayList<>();
        for (Reservation reservation : approvedReservations) {
            Map<String, Object> patient = new HashMap<>();
            Integer userId = reservation.getUserId();
            patient.put("id", userId);
            patient.put("name", reservation.getUserName());
            
            // 通过userId查询User表获取电话信息
            User user = userService.selectById(userId);
            if (user != null) {
                patient.put("phone", user.getPhone());
            } else {
                patient.put("phone", "");
            }
            
            patient.put("reservation_time", reservation.getTime());
            patientList.add(patient);
        }
        
        return patientList;
    }

}
