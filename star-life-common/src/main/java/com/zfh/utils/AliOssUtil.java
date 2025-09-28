package com.zfh.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.zfh.property.AliOssProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@AllArgsConstructor
//阿里云工具类
public class AliOssUtil {
    private AliOssProperties aliOssProperties;

    /**
     * 向阿里云oss上传文件
     */
    public String upload(byte[] content, String originalFilename) throws Exception {
        //配置信息
        String endpoint = aliOssProperties.getEndpoint();
        String region = aliOssProperties.getRegion();
        String bucketName = aliOssProperties.getBucketName();
        String accessKeyId = aliOssProperties.getAccessKeyId();
        String accessKeySecret = aliOssProperties.getAccessKeySecret();
        String projectName = aliOssProperties.getProjectName();


        // 使用DefaultCredentialProvider方法直接设置AK和SK
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
        // 填写Object完整路径，例如202406/1.png。Object完整路径中不能包含Bucket名称。
        //获取当前系统日期的字符串,格式为 yyyy/MM
        String dir = projectName+"/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

        //生成一个新的不重复的文件名
        String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = dir + "/" + newFileName;

        // 创建OSSClient实例。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));
        } finally {
            ossClient.shutdown();
        }

        return endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + objectName;
    }
}
