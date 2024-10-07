package com.example.utils.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * [分页]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/7 14:06]
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest extends LinkedHashMap<String, Object> {
    private int pageNo;
    private int pageSize;

    public PageRequest(Map<String, Object> params){
        this.putAll(params);
        this.pageNo = Integer.parseInt(params.get("pageNo").toString());
        this.pageSize = Integer.parseInt(params.get("pageSize").toString());
        this.put("pageSize", pageSize);
        this.put("pageNo",pageNo);
    }
}