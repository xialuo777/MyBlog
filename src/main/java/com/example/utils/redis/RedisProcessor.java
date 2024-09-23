package com.example.utils.redis;

import com.example.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * [redis缓存信息]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 15:38]
 */
@Component
@Slf4j
public class RedisProcessor {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, long expire, TimeUnit timeUnit) {
        if (expire <= 0){
            throw new BusinessException("过期时间不能小于等于0");
        }
        redisTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        if (StringUtils.isNotEmpty(key)) {
            redisTemplate.delete(key);
        }

    }

}
