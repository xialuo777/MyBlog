package com.example.mapper;

import com.example.entity.User;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByEmail(String email);

    List<User> selectUsersByNickName(String nickName);

    List<User> selectUsers(int pageNo, int pageSize);

    int getTotalCount();
}