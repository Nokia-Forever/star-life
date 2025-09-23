package com.zfh.config;

import com.zfh.constant.URLConstant;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/**
 * URL 配置类
 */
@Configuration
public class URLConfig {
    /**
     * 客户端白名单URL
     */
    public final ArrayList<String> CLIENT_WHITE_URL_LIST = new ArrayList<>();
    /**
     * 管理端白名单URL
     */
    public final ArrayList<String> ADMIN_WHITE_URL_LIST = new ArrayList<>();
    /**
     * 通用白名单URL
     */
    public final ArrayList<String> COMMON_WHITE_URL_LIST = new ArrayList<>();


    @PostConstruct
    public void init() {
        //初始化客户端白名单URL
        initClientWhiteUrl();
        //初始化管理端白名单URL
        initAdminWhiteUrl();
        //初始化通用白名单URL
        initCommonWhiteUrl();
    }

    //初始化客户端白名单URL
    private void initClientWhiteUrl() {
        CLIENT_WHITE_URL_LIST.add(URLConstant.SHOP_TYPE_LIST_URL);
        CLIENT_WHITE_URL_LIST.add(URLConstant.USER_LOGIN_URL);
        CLIENT_WHITE_URL_LIST.add(URLConstant.USER_REGISTER_URL);

        //ANT白名单
        CLIENT_WHITE_URL_LIST.add(URLConstant.SHOP_WHITE_URL);
        CLIENT_WHITE_URL_LIST.add(URLConstant.USER_WHITE_URL);
        CLIENT_WHITE_URL_LIST.add(URLConstant.STAFF_WHITE_URL);
    }

    //初始化管理端白名单URL
    private void initAdminWhiteUrl() {

    }

    //初始化通用白名单URL
    private void initCommonWhiteUrl() {
        COMMON_WHITE_URL_LIST.add(URLConstant.CAPTCHA_URL);
    }
}
