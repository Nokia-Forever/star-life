package com.zfh.constant;

/**
 * redis key
 */
public class RedisKeyConstant {
    public static final String USER_CAPTCHA_KEY = "tasteNotes:login:user:captcha:";
    public static final Long USER_CAPTCHA_EXPIRE_TIME = 60000L;//外置单位为毫秒(60秒)

    public static final String USER_TOKEN_KEY = "tasteNotes:login:user:token:";
    public static final Long USER_TOKEN_EXPIRE_TIME = 86400000L;//外置单位为毫秒(24小时)

    public static final String USER_LOCK_KEY = "tasteNotes:loginLock:user:username:";
    public static final Long USER_LOCK_EXPIRE_TIME = 60000L;//外置单位为毫秒(60秒)
}
