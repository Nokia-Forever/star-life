package com.zfh.constant;

/**
 * 异常常量
 */
public class ExceptionConstant {
    //验证码获取失败
    public static final String CAPTCHA_GET_FAILED = "验证码获取失败";
    //验证码超时,请刷新
    public static final String CAPTCHA_TIMEOUT = "验证码超时,请刷新";
    //请求错误
    public static final String REQUEST_ERROR = "请求错误";
    //验证码错误
    public static final String CAPTCHA_ERROR = "验证码错误";
    //用户名为空
    public static final String USERNAME_EMPTY = "用户名为空";
    //用户未登录
    public static final String USER_NOT_LOGIN = "用户未登录";
    //"用户被锁"
    public static final String USER_LOCK = "用户被锁,稍后再试";
    //用户不存在
    public static final String USER_NOT_EXIST = "用户不存在";



    //用户名存在
    public static final String USERNAME_EXIST = "用户名已存在";
    //用户在别处登录,请重新登录
    public static final String USER_LOGIN_ELSEWHERE = "用户在别处登录,请重新登录";
    public static final String USER_PASSWORD_ERROR = "密码错误";
}
