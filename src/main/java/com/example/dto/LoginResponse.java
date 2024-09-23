package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [登陆回复信息类]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 11:23]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
}
