package com.wsk.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@Data
public class UserWant implements Serializable {
    private Integer id;

    private Date modified;

    private Integer display;

    private String name;

    private Integer sort;

    private Integer uid;

    private Integer quantity;

    private BigDecimal price;

    private String remark;

    private String image;
}