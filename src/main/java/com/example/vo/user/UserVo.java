package com.example.vo.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/7 14:00]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long userId;
    private String nickName;
    private String email;
    private String website;
}