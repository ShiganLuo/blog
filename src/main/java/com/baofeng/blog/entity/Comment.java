package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private String content;
    private Long articleId;
    private Long userId;
    private Long parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 