package com.zfh.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * jwt配置属性
 */
@Component
@ConfigurationProperties(prefix = "com.zfh.jwt")
@Data
public class JwtProperties {
    private   String userSecret;
    private   String userTokenName;
    private   String adminSecret;
    private   String adminTokenName;
}
