package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogComment {
    /**
    * 主键id
    */
    private Long commentId;

    /**
    * 关联的blog主键
    */
    private Long blogId;

    /**
    * 评论者名称
    */
    private String commentator;

    /**
    * 评论人的id
    */
    private Long commentatorId;

    /**
    * 评论内容
    */
    private String commentBody;

    /**
    * 评论提交时间
    */
    private Date commentCreateTime;

    /**
    * 评论上一级的id
    */
    private Long lastId;

    /**
    * 是否删除 0-未删除 1-已删除
    */
    private Byte isDeleted;

}