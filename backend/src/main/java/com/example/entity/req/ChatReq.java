package com.example.entity.req;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对话问题实体
 *
 */

@Data // 自动生成getter、setter、toString、equals和hashCode方法
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
public class ChatReq {

    private String id;
    private Integer userId;
    private String title;
    private JSONArray conversation;
    private String heartAnalysis;

}
