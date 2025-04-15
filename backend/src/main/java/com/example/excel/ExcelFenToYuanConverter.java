package com.example.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Excel分转元转换器，将分转换为元。
 * <p>
 * 该转换器在读取和写入Excel时自动处理分和元之间的转换。
 * </p>
 */
public class ExcelFenToYuanConverter implements Converter<Long> {

    // 指定转化参数的Java类型
    public ExcelFenToYuanConverter() {
    }

    @Override
    public Class<Long> supportJavaTypeKey() {
        return Long.class; // 支持的Java类型
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER; // 支持的Excel单元格类型
    }

    @Override
    public Long convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return cellData.getNumberValue().longValue(); // 将单元格数据转换为Long类型
    }

    @Override
    public WriteCellData<?> convertToExcelData(Long value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return NumberUtils.formatToCellData(new BigDecimal(String.valueOf(value)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP), contentProperty); // 将分转换为元
    }
}
