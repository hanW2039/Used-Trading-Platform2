package com.wsk.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Lenovo
 * @Auther: Lenovo
 * @Date: 2023/05/06/20:14
 * @Description:
 */
@Data
@Getter
@Setter
public class Operation {
    private Integer id;

    private Integer uid;

    private Integer sid;

    private Integer count = 1;

    private String type;
}
