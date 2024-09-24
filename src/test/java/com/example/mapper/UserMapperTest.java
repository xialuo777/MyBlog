package com.example.mapper;

import com.example.entity.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static com.example.mapper.AssertHelperSipf.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMapperTest {
    private final String DELETE_ALL_SQL = "delete from user";
    private final String SELECT_ALL_SQL = "select * from user";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql(statements = DELETE_ALL_SQL)
    void deleteByPrimaryKey() {
        User user = randomT(User.class);
        int insert = userMapper.insert(user);
        User dbRecord = getDbRecord(User.class, jdbcTemplate, SELECT_ALL_SQL);
        System.out.println(dbRecord.getUserId());
        int i = userMapper.deleteByPrimaryKey(dbRecord.getUserId());
        assertEquals(1,i);
    }

    @Test
    @Sql(statements = DELETE_ALL_SQL)
    void insert() {
        User user = randomT(User.class);
        userMapper.insert(user);
        User dbRecord = getDbRecord(User.class, jdbcTemplate, SELECT_ALL_SQL);
        assertBean(user,dbRecord);
    }

    @Test
    @Sql(statements = DELETE_ALL_SQL)
    void insertSelective() {
        User user = randomT(User.class);
        userMapper.insert(user);
        User dbRecord = getDbRecord(User.class, jdbcTemplate, SELECT_ALL_SQL);
        assertBean(user,dbRecord);
    }

    @Test
    @Sql(statements = DELETE_ALL_SQL)
    void selectByPrimaryKey() {
        User user = randomT(User.class);
        userMapper.insert(user);
        User dbRecord = getDbRecord(User.class, jdbcTemplate, SELECT_ALL_SQL);
        User select = userMapper.selectByPrimaryKey(dbRecord.getUserId());
        assertBean(dbRecord,select);
    }

    @Test
    @Sql(statements = DELETE_ALL_SQL)
    void updateByPrimaryKeySelective() {
        User user = randomT(User.class);
        userMapper.insert(user);
        User dbRecord = getDbRecord(User.class, jdbcTemplate, SELECT_ALL_SQL);
        User user1 = randomT(User.class);
        user1.setUserId(dbRecord.getUserId());
        int i = userMapper.updateByPrimaryKeySelective(user1);
        User dbRecord1 = getDbRecord(User.class, jdbcTemplate, SELECT_ALL_SQL);
        assertBean(user1,dbRecord1);
    }

    @Test
    @Sql(statements = DELETE_ALL_SQL)
    void updateByPrimaryKey() {
        User user = randomT(User.class);
        userMapper.insert(user);
        User dbRecord = getDbRecord(User.class, jdbcTemplate, SELECT_ALL_SQL);
        User user1 = randomT(User.class);
        user1.setUserId(dbRecord.getUserId());
        int i = userMapper.updateByPrimaryKeySelective(user1);
        User dbRecord1 = getDbRecord(User.class, jdbcTemplate, SELECT_ALL_SQL);
        assertBean(user1,dbRecord1);
    }

    @Test
    @Sql(statements = DELETE_ALL_SQL)
    void selectByEmail() {
        User user = randomT(User.class);
        userMapper.insert(user);
        User dbRecord = getDbRecord(User.class, jdbcTemplate, SELECT_ALL_SQL);
        User select = userMapper.selectByEmail(user.getEmail());
        assertBean(dbRecord,select);
    }
}