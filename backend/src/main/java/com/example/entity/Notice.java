package com.example.entity;

/**
 * 通知类，表示系统中的通知信息。
 */
public class Notice {
    private Integer id; // 通知ID
    private String title; // 通知标题
    private String content; // 通知内容
    private String time; // 通知时间

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
