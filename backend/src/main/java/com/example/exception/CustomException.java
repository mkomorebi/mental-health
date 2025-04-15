package com.example.exception;

import com.example.common.enums.ResultCodeEnum;

/**
 * 自定义异常类，用于表示业务逻辑中的异常情况。
 * 该类扩展了 RuntimeException，允许在运行时抛出异常。
 */
public class CustomException extends RuntimeException {

    private String code; // 异常代码
    private String msg;  // 异常信息

    /**
     * 构造函数，接受异常代码和信息。
     *
     * @param code 异常代码
     * @param msg  异常信息
     */
    public CustomException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造函数，接受 ResultCodeEnum 枚举类型，自动获取代码和信息。
     *
     * @param resultCodeEnum 结果代码枚举
     */
    public CustomException(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.code;
        this.msg = resultCodeEnum.msg;
    }

    /**
     * 带有错误码和自定义消息的异常构造函数
     * @param resultCodeEnum 结果码枚举
     * @param message 自定义错误消息
     */
    public CustomException(ResultCodeEnum resultCodeEnum, String message) {
        super(message);
        this.code = resultCodeEnum.getCode();
        this.msg = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
