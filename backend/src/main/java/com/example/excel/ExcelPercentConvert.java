package com.example.excel;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Excel百分比转换器，将数值转换为百分比格式。
 * <p>
 * 该转换器在读取和写入Excel时自动处理数值与百分比之间的转换。
 * </p>
 */
@Slf4j
public class ExcelPercentConvert implements Converter<Object> {

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class; // 支持的Java类型
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null; // 不支持特定的Excel单元格类型
    }

    @Override
    public BigDecimal convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new BigDecimal(cellData.getData().toString()); // 将单元格数据转换为BigDecimal
    }

    @Override
    public WriteCellData<Object> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(object)) {
            return new WriteCellData<>(""); // 如果对象为空，返回空单元格
        }
        String value = Convert.toStr(object);
        BigDecimal decimal = new BigDecimal(value);
        BigDecimal percentage = decimal.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP); // 转换为百分比
        return new WriteCellData<>(percentage.toString() + "%"); // 返回带百分号的字符串
    }
}
