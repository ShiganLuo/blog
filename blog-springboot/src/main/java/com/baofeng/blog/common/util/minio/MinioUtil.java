package com.baofeng.blog.common.util.minio;

import io.minio.*;
import io.minio.http.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baofeng.blog.service.FriendLinkService;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioUtil {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${file.public-base-url}")
    private String prefix;

    public MinioUtil(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // 上传文件
        private static final Logger logger = LoggerFactory.getLogger(MinioUtil.class);


    public String uploadFile(String objectName,
                            InputStream stream,
                            long size,
                            String contentType) throws Exception {

        logger.info("开始上传文件到 MinIO, bucket={}, objectName={}, size={}, contentType={}",
                bucket, objectName, size, contentType);

        try {
            // 1. 检查 bucket 是否存在
            logger.debug("检查 MinIO bucket 是否存在: {}", bucket);

            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucket)
                            .build()
            );

            if (!found) {
                logger.warn("MinIO bucket 不存在，准备创建: {}", bucket);

                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucket)
                                .build()
                );

                logger.info("MinIO bucket 创建成功: {}", bucket);
            } else {
                logger.debug("MinIO bucket 已存在: {}", bucket);
            }

            // 2. 上传文件
            logger.debug("开始上传对象到 MinIO, bucket={}, objectName={}", bucket, objectName);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(stream, size, -1)
                            .contentType(contentType)
                            .build()
            );

            logger.info("文件上传成功, bucket={}, objectName={}, size={}",
                    bucket, objectName, size);

            return objectName;

        } catch (Exception e) {
            logger.error("文件上传失败, bucket={}, objectName={}, size={}, contentType={}",
                    bucket, objectName, size, contentType, e);
            throw e;
        }
    }


    /**
     * 获取临时访问 URL（带签名，有效期有限）
     */
    public String getTempFileUrl(String objectName, int duration, TimeUnit unit) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(objectName)
                        .expiry((int) unit.toSeconds(duration)) // 秒
                        .build()
        );
    }

    /**
     * 获取永久访问 URL（bucket 必须是 public）
     */
    public String getPermanentRelativeFileUrl(String objectName) {
        return String.format("/%s/%s", bucket, objectName);
    }

    public String getPermanentFileUrl(String objectName) {
        return String.format("%s/%s/%s",prefix, bucket, objectName);
    }

    // 下载文件
    public InputStream downloadFile(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        );
    }
}
