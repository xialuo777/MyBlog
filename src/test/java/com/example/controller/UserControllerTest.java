package com.example.controller;

import com.example.constant.Constant;
import com.example.utils.JwtProcessor;
import com.example.utils.bo.EmailCodeBo;
import com.example.utils.redis.RedisProcessor;
import com.example.vo.user.Loginer;
import com.example.vo.user.Register;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Extensions({@ExtendWith(SpringExtension.class), @ExtendWith(OutputCaptureExtension.class)})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class UserControllerTest {
    @MockBean
    private RedisProcessor redisProcessor;
    @MockBean
    private JwtProcessor jwtProcessor;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    public void init() {
        EmailCodeBo mockEmailCodeBo = new EmailCodeBo();
        mockEmailCodeBo.setCode("tested");
        mockEmailCodeBo.setEmail("212270053@qq.com");
        when(redisProcessor.get(anyString())).thenReturn(Optional.of(mockEmailCodeBo));

    }
    @Test
    @DisplayName("获取邮箱验证码")
    void getCode1() {
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity("/users/getCode?email=2436056388@qq.com", String.class);
        System.out.println(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().contains("验证码已发送"));
    }

    @Test
    @DisplayName("获取邮箱验证码-邮箱输入有误")
    void getCode() {
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity("/users/getCode?email=2436056388", String.class);
        System.out.println(responseEntity.getBody());
    }

    @Test
    @Sql("/testcase/sql/user/initUser.sql")
    @DisplayName("用户注册")
    void testRegister() {
        Register register = new Register("accountTest", "SuperMan", "passwordTest", "passwordTest", "212270053@qq.com", "18539246184", "tested");
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/users/register", register, String.class);
        System.out.println(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    @Sql("/testcase/sql/user/initUser.sql")
    @DisplayName("用户注册-邮箱输入有误")
    void testRegister1() {
        Register register = new Register("accountTest", "SuperMan", "passwordTest", "passwordTest", "212270023@qq.com", "18539246184", "tested");
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/users/register", register, String.class);
        System.out.println(responseEntity.getBody());
    }
    @Test
    @Sql("/testcase/sql/user/initUser.sql")
    @DisplayName("用户注册-邮箱已注册")
    void testRegister2() {
        Register register = new Register("accountTest", "SuperMan", "passwordTest", "passwordTest", "2436056388@qq.com", "18539246184", "tested");
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/users/register", register, String.class);
        System.out.println(responseEntity.getBody());
    }
    @Test
    @Sql("/testcase/sql/user/initUser.sql")
    @DisplayName("用户注册-密码不一致")
    void testRegister3() {
        Register register = new Register("accountTest", "SuperMan", "passwordTest", "passwordTes1t", "2436056368@qq.com", "18539246184", "tested");
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/users/register", register, String.class);
        System.out.println(responseEntity.getBody());
    }
    @Test
    @Sql("/testcase/sql/user/initUser.sql")
    @DisplayName("用户注册-请先获取邮箱验证码")
    void testRegister4() {
        when(redisProcessor.get(anyString())).thenReturn(Optional.empty());
        Register register = new Register("accountTest", "SuperMan", "passwordTest", "passwordTest", "2436056389@qq.com", "18539246184", "tested");
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/users/register", register, String.class);
        System.out.println(responseEntity.getBody());
    }
    @Test
    @Sql("/testcase/sql/user/initUser.sql")
    @DisplayName("用户注册-验证码输入有误")
    void testRegister5() {
        Register register = new Register("accountTest", "SuperMan", "passwordTest", "passwordTest", "212270053@qq.com", "18539246184", "teste1");
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/users/register", register, String.class);
        System.out.println(responseEntity.getBody());
    }
    @Test
    @DisplayName("正常登录")
    @Sql("/testcase/sql/user/initUser.sql")
    public void login1() {
        Loginer loginer = new Loginer("2436056388@qq.com", "passwordTest");
        ResponseEntity<String> response = testRestTemplate.postForEntity("/users/login", loginer, String.class);
        System.out.println(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("操作成功"));
    }
    @Test
    @DisplayName("登录-密码不正确")
    @Sql("/testcase/sql/user/initUser.sql")
    public void login2() {
        Loginer loginer = new Loginer("2436056388@qq.com", "passwordTest1");
        ResponseEntity<String> response = testRestTemplate.postForEntity("/users/login", loginer, String.class);
        System.out.println(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    @Test
    @DisplayName("登录-用户不存在")
    @Sql("/testcase/sql/user/initUser.sql")
    public void login3() {
        Loginer loginer = new Loginer("2436056387@qq.com", "passwordTest");
        ResponseEntity<String> response = testRestTemplate.postForEntity("/users/login", loginer, String.class);
        System.out.println(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Sql("/testcase/sql/user/initUser.sql")
    @DisplayName("刷新令牌")
    public void testRefreshToken1() {
        String refreshToken = "refreshToken";
        Map<String,Object> userMap = new HashMap<>();
        userMap.put(Constant.USER_MAP_KEY_ID, 11111111L);
        userMap.put(Constant.USER_MAP_KEY_NICK_NAME, "user");
        userMap.put(Constant.USER_MAP_KEY_ACCOUNT, "user");
        when(jwtProcessor.extractUserMap(anyString())).thenReturn(userMap);
        when(redisProcessor.get(anyString())).thenReturn(Optional.of("refreshToken"));
        ResponseEntity<String> response = testRestTemplate.postForEntity("/users/refresh", refreshToken, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println(response.getBody());
        assertTrue(response.getBody().contains("操作成功"));
    }
    @Test
    @Sql("/testcase/sql/user/initUser.sql")
    @DisplayName("刷新令牌-refreshToken过期")
    public void testRefreshToken2() {
        String refreshToken = "refreshToken";
        Map<String,Object> userMap = new HashMap<>();
        userMap.put(Constant.USER_MAP_KEY_ID, 11111111L);
        userMap.put(Constant.USER_MAP_KEY_NICK_NAME, "user");
        userMap.put(Constant.USER_MAP_KEY_ACCOUNT, "user");
        when(jwtProcessor.extractUserMap(anyString())).thenReturn(userMap);
        when(redisProcessor.get(anyString())).thenReturn(Optional.empty());
        ResponseEntity<String> response = testRestTemplate.postForEntity("/users/refresh", refreshToken, String.class);

        System.out.println(response.getBody());
    }

    @Test
    @Sql("/testcase/sql/user/initUser.sql")
    @DisplayName("刷新令牌-refreshToken不一致")
    public void testRefreshToken3() {
        String refreshToken = "refreshToken";
        Map<String,Object> userMap = new HashMap<>();
        userMap.put(Constant.USER_MAP_KEY_ID, 11111111L);
        userMap.put(Constant.USER_MAP_KEY_NICK_NAME, "user");
        userMap.put(Constant.USER_MAP_KEY_ACCOUNT, "user");
        when(jwtProcessor.extractUserMap(anyString())).thenReturn(userMap);
        when(redisProcessor.get(anyString())).thenReturn(Optional.of("refreshToken1"));
        ResponseEntity<String> response = testRestTemplate.postForEntity("/users/refresh", refreshToken, String.class);

        System.out.println(response.getBody());

    }
}