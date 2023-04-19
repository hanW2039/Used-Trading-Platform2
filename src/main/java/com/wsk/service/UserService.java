package com.wsk.service;

import com.wsk.pojo.LoginTicket;
import com.wsk.pojo.UserInformation;
import org.springframework.stereotype.Service;

import java.util.Map;
/**
 * @author wh
 */
public interface UserService {
    /**
     * 登录
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    Map<String, Object> login(String username, String password, long expiredSeconds);

    /**
     * 注册
     * @param userInformation
     * @return
     */
    Map<String, Object> register(UserInformation userInformation);

    /**
     * 激活
     * @param phone
     * @return
     */
    Integer activation(String phone);

    /**
     * 获取登录ticket
     * @param ticket
     * @return
     */
    LoginTicket findLoginTicket(String ticket);

    /**
     * 退出
     * @param ticket
     */
    void logout(String ticket);
}
