package com.baofeng.blog.vo.admin;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

public class AdminArticleCRUDVO {
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
        Integer pageNum,     // 当前页码
        Integer pageSize,    // 每页显示条数
        String keyword,      // 可选，搜索关键词
        Long categoryId,     // 可选，分类ID
        String sortBy,       // 可选，排序字段
        String sortOrder     // 可选，排序方向
    ) {}

    /**
     * 文章分页响应数据
     */
    @Data
    public static class ArticlePageResponseVO {
        private long total;          // 总记录数
        private int pages;           // 总页数
        private List<ArticleVO> list; // 文章列表
    }

    /**
     * 文章信息（改为 class，MyBatis 需要 setter）
     */
    @Data
    public static class ArticleVO {
        private Long id;
        private String title;
        private String summary;
        private Integer viewCount;
        private Integer commentCount;
        private Integer likeCount;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private ArticleStatus status;
        private String coverImage;
        private AuthorVO author;
        private List<String> categoryNames;
    }
    public enum ArticleStatus {
        DRAFT, PUBLISHED, DELETED;
    }

    /**
     * 作者信息（改为 class，MyBatis 需要 setter）
     */
    @Data
    public static class AuthorVO {
        private Long authorId;
        private String authorNickname;
        private String authorAvatar;
    }

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
    
}
