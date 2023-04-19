package com.wsk.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author hanW
 * @create 2022-07-30 13:42
 */
@Data
public class User {
    private int id;
    private String username;
    private String password;
    //拼接密码提升安全性
    private String salt;
    private String email;
    private int type;
    private int status;
    //激活码
    private String activationCode;
    private String headerUrl;
    private Date createTime;

    public User(){}
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", activationCode='" + activationCode + '\'' +
                ", headerUrl='" + headerUrl + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
