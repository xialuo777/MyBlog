package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import com.example.authentication.CurrentUserHolder;
import com.example.constant.Constant;
import com.example.dto.LoginResponse;
import com.example.dto.ResponseResult;
import com.example.entity.User;
import com.example.enums.ResponseCodeEnum;
import com.example.exception.BusinessException;
import com.example.mapper.UserMapper;
import com.example.service.MailService;
import com.example.utils.JwtProcessor;
import com.example.utils.SecurityUtils;
import com.example.utils.UserTransUtils;
import com.example.utils.bo.EmailCodeBo;
import com.example.utils.dto.PageRequest;
import com.example.utils.dto.PageResult;
import com.example.utils.redis.RedisProcessor;
import com.example.utils.redis.RedisTransKey;
import com.example.vo.PasswordVo;
import com.example.vo.user.Loginer;
import com.example.vo.user.Register;
import com.example.vo.user.UserInfoVo;
import com.example.vo.user.UserVo;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
@Api(tags = "用户功能接口")
public class UserController extends BaseController {
    private final UserMapper userMapper;
    private final JwtProcessor jwtProcessor;
    private final RedisProcessor redisProcessor;
    private final MailService mailService;
    private final CurrentUserHolder currentUserHolder;

    /**
     * 用户登录接口
     *
     * @param loginer 用户登录信息
     * @return ResponseResult<String>
     */
    @PostMapping("/login")
    @ApiModelProperty(value = "用户登录")
    public ResponseResult<LoginResponse> login(@Validated @RequestBody Loginer loginer) {
        String email = loginer.getEmail();
        User user = Optional.ofNullable(userMapper.selectByEmail(email)).orElseThrow(() -> new BusinessException("用户不存在"));
        boolean matches = SecurityUtils.matches(loginer.getPassword(), user.getPassword());
        if (!matches) {
            throw new BusinessException("密码错误", HttpStatus.OK);
        }
        Map<String, Object> userMap = getUserMap(user);
        String token = jwtProcessor.generateToken(userMap);
        String refreshToken = jwtProcessor.generateRefreshToken(userMap);
        redisProcessor.set(RedisTransKey.tokenKey(user.getUserId()), token, 15, TimeUnit.MINUTES);
        redisProcessor.set(RedisTransKey.refreshTokenKey(user.getUserId()), refreshToken, 7, TimeUnit.DAYS);
        LoginResponse loginResponse = new LoginResponse(token, refreshToken);
        return ResponseResult.success(loginResponse);
    }

