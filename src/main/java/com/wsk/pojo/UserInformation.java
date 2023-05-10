package com.wsk.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UserInformation implements Serializable {
    private Integer id;

    private Date modified;

    private String username;

    /**
     * 新加
     */
    private String phone;

    private String email;

    private String password;

    private Integer type;

    private Integer status;

    private String confirmPassword;

    private String realname;

    private String clazz;

    private String sno;

    private String dormitory;

    private String gender;

    private Date createtime;

    private String avatar;

    private Byte lockedflag;
    private Byte isdeleted;
}