package com.example.excel;

import cn.hutool.core.util.StrUtil;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认Excel返回对象，包含导入结果和错误信息。
 * 
 * @param <T> 数据类型
 */
public class DefaultExcelResult<T> implements ExcelResult<T> {

    /**
     * 数据对象列表
     */
    @Setter
    private List<T> list;

    /**
     * 错误信息列表
     */
    @Setter
    private List<T> errorList;

    public DefaultExcelResult() {
        this.list = new ArrayList<>(); // 初始化数据列表
        this.errorList = new ArrayList<>(); // 初始化错误列表
    }

    public DefaultExcelResult(List<T> list, List<T> errorList) {
        this.list = list; // 设置数据列表
        this.errorList = errorList; // 设置错误列表
    }

    public DefaultExcelResult(ExcelResult<T> excelResult) {
        this.list = excelResult.getList(); // 从已有结果中获取数据列表
        this.errorList = excelResult.getErrorList(); // 从已有结果中获取错误列表
    }

    @Override
    public List<T> getList() {
        return list; // 返回数据列表
    }

    @Override
    public List<T> getErrorList() {
        return errorList; // 返回错误列表
    }

    /**
     * 获取导入回执信息
     *
     * @return 导入回执信息
     */
    @Override
    public String getAnalysis() {
        int successCount = list.size(); // 成功读取的数量
        int errorCount = errorList.size(); // 错误数量
        if (successCount == 0) {
            return "读取失败，未解析到数据"; // 无数据解析
        } else {
            if (errorCount == 0) {
                return StrUtil.format("恭喜您，全部读取成功！共{}条", successCount); // 全部成功
            } else {
                return ""; // 有错误但不返回信息
            }
        }
    }
}
