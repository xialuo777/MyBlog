package com.example.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * [常量类]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 15:02]
 */

public class Constant {
    public static final String USER_MAP_KEY_ID = "id";
    public static final String USER_MAP_KEY_ACCOUNT = "account";
    public static final String USER_MAP_KEY_NICK_NAME = "nickName";
    public static final String REDIS_NAME_SPACE="user";
    public static final String REDIS_TOKEN_NAME="token";
    public static final String REDIS_REFRESH_TOKEN_NAME="refreshToken";
    public static final String REDIS_ID_CODE_NAME="emailCode";
    public static final String AUTHORIZATION = "Authorization";
    public static final Set<String> ALLOWED_PATHS = new HashSet<>(Arrays.asList(
            "/users/login",
            "/users/refresh",
            "/users/getCode",
            "/users/register",
            "/users/logout",
            "/admin/login"
    ));
}
