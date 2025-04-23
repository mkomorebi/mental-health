package com.example.entity.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 自动生成getter、setter、toString、equals和hashCode方法
@Builder // 支持构建者模式，方便创建对象
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
public class MetricResp {
    private String name;        // 部门名称
    private Double deptScore;   // 部门总分
    private Integer userNum;    // 部门总用户数量
    private Integer testedUserNum; // 参与测试的用户数量
    private Integer testCount;  // 测试总次数
    private Double avgScore;    // 平均分数
    
    // 计算人均测试次数
    public Double getTestsPerUser() {
        return testedUserNum > 0 ? (double) testCount / testedUserNum : 0.0;
    }
    
    // 计算测试参与率
    public Double getParticipationRate() {
        return userNum > 0 ? (double) testedUserNum / userNum : 0.0;
    }
    
    // 获取健康状态
    public String getHealthStatus() {
        if (avgScore >= 80) return "健康";
        if (avgScore >= 60) return "亚健康";
        return "不健康";
    }
}
