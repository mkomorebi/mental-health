package com.example.entity;

/**
 * 类型类，表示心理分类信息。
 */
public class Type {
    private Integer id; // 类型ID
    private String title; // 类型标题

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
}
