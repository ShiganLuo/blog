package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleLike {
    private Long id;
    private Long userId;
    private Long articleId;
    private LocalDateTime createdAt;
    
}
