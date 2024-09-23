package com.example.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("user")
public class User {
    /**
    * 用户ID
    */
    private Long userId;

    /**
    * 用户账号
    */
    private String account;

    /**
    * 个人简介
    */
    private String description;

    /**
    * 用户昵称
    */
    private String nickName;

    /**
    * 密码
    */
    private String password;

    /**
    * 邮箱
    */
    private String email;

    /**
    * 手机号
    */
    private String phone;

    /**
    * 用户状态，(0正常  1封禁)
    */
    private Integer status;

    /**
    * 主页网址
    */
    private String website;


}