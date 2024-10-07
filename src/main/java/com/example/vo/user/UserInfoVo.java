package com.example.vo.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [更新用户信息]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/7 13:40]
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long userId;

    private String account;
    private String nickName;
    private String email;
    private String phone;
}

