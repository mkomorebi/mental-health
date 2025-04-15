package com.example.excel;

import java.lang.annotation.*;

/**
 * Excel 列单元格合并注解，用于合并列中相同的项。
 * <p>
 * 需搭配 {@link CellMergeStrategy} 策略使用。
 * </p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CellMerge {

	/**
	 * 列索引，默认为-1表示未指定。
	 * 
	 * @return 列索引
	 */
	int index() default -1;

}
