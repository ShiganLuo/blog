package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class Image {
    private Long id;
    private String filePath;
    private String fileName;
    //kb
    private Long fileSize;
    private String mimeType;
    //上传人用户名
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
}
