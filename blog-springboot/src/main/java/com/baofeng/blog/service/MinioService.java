package com.baofeng.blog.service;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // 上传文件
    public String uploadFile(String objectName, InputStream stream, long size, String contentType) throws Exception {
        // 确保存储桶存在
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        // 上传
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(stream, size, -1)
                        .contentType(contentType)
                        .build()
        );

        return objectName;
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
    public String getPermanentFileUrl(String objectName) {
        return String.format("%s/%s/%s", endpoint, bucket, objectName);
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
