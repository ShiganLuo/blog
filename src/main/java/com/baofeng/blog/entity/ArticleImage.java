package com.baofeng.blog.entity;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class ArticleImage {
    private Long id;
    private Long articleId;
    private Long imageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
}
