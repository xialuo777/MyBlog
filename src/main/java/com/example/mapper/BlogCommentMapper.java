package com.example.mapper;

import com.example.entity.BlogComment;
import com.example.utils.bo.BlogCommentBo;

import java.util.List;

public interface BlogCommentMapper {
    int deleteByPrimaryKey(Long commentId);

    int insert(BlogComment record);

    int insertSelective(BlogComment record);

    BlogComment selectByPrimaryKey(Long commentId);

    int updateByPrimaryKeySelective(BlogComment record);

    int updateByPrimaryKey(BlogComment record);

    List<BlogCommentBo> queryFirstCommentList(Long blogId);

    List<BlogCommentBo> querySecondCommentList(Long blogId);
}