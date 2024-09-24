package com.example.controller;

import com.example.utils.JwtProcessor;
import com.example.utils.redis.RedisProcessor;
import com.example.vo.user.Loginer;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}