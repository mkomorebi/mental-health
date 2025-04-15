package com.example.exception;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器，使用 @ControllerAdvice 注解。
 * 该类用于捕获和处理应用程序中的所有异常，并返回统一的错误响应。
 */
@ControllerAdvice("com.example.controller")
public class GlobalExceptionHandler {

    private static final Log log = LogFactory.get(); // 日志记录器

    /**
     * 处理所有未捕获的异常。
     *
     * @param e 捕获的异常
     * @return 统一的错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody // 返回 JSON 格式的响应
    public Result error(Exception e) {
        log.error("异常信息：", e); // 记录异常信息
        return Result.error(); // 返回统一的错误响应
    }

    /**
     * 处理自定义异常。
     *
     * @param e 捕获的自定义异常
     * @return 统一的错误响应，包含异常代码和信息
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody // 返回 JSON 格式的响应
    public Result error(CustomException e) {
        // log.error("异常信息：", e); // 可选：记录自定义异常信息
        return Result.error(e.getCode(), e.getMsg()); // 返回包含异常代码和信息的错误响应
    }
}
