package com.example.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 10:58]
 */

public class SecurityUtils {
    public static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    /**
     * 对输入password进行加密
     * @param password 需要加密的密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    /**
     * 检查输入的密码是否和数据库中存储的加密密码匹配
     * @param password 输入的明文密码
     * @param encodePassword 数据库中存储的加密密码
     * @return
     */
    public static boolean matches(String password, String encodePassword) {
        return bCryptPasswordEncoder.matches(password, encodePassword);
    }
}
