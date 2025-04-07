package com.kepu.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseResult {
    
    @JsonProperty("code")
    private Integer code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
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
    
    // 明确的getter方法
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Object getData() {
        return data;
    }
    
    // 明确的setter方法
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}