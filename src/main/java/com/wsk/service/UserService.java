package com.wsk.service;

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
}
