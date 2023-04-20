package com.wsk.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wh
 */
@Data
public class AllKinds implements Serializable {
    private Integer id;

    private String name;

    private Date modified;

    private String link;

}