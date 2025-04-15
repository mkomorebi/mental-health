package com.example.excel;

import com.alibaba.excel.read.listener.ReadListener;

/**
 * Excel导入监听器接口，提供获取导入结果的方法。
 * 
 * @param <T> 数据类型
 */
public interface ExcelListener<T> extends ReadListener<T> {

    /**
     * 获取Excel导入结果
     *
     * @return 导入结果
     */
    ExcelResult<T> getExcelResult();
}
