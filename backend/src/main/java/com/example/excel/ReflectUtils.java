package com.example.excel;

import cn.hutool.core.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 反射工具类，提供调用getter/setter方法、访问私有变量、调用私有方法等功能。
 * <p>
 * 该工具类简化了反射操作，方便在Excel导入导出时使用。
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) // 私有构造函数，防止实例化
public class ReflectUtils extends ReflectUtil {

    private static final String SETTER_PREFIX = "set"; // Setter方法前缀
    private static final String GETTER_PREFIX = "get"; // Getter方法前缀

    /**
     * 调用Getter方法，支持多级属性访问。
     *
     * @param obj          对象
     * @param propertyName 属性名称
     * @param <E>         返回类型
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtils.split(propertyName, ".")) {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name); // 生成Getter方法名
            object = invoke(object, getterMethodName); // 调用Getter方法
        }
        return (E) object; // 返回属性值
    }

    /**
     * 调用Setter方法，支持多级属性访问。
     *
     * @param obj          对象
     * @param propertyName 属性名称
     * @param value        设置的值
     * @param <E>         值的类型
     */
    public static <E> void invokeSetter(Object obj, String propertyName, E value) {
        Object object = obj;
        String[] names = StringUtils.split(propertyName, ".");
        for (int i = 0; i < names.length; i++) {
            if (i < names.length - 1) {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names[i]); // 生成Getter方法名
                object = invoke(object, getterMethodName); // 调用Getter方法
            } else {
                String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names[i]); // 生成Setter方法名
                Method method = getMethodByName(object.getClass(), setterMethodName); // 获取Setter方法
                invoke(object, method, value); // 调用Setter方法
            }
        }
    }
}
