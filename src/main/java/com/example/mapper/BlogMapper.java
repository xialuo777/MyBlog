package com.example.mapper;

import com.example.entity.Blog;

import java.util.List;

public interface BlogMapper {
    int deleteByPrimaryKey(Long blogId);

    int insert(Blog record);

    int insertSelective(Blog record);

    Blog selectByPrimaryKey(Long blogId);

    int updateByPrimaryKeySelective(Blog record);

    int updateByPrimaryKey(Blog record);

    List<Blog> selectBlogsByUserId(Long userId);

    List<Blog> selectBlogsByCategoryId(Long categoryId);

    List<Blog> selectBlogs();
}