package com.example.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 * [异常类]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 14:31]
 */
@Data
public class BusinessException extends RuntimeException{
    /**
     * 异常对应的返回码
     */
    private Integer code;
    /**
     * 一场对应的描述信息
     */
    private String msg;
    private HttpStatus httpStatus;
    private Object[] params;
    public BusinessException(String msg, Object... params){
        this.msg = msg;
        this.params = params;
    }
    public BusinessException(Integer code, String msg, Object... params){
        this.code = code;
        this.msg = msg;
        this.params = params;
    }
    public BusinessException(Integer code, String msg, HttpStatus httpStatus, Object... params){
        this.code = code;
        this.msg = msg;
        this.httpStatus = httpStatus;
        this.params = params;
    }

}
