package com.example.vo.blog;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/5 14:00]
 */
@Data
@ApiModel(description = "博客分类")
public class BlogCategory {
    /*分类id*/
    @ApiModelProperty(value = "分类id")
    private Integer id;

    /* 分类名*/
    @ApiModelProperty(value = "分类名")
    private String categoryName;
}
