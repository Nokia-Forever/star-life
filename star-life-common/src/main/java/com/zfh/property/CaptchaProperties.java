package com.zfh.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码配置属性
 */
@Component
@ConfigurationProperties(prefix = "com.zfh.captcha")
@Data
public class CaptchaProperties {
    //用户
    //验证码是否启用
    private boolean userEnable =false ;
    //验证码长度
    private int userLength = 4;
}
