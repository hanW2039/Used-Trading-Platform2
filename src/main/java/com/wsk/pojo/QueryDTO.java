package com.wsk.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author wh
 * @Date: 2023/04/19/20:45
 * @Description:
 */
@Data
public class QueryDTO {
    private String keyword;

    private List<Integer> kindidList;

    private Integer offset;

    private Integer limit;

    private String name;

}
