package com.example.excel;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelDataConvertException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Excel 导入监听器，用于处理Excel导入过程中的事件。
 * <p>
 * 该监听器可以处理数据解析、异常处理等。
 * </p>
 * 
 * @param <T> 数据类型
 */
@Slf4j
@NoArgsConstructor
public class DefaultExcelListener<T> extends AnalysisEventListener<T> implements ExcelListener<T> {

    /**
     * 是否进行验证，默认为true
     */
    private Boolean isValidate = Boolean.TRUE;

    /**
     * Excel表头数据
     */
    private Map<Integer, String> headMap;

    /**
     * 导入结果
     */
    private ExcelResult<T> excelResult;

    public DefaultExcelListener(boolean isValidate) {
        this.excelResult = new DefaultExcelResult<>(); // 初始化导入结果
        this.isValidate = isValidate; // 设置验证标志
    }

    /**
     * 处理异常
     *
     * @param exception ExcelDataConvertException
     * @param context   Excel上下文
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        String errMsg = null;
        if (exception instanceof ExcelDataConvertException) {
            // 如果是单元格转换异常，获取具体行号
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            Integer rowIndex = excelDataConvertException.getRowIndex();
            Integer columnIndex = excelDataConvertException.getColumnIndex();
            errMsg = StrUtil.format("第{}行-第{}列-表头{}: 解析异常<br/>",
                rowIndex + 1, columnIndex + 1, headMap.get(columnIndex));
            if (log.isDebugEnabled()) {
                log.error(errMsg); // 记录错误信息
            }
        }
        excelResult.getErrorList().add((T) errMsg); // 添加错误信息到结果列表
        throw new ExcelAnalysisException(errMsg); // 抛出异常
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap; // 保存表头数据
        log.debug("解析到一条表头数据: {}", JSONUtil.toJsonStr(headMap));
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        excelResult.getList().add(data); // 添加解析到的数据
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.debug("所有数据解析完成！"); // 解析完成日志
    }

    @Override
    public ExcelResult<T> getExcelResult() {
        return excelResult; // 返回导入结果
    }
}
