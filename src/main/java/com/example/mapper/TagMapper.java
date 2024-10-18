package com.example.mapper;

import com.example.entity.Tag;

import java.util.List;

public interface TagMapper {
    int deleteByPrimaryKey(Long tagId);

    int insert(Tag record);

    int insertSelective(Tag record);

    Tag selectByPrimaryKey(Long tagId);

    int updateByPrimaryKeySelective(Tag record);

    int updateByPrimaryKey(Tag record);

    List<Tag> selectListByTagNames(List<String> distinctTagNames);

    void insertList(List<Tag> tagListForInsert);
}