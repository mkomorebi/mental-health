package com.example.excel;

import java.util.List;

/**
 * Excel返回对象接口，定义了获取导入结果和错误信息的方法。
 * 
 * @param <T> 数据类型
 */
public interface ExcelResult<T> {

    /**
     * 获取对象列表
     *
     * @return 对象列表
     */
    List<T> getList();

    /**
     * 获取错误列表
     *
     * @return 错误列表
     */
    List<T> getErrorList();

    /**
     * 获取导入回执信息
     *
     * @return 导入回执
     */
    String getAnalysis();
}
