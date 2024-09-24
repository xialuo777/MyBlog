package com.example.dto;

import com.example.enums.ResponseCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [响应结果]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 11:06]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("响应结果")
public class ResponseResult<T> {
    @ApiModelProperty(value = "状态码")
    private String code;
    @ApiModelProperty(value = "返回信息")
    private String msg;
    @ApiModelProperty(value = "返回数据")
    private T data;

    public static <T> ResponseResult<T> success(T data){
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(ResponseCodeEnum.SUCCESS.getCode());
        responseResult.setMsg(ResponseCodeEnum.SUCCESS.getMsg());
        responseResult.setData(data);
        return responseResult;
    }

    public static <T> ResponseResult<T> fail(String code, String msg){
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(code);
        responseResult.setMsg(msg);
        return responseResult;
    }
    public static <T> ResponseResult<T> fail(String msg){
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setMsg(msg);
        responseResult.setCode(ResponseCodeEnum.ERROR.getCode());
        return responseResult;
    }
}
