package com.example.controller;

import com.example.dto.LoginResponse;
import com.example.dto.ResponseResult;
import com.example.entity.User;
import com.example.exception.BusinessException;
import com.example.mapper.UserMapper;
import com.example.utils.JwtProcessor;
import com.example.utils.redis.RedisProcessor;
import com.example.utils.SecurityUtils;
import com.example.utils.redis.RedisTransKey;
import com.example.vo.user.Loginer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.utils.UserTransUtils.getUserMap;

/**
 * [用户功能接口]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @time 2024-09-23 11:02
 */
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final JwtProcessor jwtProcessor;
    private final RedisProcessor redisProcessor;
    @PostMapping("/login")
    public ResponseResult<LoginResponse> login(@Validated @RequestBody Loginer loginer) {
        String email = loginer.getEmail();
        User user = Optional.ofNullable(userMapper.selectByEmail(email)).orElseThrow(()->new BusinessException("用户不存在", HttpStatus.NOT_FOUND));
        boolean matches = SecurityUtils.matches(loginer.getPassword(), user.getPassword());
        if (!matches){
            throw new BusinessException("密码错误", HttpStatus.FORBIDDEN);
        }
        Map<String, Object> userMap = getUserMap(user);
        String token = jwtProcessor.generateToken(userMap);
        String refreshToken = jwtProcessor.generateRefreshToken(userMap);
        redisProcessor.set(RedisTransKey.tokenKey(user.getUserId()), token,15, TimeUnit.MINUTES);
        redisProcessor.set(RedisTransKey.refreshTokenKey(user.getUserId()),refreshToken,7, TimeUnit.DAYS);
        LoginResponse loginResponse = new LoginResponse(token, refreshToken);
        return ResponseResult.success(loginResponse);
    }


}
