package com.example.entity.resp;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对话响应实体
 */

@Data // 自动生成getter、setter、toString、equals和hashCode方法
@Builder // 支持构建者模式，方便创建对象
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
public class ChatResp {
    private String id;
    private Integer userId;
    private String title;
    private JSONArray conversation;
    private String heartAnalysis;
}
