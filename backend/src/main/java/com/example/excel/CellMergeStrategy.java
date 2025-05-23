package com.example.excel;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 列值重复合并策略，用于在Excel中合并相同值的单元格。
 * <p>
 * 该策略会在写入Excel时自动合并相同的列值。
 * </p>
 */
@Slf4j
public class CellMergeStrategy extends AbstractMergeStrategy {

    private final List<CellRangeAddress> cellList; // 存储合并单元格的列表
    private final boolean hasTitle; // 是否包含标题
    private int rowIndex; // 行索引

    public CellMergeStrategy(List<?> list, boolean hasTitle) {
        this.hasTitle = hasTitle;
        this.rowIndex = hasTitle ? 1 : 0; // 行合并开始下标
        this.cellList = handle(list, hasTitle); // 处理合并逻辑
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        if (CollUtil.isNotEmpty(cellList)) {
            if (cell.getRowIndex() == rowIndex && cell.getColumnIndex() == 0) {
                for (CellRangeAddress item : cellList) {
                    sheet.addMergedRegion(item); // 添加合并区域
                }
            }
        }
    }

    @SneakyThrows
    private List<CellRangeAddress> handle(List<?> list, boolean hasTitle) {
        List<CellRangeAddress> cellList = new ArrayList<>();
        if (CollUtil.isEmpty(list)) {
            return cellList; // 如果列表为空，返回空列表
        }
        Field[] fields = ReflectUtils.getFields(list.get(0).getClass(), field -> !"serialVersionUID".equals(field.getName()));

        List<Field> mergeFields = new ArrayList<>(); // 存储需要合并的字段
        List<Integer> mergeFieldsIndex = new ArrayList<>(); // 存储字段索引
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(CellMerge.class)) {
                CellMerge cm = field.getAnnotation(CellMerge.class);
                mergeFields.add(field);
                mergeFieldsIndex.add(cm.index() == -1 ? i : cm.index());
                if (hasTitle) {
                    ExcelProperty property = field.getAnnotation(ExcelProperty.class);
                    rowIndex = Math.max(rowIndex, property.value().length); // 更新行索引
                }
            }
        }

        Map<Field, RepeatCell> map = new HashMap<>(); // 存储重复单元格信息
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < mergeFields.size(); j++) {
                Field field = mergeFields.get(j);
                Object val = ReflectUtils.invokeGetter(list.get(i), field.getName());

                int colNum = mergeFieldsIndex.get(j);
                if (!map.containsKey(field)) {
                    map.put(field, new RepeatCell(val, i)); // 添加新单元格
                } else {
                    RepeatCell repeatCell = map.get(field);
                    Object cellValue = repeatCell.getValue();
                    if (cellValue == null || "".equals(cellValue)) {
                        continue; // 跳过空值
                    }
                    if (!cellValue.equals(val)) {
                        if (i - repeatCell.getCurrent() > 1) {
                            cellList.add(new CellRangeAddress(repeatCell.getCurrent() + rowIndex, i + rowIndex - 1, colNum, colNum)); // 添加合并区域
                        }
                        map.put(field, new RepeatCell(val, i)); // 更新单元格
                    } else if (i == list.size() - 1) {
                        if (i > repeatCell.getCurrent()) {
                            cellList.add(new CellRangeAddress(repeatCell.getCurrent() + rowIndex, i + rowIndex, colNum, colNum)); // 添加合并区域
                        }
                    }
                }
            }
        }
        return cellList; // 返回合并单元格列表
    }

    @Data
    @AllArgsConstructor
    static class RepeatCell {
        private Object value; // 单元格值
        private int current; // 当前行索引
    }
}
