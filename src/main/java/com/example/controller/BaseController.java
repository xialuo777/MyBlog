package com.example.controller;

import com.example.authentication.CurrentUserHolder;
import com.example.constant.Constant;
import com.example.entity.Blog;
import com.example.entity.User;
import com.example.enums.ResponseCodeEnum;
import com.example.exception.BusinessException;
import com.example.mapper.UserMapper;
import com.example.utils.JwtProcessor;
import com.example.utils.redis.RedisProcessor;
import com.example.utils.redis.RedisTransKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/3 14:01]
 */

@RestController
public class BaseController {
    @Autowired
    private CurrentUserHolder currentUserHolder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProcessor jwtProcessor;
    @Autowired
    private RedisProcessor redisProcessor;
    protected boolean validPageParams(Map<String, Object> params) {
        if (ObjectUtils.isEmpty(params.get(Constant.PAGE_NO)) || ObjectUtils.isEmpty(params.get(Constant.PAGE_SIZE))) {
            return false;
        }
        return true;
    }
    protected boolean validUserStatus(User user) {
        return user.getStatus().equals(Constant.USER_STATUS_NORMAL);
    }
    protected boolean validBlogEnableComment(Blog blog) {
        return blog.getEnableComment().equals(Constant.BLOG_ENABLE_COMMENT_YES);
    }
    protected boolean isValidUser(Long userId){
        return currentUserHolder.getUserId().equals(userId);
    }

    /**
     * 检查用户以及token
     *
     * @return User
     */
    protected User checkUser() {
        Long userId = currentUserHolder.getUserId();
        User user = Optional.ofNullable(userMapper.selectByPrimaryKey(userId))
                .orElseThrow(() -> new BusinessException("用户不存在！"));

        String accessToken = (String) redisProcessor.get(RedisTransKey.getTokenKey(userId));
        if (!jwtProcessor.validateToken(accessToken, user.getAccount())) {
            throw new BusinessException("token验证失败", ResponseCodeEnum.PARAM_ERROR);
        }
        return user;
    }
}
