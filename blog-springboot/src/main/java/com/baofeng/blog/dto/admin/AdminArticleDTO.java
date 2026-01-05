package com.baofeng.blog.dto.admin;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import com.baofeng.blog.common.annotation.MinioFile;
import com.baofeng.blog.common.annotation.MinioScan;
public class AdminArticleDTO {
    public static record CreateArticleRequest(
        String title,
        String content,
        String summary,
        String author
    ) {}
    /**
     * 文章分页请求参数
     */
    public static record ArticlePageRequest(
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

    public static record CreateAdminArticlePageRequest(
        Integer current,
        Integer size,
        String keyword,
        Long categoryId,
        Long tagId,
        String type,
        Integer status,
        Boolean isDeleted
    ) {    }


    @Data
    public static class AdminArticle {
        private Long id;
        private String authorName;
        @MinioFile
        private String articleCover;
        private String articleTitle;
        private List<String> categoryNameList;
        private String articleAbstract;
        private String articleContent;
        private Integer isTop;
        private Integer isFeatured;
        private Integer isDelted;
        private Integer viewsCount;
        private Integer status;
        private Integer type;
        private String originUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<String> tagNameList;
    }

    @Data
    @MinioScan(maxDepth = 2)
    public static class AdminArticlePageVO {
        private List<AdminArticle> list;
        private Long total;
    }

    public static record UpdateArticlesRequest(
        @NotEmpty List<Long> ids,
        Long id,
        Long imageId,
        String articleTitle,
        String articleContent,
        String articleAbstract,
        String articleCover,
        List<String> categoryNameList,
        List<String> tagNameList,
        Boolean isTop,
        Boolean isFeatured,
        Integer type,
        Boolean isDeleted,
        Integer status,
        String originUrl
    ){    }

    public static record DeleteArticlesLogicallyRequest(
        @NotEmpty List<Long> ids,
        @NotNull Boolean isDeleted
    ) {}


    
}
