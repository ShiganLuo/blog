package com.baofeng.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
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
    private Boolean isDeleted;
    private Integer type;
    private ArticleStatus status;
    private String originUrl;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public enum ArticleStatus {
        DRAFT, PUBLISHED, DELETED;
    }
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    public Article() {
        // 可选择初始化默认值
        this.views = 0L;
        this.likes = 0L;
        this.commentsCount = 0L;
        this.isFeatured = false;
        this.isTop = false;
        this.isDeleted = false;
        this.status = ArticleStatus.DRAFT;
        this.type = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}

