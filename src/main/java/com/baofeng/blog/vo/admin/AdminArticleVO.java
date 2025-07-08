package com.baofeng.blog.vo.admin;

import java.util.List;

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
        Integer pageNum,     // 当前页码
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
    
}
