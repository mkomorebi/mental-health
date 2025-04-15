package com.example.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Excel相关处理工具类，提供Excel的导入、导出、样式设置等功能。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelUtil {

    /**
     * 默认样式策略
     *
     * @return 默认的样式策略
     */
    public static HorizontalCellStyleStrategy defaultStylePolicyPolicy() {
        // 头的样式策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex()); // 设置背景色
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER); // 设置水平对齐方式
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 设置垂直对齐方式

        // 设置字体
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontName("宋体");
        headWriteCellStyle.setWriteFont(headWriteFont);

        // 内容的样式策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER); // 设置水平对齐方式
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 设置垂直对齐方式

        // 设置字体
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontName("宋体");
        contentWriteCellStyle.setWriteFont(contentWriteFont);

        // 返回头和内容的样式策略
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    /**
     * 同步导入Excel（适用于小数据量）
     *
     * @param is 输入流
     * @param clazz 数据类型
     * @return 转换后的数据集合
     */
    public static <T> List<T> importExcel(InputStream is, Class<T> clazz) {
        return EasyExcel.read(is).head(clazz).autoCloseStream(false).sheet().doReadSync(); // 读取Excel数据
    }

    /**
     * 使用校验监听器异步导入Excel
     * 
     * @param data 模板需要的数据
     * @param os 输出流
     */
    public static void exportTemplateMultiList(Map<String, Object> data, String templatePath, OutputStream os) {
        ClassPathResource templateResource = new ClassPathResource(templatePath);
        ExcelWriter excelWriter = EasyExcel.write(os)
                .withTemplate(templateResource.getStream())
                .autoCloseStream(false)
                .registerConverter(new ExcelBigNumberConvert()) // 注册大数值转换器
                .build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        if (CollUtil.isEmpty(data)) {
            throw new IllegalArgumentException("数据为空"); // 数据为空异常
        }
        for (Map.Entry<String, Object> map : data.entrySet()) {
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build(); // 设置填充配置
            if (map.getValue() instanceof Collection) {
                // 多表导出必须使用 FillWrapper
                excelWriter.fill(new FillWrapper(map.getKey(), (Collection<?>) map.getValue()), fillConfig, writeSheet);
            } else {
                excelWriter.fill(map.getValue(), writeSheet);
            }
        }
        excelWriter.finish(); // 完成写入
    }

    /**
     * 重置响应体
     *
     * @param fileName 文件名
     * @param response HttpServletResponse对象
     * @throws UnsupportedEncodingException 编码异常
     */
    public static void resetResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        FileUtils.setAttachmentResponseHeader(response, encodingFilename(fileName)); // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"); // 设置内容类型
    }

    /**
     * 编码文件名
     *
     * @param filename 真实文件名
     * @return 编码后的文件名
     */
    public static String encodingFilename(String filename) {
        return IdUtil.fastSimpleUUID() + "_" + filename + ".xlsx"; // 返回编码后的文件名
    }

    /**
     * 格式化Excel sheet表命名规则：
     * 不多于31个字符，不包含特定字符，首尾不能是单引号
     *
     * @param name sheet名称
     * @return 格式化后的sheet名称
     */
    public static String formatSheetName(String name) {
        if (ObjectUtil.isEmpty(name)) {
            return "_"; // 返回默认值
        }
        String invalidChars = "\\/?*:[]'"; // 无效字符
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (invalidChars.indexOf(c) != -1) {
                result.append('_'); // 替换无效字符
            } else {
                result.append(c);
            }
        }
        if (result.length() > 31) {
            result.setLength(31); // 限制长度
        }

        if (result.charAt(0) == '\'') {
            result.insert(0, '_'); // 处理首字符
        }

        if (result.charAt(result.length() - 1) == '\'') {
            result.append('_'); // 处理尾字符
        }

        return result.toString(); // 返回格式化后的名称
    }

    /**
     * 导出Excel数据
     *
     * @param list     导出数据集合
     * @param sheetName 工作表名称
     * @param clazz    实体类
     * @param response 响应对象
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, HttpServletResponse response) {
        try {
            resetResponse(sheetName, response);
            ServletOutputStream out = response.getOutputStream();
            ExcelWriterSheetBuilder writerSheet = EasyExcel.write(out, clazz)
                .registerWriteHandler(defaultStylePolicyPolicy())
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet(formatSheetName(sheetName));
            
            writerSheet.doWrite(list);
        } catch (IOException e) {
            throw new RuntimeException("导出Excel异常", e);
        }
    }
}
