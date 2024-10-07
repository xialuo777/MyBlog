package com.example.utils.bo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/3 11:09]
 */
@Data
@Component
public class EmailCodeBo implements Serializable {
    private String email;
    private String code;

}
