package com.example.vo.blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/5 14:33]
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BlogDetail extends BlogDesc{
    private String blogDesc;
    private Long userId;
    private String blogContent;
    private String blogTags;
    private String thumbnail;
    private Integer isTop;
    private Integer blogStatus;

}
