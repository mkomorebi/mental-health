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
}