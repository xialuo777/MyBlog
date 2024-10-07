package com.example.utils.redis;

import com.example.constant.Constant;

import static com.example.constant.Constant.REDIS_EMAIL_NAME;
import static com.example.constant.Constant.REDIS_NAME_SPACE;

/**
 * [redis修改key值]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 15:50]
 */

public class RedisTransKey {

    public static String emailKey(String key){
        return REDIS_NAME_SPACE+":"+REDIS_EMAIL_NAME+":"+key;
    }

    public static String loginKey(Long key){
        return REDIS_NAME_SPACE+":"+Constant.REDIS_ID_CODE_NAME+":"+key;
    }
    public static String tokenKey(Long key){
        return REDIS_NAME_SPACE+':'+Constant.REDIS_TOKEN_NAME+":"+key;
    }
    public static String refreshTokenKey(Long key){
        return REDIS_NAME_SPACE+':'+Constant.REDIS_REFRESH_TOKEN_NAME+":"+key;
    }

    public static String getLoginKey(Long key){return loginKey(key);}
    public static String getEmailKey(String key){return emailKey(key);}
    public static String getTokenKey(Long key){return tokenKey(key);}
    public static String getRefreshTokenKey(Long key){return refreshTokenKey(key);}
}
