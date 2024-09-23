package com.example.vo.user;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

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
    @Email(message = "邮箱格式不正确")
    private String email;
    @NotNull(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码长度必须在6-18之间")
    private String password;
}
