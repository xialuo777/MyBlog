package com.example.utils.bo;

import com.example.entity.BlogComment;
import com.example.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/7 18:55]
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BlogCommentBo extends BlogComment {
    @ApiModelProperty(value = "评论人信息")
    private User user;
    @ApiModelProperty(value = "下一条回复")
    private List<BlogCommentBo> nextNodes = new ArrayList<>();


}