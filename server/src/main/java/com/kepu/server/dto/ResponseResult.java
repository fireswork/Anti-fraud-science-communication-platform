package com.kepu.server.dto;

import lombok.Data;

@Data
public class ResponseResult {
    
    private Integer code;
    private String message;
    private Object data;
    
    // 添加无参构造函数
    public ResponseResult() {
    }
    
    // 添加全参构造函数
    public ResponseResult(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    // 成功响应，带数据
    public static ResponseResult success(Object data) {
        return new ResponseResult(200, "操作成功", data);
    }
    
    // 成功响应，带消息和数据
    public static ResponseResult success(String message, Object data) {
        return new ResponseResult(200, message, data);
    }
    
    // 失败响应，无数据
    public static ResponseResult fail(Integer code, String message) {
        return new ResponseResult(code, message, null);
    }
    
    // 错误响应，无数据
    public static ResponseResult error(Integer code, String message) {
        return new ResponseResult(code, message, null);
    }
}