package com.qiujie.aizerocode.manager;

import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import com.qiujie.aizerocode.config.OssConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 阿里云 OSS 对象存储工具类，封装常用上传/下载/删除操作。
 *
 * @author quuj
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssManager {

    private final OSS ossClient;
    private final OssConfig ossConfig;


    /**
     * 上传文件。
     *
     * @param objectName 存储路径
     * @param file       文件
     * @return 文件访问 URL
     */
    public String upload(String objectName, File file) {
        PutObjectResult putObjectResult = ossClient.putObject(ossConfig.getBucketName(), objectName, file);
        if (putObjectResult != null) {
            String url = getUrl(objectName);
            log.info("OSS 上传成功：{} -> {}", file.getName(), url);
            return url;
        }
        log.error("OSS 上传失败：{}", file.getName());
        return null;
    }


    /**
     * 获取文件的公开访问 URL。
     *
     * @param objectName 存储路径
     * @return 文件访问 URL
     */
    public String getUrl(String objectName) {
        return StrUtil.format("https://{}.{}/{}",
                ossConfig.getBucketName(), ossConfig.getEndpoint(), objectName);
    }

    /**
     * 生成带签名的临时访问 URL。
     *
     * @param objectName 存储路径
     * @param expiration 过期时间（毫秒）
     * @return 签名 URL
     */
    public String getSignedUrl(String objectName, long expiration) {
        URL url = ossClient.generatePresignedUrl(
                ossConfig.getBucketName(), objectName, new Date(System.currentTimeMillis() + expiration));
        return url.toString();
    }

    /**
     * 下载文件内容。
     *
     * @param objectName 存储路径
     * @return 文件输入流（调用方需关闭）
     */
    public InputStream download(String objectName) {
        OSSObject object = ossClient.getObject(ossConfig.getBucketName(), objectName);
        return object.getObjectContent();
    }

    /**
     * 删除文件。
     *
     * @param objectName 存储路径
     */
    public void delete(String objectName) {
        ossClient.deleteObject(ossConfig.getBucketName(), objectName);
        log.debug("OSS 删除成功: {}", objectName);
    }

    /**
     * 判断文件是否存在。
     *
     * @param objectName 存储路径
     * @return true 存在
     */
    public boolean exist(String objectName) {
        return ossClient.doesObjectExist(ossConfig.getBucketName(), objectName);
    }
}
