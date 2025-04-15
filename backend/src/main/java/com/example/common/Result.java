package com.example.common;

/**
 * 结果类，用于统一返回API的响应结果。
 */
public class Result {

    private String code; // 响应状态码
    private String msg; // 响应消息
    private Object data; // 响应数据

    public Result() {
        // 默认构造函数
    }

    // 成功响应
    public static Result success() {
        Result result = new Result();
        result.setCode("200");
        result.setMsg("请求成功");
        return result;
    }

    // 成功响应，带数据
    public static Result success(Object data) {
        Result result = success();
        result.setData(data);
        return result;
    }

    // 错误响应
    public static Result error() {
        Result result = new Result();
        result.setCode("500");
        result.setMsg("系统异常");
        return result;
    }

    // 错误响应，带状态码和消息
    public static Result error(String code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    // 错误响应，带消息
    public static Result error(String msg) {
        Result result = new Result();
        result.setCode("500");
        result.setMsg(msg);
        return result;
    }

    // Getter和Setter方法
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
