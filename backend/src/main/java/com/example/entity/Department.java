package com.example.entity;

import lombok.Data;

/**
 * 部门类，表示系统中的部门信息。
 */
@Data
public class Department {

    /**
     * 部门id
     */
    private Integer id; // 部门ID

    /**
     * 部门名称
     */
    private String name; // 部门名称
    
    /**
     * 公司ID
     */
    private Integer companyId; // 公司ID

    // Getter 和 Setter 方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}