package com.wsk.service.Impl;

import com.wsk.dao.UserInformationMapper;
import com.wsk.dao.UserPasswordMapper;
import com.wsk.pojo.LoginTicket;
import com.wsk.pojo.UserInformation;
import com.wsk.service.UserInformationService;
import com.wsk.service.UserPasswordService;
import com.wsk.service.UserService;
import com.wsk.util.AllUtils;
import com.wsk.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wh
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserPasswordMapper userPasswordMapper;
    @Autowired
    private UserInformationMapper userInformationMapper;
    @Autowired
    private UserInformationService userInformationService;
    @Autowired
    private UserPasswordService userPasswordService;

    @Autowired
    private RedisTemplate redisTemplate;

    public Map<String, Object> login(String phoneNumber, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        //空之判断
        if (StringUtils.isBlank(phoneNumber)) {
            map.put("usernameMessage", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMessage", "密码不能为空");
            return map;
        }
        UserInformation userInformation = userInformationMapper.selectUserInformationByPhone(phoneNumber);
        if (userInformation == null) {
            map.put("usernameMessage", "账号不存在！");
            return map;
        }
//        //验证是否激活
//        if (user.getStatus() == 0) {
//            map.put("usernameMessage", "该账号未激活！");
//            return map;
//        }
        //验证密码
        password = com.wsk.tool.StringUtils.getInstance().getMD5(password);
        String password2 = userPasswordService.selectByUid(userInformation.getId()).getPassword();
        if (!password.equals(password2)) {
            map.put("passwordMessage", "密码错误！");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userInformation.getPhone());
        loginTicket.setTicket(AllUtils.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        String redisKey = RedisKeyUtil.getLoginTicket(loginTicket.getTicket());
        //redis会把loginTicket转为json字符串
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }
}