    /**
     * 用户注册接口
     *
     * @param register 用户注册信息
     * @return ResponseResult<String>
     */
    @PostMapping("/register")
    @ApiModelProperty(value = "用户注册")
    public ResponseResult<String> register(@Validated @RequestBody Register register) {
        String account = register.getAccount();
        String nickName = register.getNickName();
        String password = register.getPassword();
        String checkPassword = register.getCheckPassword();
        String email = register.getEmail();
        String phone = register.getPhone();
        String emailCode = register.getEmailCode().trim();
        if (userMapper.selectByEmail(email) != null) {
            log.error("邮箱已注册，请重新输入：{}", email);
            throw new BusinessException("邮箱已注册，请重新输入", ResponseCodeEnum.ERROR);
        }
        //验证两次输入密码是否一致
        if (!checkPassword.equals(password)) {
            log.error("两次输入密码不一致，注册失败：{}", account);
            throw new BusinessException("两次输入密码不一致，请重新输入", ResponseCodeEnum.PARAM_ERROR);
        }
        /*验证邮箱验证码*/
        Optional<EmailCodeBo> optionalEmailCodeBo = (Optional<EmailCodeBo>) redisProcessor.get(RedisTransKey.getEmailKey(email));
        if (!optionalEmailCodeBo.isPresent()){
            log.error("请先获取邮箱验证码");
            throw new BusinessException("请先获取邮箱验证码", ResponseCodeEnum.PARAM_ERROR);
        }
        EmailCodeBo emailCodeBo = optionalEmailCodeBo.get();

        if (!emailCodeBo.getCode().equals(emailCode)) {
            log.error("邮箱验证码输入错误，注册失败：{}", account);
            throw new BusinessException("请确认邮箱验证码是否正确", ResponseCodeEnum.PARAM_ERROR);
        }
        if (!emailCodeBo.getEmail().equals(email)) {
            log.error("邮箱输入错误，注册失败：{}", account);
            throw new BusinessException("请确认邮箱输入是否正确", ResponseCodeEnum.PARAM_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(register, user);
        user.setUserId(IdUtil.getSnowflakeNextId());
        String baseHomePageUrl = Constant.USER_BASE_PATH + user.getUserId();
        user.setWebsite(baseHomePageUrl);
        return ResponseResult.success("注册成功");
    }

    /**
     * 获取验证码接口
     *
     * @param email
     * @return ResponseResult<String>
     */
    @GetMapping("/getCode")
    @ApiModelProperty(value = "获取验证码")
    public ResponseResult<String> getCode(@RequestParam @Email String email) {
        mailService.sendCode(email);
        return ResponseResult.success("验证码已发送");
    }

    /**
     * 刷新令牌验证
     *
     * @param refreshToken 输入的刷新令牌信息
     * @return ResponseResult<String>
     */
    @PostMapping("/refresh")
    @ApiModelProperty(value = "刷新token")
    public ResponseResult<String> refresh(@RequestBody String refreshToken) {
        Map<String, Object> userMap = jwtProcessor.extractUserMap(refreshToken);
        Long userId = (Long) userMap.get(Constant.USER_MAP_KEY_ID);
        Optional<String> redisRefreshToken = (Optional<String>) redisProcessor.get(RedisTransKey.getRefreshTokenKey(userId));
        if (!redisRefreshToken.isPresent()){
            log.error("refreshToken已过期，请重新登录");
            throw new BusinessException("refreshToken已过期，请重新登录", ResponseCodeEnum.PARAM_ERROR);
        }
        if (!redisRefreshToken.get().equals(refreshToken)) {
            log.error("refreshToken不一致");
            throw new BusinessException("refreshToken不一致", ResponseCodeEnum.PARAM_ERROR);
        }
        String accessToken = jwtProcessor.generateToken(userMap);
        String newRefreshToken = jwtProcessor.generateRefreshToken(userMap);
        redisProcessor.set(RedisTransKey.tokenKey(userId), accessToken, 15, TimeUnit.MINUTES);
        redisProcessor.set(RedisTransKey.refreshTokenKey(userId), newRefreshToken, 7, TimeUnit.DAYS);
        return ResponseResult.success(accessToken);
    }

    /**
     * 退出登录接口
     *
     * @return ResponseResult<String>
     */
    @PostMapping("/logout")
    @ApiModelProperty(value = "用户登出")
    public ResponseResult<String> logout() {
        User user = checkUser();
        redisProcessor.delete(RedisTransKey.tokenKey(user.getUserId()));
        redisProcessor.delete(RedisTransKey.refreshTokenKey(user.getUserId()));
        return ResponseResult.success("用户退出登陆成功");
    }

    /**
     * 删除用户信息接口
     *
     * @return ResponseResult<String>
     */
    @PostMapping("/delete")
    @ApiModelProperty(value = "删除用户")
    public ResponseResult<String> delete() {
        User user = checkUser();
        userMapper.deleteByPrimaryKey(user.getUserId());
        return ResponseResult.success("用户删除成功");
    }

    /**
     * 更新用户信息接口
     *
     * @param userInfoVo 更新用户信息
     * @return ResponseResult<String>
     */
    @PostMapping("/update")
    public ResponseResult<String> updateUser(@RequestBody UserInfoVo userInfoVo) {
        String accessToken = (String) redisProcessor.get(RedisTransKey.getTokenKey(userInfoVo.getUserId()));
        Long userId = currentUserHolder.getUserId();
        if (!userId.equals(userInfoVo.getUserId())) {
            log.error("用户id不一致");
            throw new BusinessException("用户id不一致", ResponseCodeEnum.PARAM_ERROR);
        }
        User user = Optional.ofNullable(userMapper.selectByPrimaryKey(userId))
                .orElseThrow(() -> new BusinessException("用户不存在！"));
        if (!jwtProcessor.validateToken(accessToken, user.getAccount())) {
            log.error("token验证失败");
            throw new BusinessException("token验证失败", ResponseCodeEnum.PARAM_ERROR);
        }
        BeanUtil.copyProperties(userInfoVo, user, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        userMapper.updateByPrimaryKey(user);
        Map<String, Object> userMap = UserTransUtils.getUserMap(user);
        redisProcessor.set(RedisTransKey.getTokenKey(userId), userMap, 15, TimeUnit.MINUTES);
        redisProcessor.set(RedisTransKey.getRefreshTokenKey(userId), userMap, 7, TimeUnit.DAYS);
        return ResponseResult.success("用户信息更新成功");
    }

    /**
     * 更新用户密码接口
     *
     * @param passwordVo 用户更新密码对象
     * @return ResponseResult<String>
     */
    @PostMapping("/update/password")
    public ResponseResult<String> updatePassword(@RequestBody PasswordVo passwordVo) {
        String oldPassword = passwordVo.getOldPassword();
        String newPassword = passwordVo.getNewPassword();
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
            return ResponseResult.fail("输入密码不能为空");
        }

        User user = checkUser();
        if (!SecurityUtils.matches(oldPassword, user.getPassword())) {
            return ResponseResult.fail("密码验证失败，请重新输入");
        }
        user.setPassword(SecurityUtils.encode(newPassword));
        userMapper.updateByPrimaryKey(user);
        return ResponseResult.success("密码修改成功");
    }

    /**
     * 获取用户主页信息
     *
     * @return ResponseResult<UserInfoVo>
     */
    @GetMapping("/home")
    public ResponseResult<UserInfoVo> getProfile() {
        User user = checkUser();
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtil.copyProperties(user, userInfoVo);
        return ResponseResult.success(userInfoVo);
    }

    /**
     * 根据用户昵称查找用户信息
     *
     * @param nickName 昵称
     * @param params   分页参数
     * @return ResponseResult<PageResult < UserVo>>
     */
    @GetMapping("/{nickName}")
    public ResponseResult<PageResult<UserVo>> getUsersByNickName(@PathVariable String nickName, @RequestParam Map<String, Object> params) {
        if (!validPageParams(params)) {
            return ResponseResult.fail("分页参数异常");
        }
        PageRequest pageRequest = new PageRequest(params);
        PageHelper.startPage(pageRequest.getPageNo(), pageRequest.getPageSize());
        List<User> users = userMapper.selectUsersByNickName(nickName);
        List<UserVo> result = users.stream().map(user -> BeanUtil.copyProperties(user, UserVo.class))
                .collect(Collectors.toList());
        int totalCount = result.size();
        PageResult<UserVo> pageResult = new PageResult<>(result, totalCount);
        return ResponseResult.success(pageResult);
    }

    /**
     * 获取所有用户列表
     * @param params 分页参数
     * @return ResponseResult<PageResult<UserVo>>
     */
    @GetMapping("/getUsers")
    public ResponseResult<PageResult<UserVo>> getUsers(@RequestParam Map<String, Object> params) {
        if (!validPageParams(params)){
            return ResponseResult.fail("分页参数异常");
        }
        PageRequest pageRequest = new PageRequest(params);
        PageHelper.startPage(pageRequest.getPageNo(), pageRequest.getPageSize());
        List<User> users = Optional.ofNullable(userMapper.selectUsers(pageRequest.getPageNo(), pageRequest.getPageSize()))
                .orElseThrow(() -> new BusinessException("用户列表为空"));
        List<UserVo> result = users.stream()
                .map(user -> BeanUtil.copyProperties(user, UserVo.class))
                .collect(Collectors.toList());
        int totalCount = userMapper.getTotalCount();
        PageResult<UserVo> pageResult = new PageResult<>(result, totalCount);
        return ResponseResult.success(pageResult);
    }

}
