package com.example.entity;

import lombok.Data;

/**
 * 类型类，表示心理分类信息。
 */
@Data
public class Type {
    private Integer id; // 类型ID
    private String title; // 类型标题
    private Integer companyId; // 公司ID

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
