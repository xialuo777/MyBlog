package com.example.authentication;

import org.springframework.stereotype.Component;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/24 16:54]
 */
@Component
public class CurrentUserHolder {
    private final ThreadLocal<Long> currentUser = new ThreadLocal<>();
    public void setUserId(Long userId)
    {
        currentUser.set(userId);
    }
    public Long getUserId()
    {
        return currentUser.get();
    }
    public void clear()
    {
        currentUser.remove();
    }
}
