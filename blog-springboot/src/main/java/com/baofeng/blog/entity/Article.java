package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Article {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Long authorId;
    private Long views;
    private Long likes;
    private Long commentsCount;
    private Boolean isFeatured;
    private Boolean isTop;
    private Boolean isDelted;
    private Integer type;
    private ArticleStatus status;
    private String originUrl;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public enum ArticleStatus {
        DRAFT, PUBLISHED, DELETED;
    }

}

