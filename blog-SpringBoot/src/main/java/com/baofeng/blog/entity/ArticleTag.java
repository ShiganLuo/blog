package com.baofeng.blog.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ArticleTag {
    private Long id;
    private Long articleId;
    private Long tagId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
