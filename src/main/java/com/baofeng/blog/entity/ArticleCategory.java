package com.baofeng.blog.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ArticleCategory {
    private Long id;
    private Long articleId;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
