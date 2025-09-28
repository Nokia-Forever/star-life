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


    //操作失败,请稍后再试
    public static final String OPERATION_FAILED = "操作失败,请稍后再试";
    //不能关注自己
    public static final String CANNOT_FOLLOW_YOURSELF = "不能关注自己";

    //非商家不可操作
    public static final String NOT_BUSINESS = "非商家不可操作";
    //权限不足
    public static final String PERMISSION_DENIED = "权限不足";
    public static final String USER_IS_STAFF = "该用户已加入";

    //用户不是该店职员
    public static final String USER_IS_NOT_STAFF ="用户不是该店职员" ;

    //图片上传失败
    public static final String IMAGE_UPLOAD_FAILED = "图片上传失败";
    //图片格式错误
    public static final String IMAGE_FORMAT_ERROR = "图片格式错误";

    //此篇博客不存在
    public static final String BLOG_NOT_EXIST = "此篇博客不存在";
}
