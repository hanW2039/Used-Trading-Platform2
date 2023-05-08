package com.wsk.util;

/**
 * @author hanW
 * @create 2022-08-11 18:32
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_RECOMMEND = "recommend";




    //登录验证码
    //kaptcha:owner
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登陆凭证
    public static String getLoginTicket(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 推荐算法推荐
    public static String getRecommend(String uid){
        return PREFIX_TICKET + SPLIT + uid;
    }
}
