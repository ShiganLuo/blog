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
        Boolean delted
    ) {
        public CreateAdminArticlePageRequest {
            if (current == null || current < 1) {
                throw new IllegalArgumentException("页码必须大于等于1");
            }
            if (size == null || size < 1) {
                throw new IllegalArgumentException("每页显示条数必须大于等于1");
            }
            if (categoryId != null && categoryId < 1) {
                throw new IllegalArgumentException("分类ID必须大于等于1");
            }
            if (tagId != null && tagId < 1) {
                throw new IllegalArgumentException("标签ID必须大于等于1");
            }
            if (type != null && type.isEmpty()) {
                type = null;
            }
            if (status != null && status.isEmpty()) {
                status = null;
            };
        }
    }

    @Data
    public static class AdminArticle {
        private Long id;
        private String authorName;
        private String articleCover;
        private String articleTitle;
        private List<String> categoryNameList;
        private String articleAbstract;
        private String articleContent;
        private Integer isTop;
        private Integer isFeatured;
        private Integer isDelted;
        private Integer viewsCount;
        private String status;
        private Integer type;
        private String originalUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<String> tagNameList;
    }

    @Data
    public static class AdminArticlePageVO {
        private List<AdminArticle> list;
        private Long total;
    }


    
}
