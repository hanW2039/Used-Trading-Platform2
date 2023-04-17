package com.wsk.util;

public class Constant {
    //    public static final
    //激活成功
    public static final Integer  ACTIVATION_SUCCESS = 0;
    //重复激活
    public static final Integer  ACTIVATION_REPEAT = 1;
    //激活失败
    public static final Integer  ACTIVATION_FAILURE = 2;
    //默认状态的登陆凭证的超时时间
    public static final Integer  DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    //记住状态下的登陆凭证的超时时间
    public static final Integer REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
}
