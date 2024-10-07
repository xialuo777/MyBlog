package com.example.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * [注册用户类]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/3 11:38]
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Register {

    @NotBlank(message = "用户账号不能为空")
    @Length(min = 6, max = 12, message = "账号长度为6-12位")
    private String account;

    @NotBlank(message = "用户昵称不能为空")
    private String nickName;

    @NotBlank(message = "用户密码不能为空")
    @Length(min = 6, max = 18, message = "密码长度为6-18位")
    private String password;

    @NotBlank(message = "用户密码不能为空")
    @Length(min = 6, max = 18, message = "密码长度为6-18位")
    private String checkPassword;

    @NotBlank(message = "用户邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @NotBlank
    private String phone;

    @NotBlank(message = "邮箱验证码不能为空")
    private String emailCode;

}
