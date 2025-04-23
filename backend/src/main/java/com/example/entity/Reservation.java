package com.example.entity;

import java.util.List;

/**
 * 预约类，表示用户的预约信息。
 */
public class Reservation {
    private Integer id; // 预约ID
    private Integer userId; // 用户ID，对应数据库中的 employee_id
    private Integer doctorId; // 医生ID
    private String start; // 预约开始时间
    private String end; // 预约结束时间
    private String time; // 预约时间
    private String question; // 预约问题
    private String status; // 预约状态
    private String reason; // 取消原因

    private List<String> timeRange; // 可选时间范围

    private String userName; // 用户姓名
    private String doctorName; // 医生姓名

    private Integer companyId; // 添加公司ID字段
    
    // 添加查询用的日期范围字段
    private String startDate; // 开始日期
    private String endDate; // 结束日期

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public List<String> getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(List<String> timeRange) {
        this.timeRange = timeRange;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
