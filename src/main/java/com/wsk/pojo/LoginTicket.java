package com.wsk.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author hanW
 * @create 2022-08-03 12:03
 */
@Data
public class LoginTicket {
    private Integer id;
    private String userId;
    private String ticket;
    private Integer status;
    private Date expired;


    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }
}
