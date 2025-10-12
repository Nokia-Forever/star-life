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

    public static final String STAFF_ROLE_KEY = "starLife:staff:role:";

    public static final String SHOP_TYPE_LIST = "starLife:shopType::list";

    public static final String USER_SHOP_ROLE_KEY = "starLife:user:shopRole";
    public static final Long USER_SHOP_ROLE_EXPIRE_TIME = 86400000L;//外置单位为毫秒(24小时)

    public static final String SHOP_BUSINESS_KEY = "starLife:shop:business:";
    public static final String SHOP_STATUS_KEY = "starLife:shop:status:";


    public static final String BLOG_LIKE_KEY = "starLife:blog:like:";
    public static final String BLOGCOMMENT_LIKE_KEY = "starLife:blogComment:like:";

    public static final String SECKILL_STOCK_KEY = "starLife:seckill:stock:";
    public static final String SECKILL_ORDER_KEY = "starLife:seckill:order:";
    public static final String SECKILL_ID_KEY = "starLife:seckill:id:";
    public static final String SECKILL_LOCK_KEY = "starLife:seckill:lock:";


    public static final String USER_SESSION_KEY = "startLife:customerService:session:";
}
