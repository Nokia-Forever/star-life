package com.zfh.constant;

/**
 * URL常量
 */
public class URLConstant {
    //用户登录URL
    public static final String USER_LOGIN_URL = "/client/user/login";
    //用户登出
    public static final String USER_LOGOUT_URL = "/client/user/logout";
    //用户注册
    public static final String USER_REGISTER_URL = "/client/user/register";
    //商品类型列表
    public static final String SHOP_TYPE_LIST_URL = "/client/shop-type/list";

    //验证码URl
    public static final String CAPTCHA_URL = "/common/captcha";

    //商铺白名单 URL
    public static final String SHOP_WHITE_URL = "/client/shop/white/**";
    //用户白名单 URL
    public static final String USER_WHITE_URL = "/client/user/white/**";
    //职员白名单
    public static final String STAFF_WHITE_URL = "/client/staff/white/**";
    //博客白名单
    public static final String BLOG_WHITE_URL = "/client/blog/white/**";
    //优惠券白名单
    public static final String VOUCHER_WHITE_URL = "/client/voucher/white/**";

}
