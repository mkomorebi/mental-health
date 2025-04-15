package com.example.entity;

import lombok.Data;

/**
 * 聊天记录类，表示用户的聊天记录。
 */
@Data
public class ChatLog {
    private String id; // 聊天记录ID
    private Integer userId; // 用户ID
    private String title; // 聊天标题
    private String conversation; // 聊天内容
    private String heartAnalysis; // 心理分析结果
}
