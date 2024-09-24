package com.example.enums;

/**
 * [回复信息枚举类]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 11:13]
 */
public enum ResponseCodeEnum {
    SUCCESS("200","操作成功"),
    ERROR("500","操作失败"),
    PARAM_ERROR("400","参数错误"),
    NOT_FOUND("404","未找到该资源"),
    UNAUTHORIZED("401","未授权"),
    FORBIDDEN("403","禁止访问"),
    NOT_SUPPORTED("405","不支持该请求"),
    SERVER_ERROR("500","服务器错误"),
    NOT_IMPLEMENTED("501","服务器未实现"),
    SERVICE_UNAVAILABLE("503","服务器不可用");
    private final String code;
    private final String msg;

    ResponseCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
