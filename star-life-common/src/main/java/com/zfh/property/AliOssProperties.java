package com.zfh.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//阿里云OSS配置类
@Component
@ConfigurationProperties(prefix = "com.zfh.alioss")
@Data
public class AliOssProperties {
    private String endpoint;// 阿里云OSS地址
    private String bucketName;// bucket名字
    //项目名字
    private String projectName;
    private String region;// ip
    private String accessKeyId;// accessKeyId
    private String accessKeySecret;// accessKeySecret

}
