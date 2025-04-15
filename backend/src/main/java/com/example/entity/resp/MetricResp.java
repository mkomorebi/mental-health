package com.example.entity.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data // 自动生成getter、setter、toString、equals和hashCode方法
@Builder // 支持构建者模式，方便创建对象
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
public class MetricResp {
    private String name;
    private BigDecimal deptScore;
    private BigDecimal userNum;
    private BigDecimal avgScore;
}
