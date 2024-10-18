package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blog {
    private Long blogId;

    private String blogTitle;

    private Long userId;

    private String blogDesc;

    private String blogContent;

    private Long categoryId;

    private String categoryName;

    private Integer blogStatus;

    private String blogTags;

    private String thumbnail;

    private Long viewCount;

    private Date creatTime;

    private Date updateTime;

    private Integer isTop;

    private Integer enableComment;

    private Integer isDelete;

    private String subUrl;

}