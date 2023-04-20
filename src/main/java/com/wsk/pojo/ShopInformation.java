package com.wsk.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wh
 */
@Data
public class ShopInformation implements Serializable {
    private Integer id;

    private Date modified;

    private String name;

    private Integer level;

    private String remark;

    private BigDecimal price;

    private Integer sort;

    private Integer display;

    private Integer quantity;

    private Integer transaction;

    private Integer uid;

    private String image;

    private Integer sales;

    private String thumbnails;

    private Integer kindid;
}