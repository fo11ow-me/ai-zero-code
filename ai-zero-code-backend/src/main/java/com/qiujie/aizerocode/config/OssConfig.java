package com.qiujie.aizerocode.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置类，读取 oss.client.* 配置并创建 OSS 客户端 Bean。
 *
 * @author quuj
 */
@Configuration
@ConfigurationProperties(prefix = "oss.client")
@Data
public class OssConfig {

    /** OSS 地域节点，如 oss-cn-guangzhou.aliyuncs.com */
    private String endpoint;

    /** 访问密钥 ID */
    private String accessKeyId;

    /** 访问密钥 Secret */
    private String accessKeySecret;

    /** 存储桶名称 */
    private String bucketName;

    /**
     * 创建 OSS 客户端实例。
     *
     * @return OSS 客户端
     */
    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
