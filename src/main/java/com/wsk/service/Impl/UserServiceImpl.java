package com.wsk.service.Impl;

import com.wsk.dao.UserInformationMapper;
import com.wsk.dao.UserPasswordMapper;
import com.wsk.pojo.LoginTicket;
import com.wsk.pojo.UserInformation;
import com.wsk.pojo.UserPassword;
import com.wsk.response.BaseResponse;
import com.wsk.service.UserInformationService;
import com.wsk.service.UserPasswordService;
import com.wsk.service.UserService;
import com.wsk.util.AllUtils;
import com.wsk.util.Constant;
import com.wsk.util.MailClient;
import com.wsk.util.RedisKeyUtil;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.beans.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author wh
 */
@Service
@Slf4j
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
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MailClient mailClient;


    private String domain = "http://localhost:8888";
    @Override
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
        //验证是否激活
        if (userInformation.getStatus() == 0) {
            map.put("usernameMessage", "该账号未激活！");
            return map;
        }
        //验证密码
        password = com.wsk.tool.StringUtils.getInstance().getMD5(password);
        String password2 = userPasswordService.selectByUid(userInformation.getId()).getPassword();
        if (!password.equals(password2)) {
            map.put("passwordMessage", "密码错误！");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userInformation.getId());
        loginTicket.setTicket(AllUtils.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        String redisKey = RedisKeyUtil.getLoginTicket(loginTicket.getTicket());
        //redis会把loginTicket转为json字符串
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 注册
     * @param userInformation
     * @return
     */
    @Override
    @Transient
    public Map<String, Object> register(UserInformation userInformation) {
        Map<String, Object> map = new HashMap<>();
        // 空值判断
        if (userInformation == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(userInformation.getPhone())) {
            map.put("usernameMessage", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(userInformation.getPassword())) {
            map.put("passwordMessage", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(userInformation.getEmail())) {
            map.put("emailMessage", "邮箱不能为空");
            return map;
        }

        //验证账号
        UserInformation u = userInformationMapper.selectUserInformationByPhone(userInformation.getPhone());
        if (u != null) {
            map.put("usernameMessage", "该账号已存在");
            return map;
        }
        //验证邮箱
        UserInformation e = userInformationMapper.selectUserInformationByEmail(userInformation.getEmail());
        if (e != null) {
            map.put("emailMessage", "该邮箱已被注册");
            return map;
        }

        //用户信息
        userInformation.setCreatetime(new Date());
//        String username = (String) request.getSession().getAttribute("name");
//        userInformation.setUsername(username);
        userInformation.setModified(new Date());
        userInformation.setStatus(0);
        userInformation.setType(0);
        userInformationService.insertSelective(userInformation);
        // 插入密码
        Integer uid = userInformationService.selectIdByPhone(userInformation.getPhone());
        String newPassword = com.wsk.tool.StringUtils.getInstance().getMD5(userInformation.getPassword());
        UserPassword userPassword = new UserPassword();
        userPassword.setModified(new Date());
        userPassword.setUid(uid);
        userPassword.setPassword(newPassword);
        userPasswordService.insertSelective(userPassword);

        //激活邮件
        Context context = new Context();
        context.setVariable("email", userInformation.getEmail());
        //url路径
        String url = domain + "/activation/" + userInformation.getPhone();
        context.setVariable("url", url);
        String content = templateEngine.process("/new/activation", context);
        mailClient.sendMail(userInformation.getEmail(), "激活账号", content);
        return map;
    }

    @Override
    public Integer activation(String phone) {
        UserInformation u = userInformationMapper.selectUserInformationByPhone(phone);
        if (u.getStatus() == 1) {
            return Constant.ACTIVATION_REPEAT;
        } else if (u.getStatus() == 0) {
            userInformationMapper.updateStatus(phone, "1");
//            clearCache(userId);
            return Constant.ACTIVATION_SUCCESS;
        } else {
            return Constant.ACTIVATION_FAILURE;
        }
    }

    public LoginTicket findLoginTicket(String ticket) {

        String redisKey = RedisKeyUtil.getLoginTicket(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    //退出
    public void logout(String ticket) {

        String redisKey = RedisKeyUtil.getLoginTicket(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }
}
