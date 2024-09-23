package com.example.exception;

import com.example.dto.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * [统一异常管理]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 14:45]
 */
@Slf4j
public class ExceptionAdvice {
    /**
     *
     * @param ex 参数异常类型
     * @return ResponseResult
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseResult<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>(1);
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseResult.fail(errors.toString());
    }


    /**
     * 自定义异常统一管理
     * @param e 错误异常类型
     * @return ResponseResult
     */
    @ExceptionHandler(value= BusinessException.class)
    public ResponseResult<Object> handleBusinessException(BusinessException e){
        log.error("错误：",e);
        return ResponseResult.fail(e.getCode(),e.getMessage());
    }

}
