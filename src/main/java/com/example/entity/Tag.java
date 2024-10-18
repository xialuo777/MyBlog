package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
    private Long tagId;

    private String tagName;

    private Date createTime;

    private Integer deleteFlag;

    public Tag(long snowflakeNextId, String tagName) {
        this.tagId = snowflakeNextId;
        this.tagName = tagName;
    }

}