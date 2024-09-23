package com.example.vo.user;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 10:56]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("登录信息")
public class Loginer {
    private String email;
    private String password;
}
