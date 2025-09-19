package com.zfh.constant;

/**
 * redis key
 */
public class RedisKeyConstant {
    public static final String USER_CAPTCHA_KEY = "starLife:login:user:captcha:";
    public static final Long USER_CAPTCHA_EXPIRE_TIME = 60000L;//外置单位为毫秒(60秒)

    public static final String USER_TOKEN_KEY = "starLife:login:user:";
    public static final Long USER_TOKEN_EXPIRE_TIME = 86400000L;//外置单位为毫秒(24小时)

    public static final String USER_LOCK_KEY = "starLife:loginLock:user:";
    public static final Long USER_LOCK_EXPIRE_TIME = 60000L;//外置单位为毫秒(60秒)

    public static final String USER_FOLLOW_KEY = "starLife:follow:user:";
    public static final Long USER_FOLLOW_EXPIRE_TIME = 86400000L;//外置单位为毫秒(24小时)

}
