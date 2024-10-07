package com.example.vo;

import lombok.Data;

/**
 * [更新用户密码]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/7 13:45]
 */
@Data
public class PasswordVo {
    private String oldPassword;
    private String newPassword;
}
