package com.example.utils;

import com.example.entity.User;
import com.example.constant.Constant;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * [用于提取用户map信息，保存在token中]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 15:00]
 */
@Slf4j
public class UserTransUtils {
    public static Map<String, Object> getUserMap(User user){
        Map<String, Object> userMap = new HashMap<>(3);
        userMap.put(Constant.USER_MAP_KEY_ID, user.getUserId());
        userMap.put(Constant.USER_MAP_KEY_ACCOUNT, user.getAccount());
        userMap.put(Constant.USER_MAP_KEY_NICK_NAME, user.getNickName());
        return userMap;
    }
}
