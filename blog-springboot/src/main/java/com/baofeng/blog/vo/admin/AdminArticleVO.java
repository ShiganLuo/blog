package com.baofeng.blog.vo.admin;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

public class AdminArticleVO {
    public record CreateArticleRequest(
        String title,
        String content,
        String summary,
        String author
    ) {}
    /**
     * 文章分页请求参数
     */
    public record ArticlePageRequestVO(
        @NotNull(message = "页码不能为空")
        @Min(value = 1, message = "页码必须大于等于1")
        Integer pageNum,     // 当前页码

        @NotNull(message = "每页显示条数不能为空")
        @Min(value = 1, message = "每页显示条数必须大于等于1")
        Integer pageSize,    // 每页显示条数
        
        String keyword,      // 可选，搜索关键词
        Long categoryId,     // 可选，分类ID
        String sortBy,       // 可选，排序字段
        String sortOrder     // 可选，排序方向
    ) {}

    /**
     * 文章分类请求
     */
    @Data
    public static class CategoryRequest {
        private String categoryName;
        private Long articleId;
    }
    /**
     * 文章标签请求 
     */
    @Data
    public static class TagRequest {
        private List<String> tagNames;
        private Long articleId;
    }

    public record CreateAdminArticlePageRequest(
        Integer current,
        Integer size,
        String keyword,
        Long categoryId,
        Long tagId,
        String type,
        String status,
        Integer isDelted
    ) {
    }
    @Data
    public static class AdminArticle {
        private Long id;
        private Long userId;
        private Long categoryId;
        private String articleCover;
        private String articleTitle;
        private String articleAbstract;
        private String articleContent;
        private String isTop;
        private String isFeatured;
        private String isDelted;
        private String status;
        private String type;
        private String password;
        private String originalUrl;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

    @Data
    public static class AdminArticlePageVO {
        private List<AdminArticle> adminArticles;
        private Long total;
    }


    
}
