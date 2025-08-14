package com.baofeng.blog.vo.common;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

public class Article {
    
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
        private String article_title;
        private String article_description;
        private String article_content;
        private Integer view_times;
        private Integer commentsCount;
        private Integer thumbs_up_times;
        private Integer type;
        private String originUrl;
        private LocalDateTime  createdAt;
        private LocalDateTime updatedAt;
        private ArticleStatus status;
        private String article_cover;
        private Boolean is_top;
        private AuthorVO author;
        private List<String> categoryNameList;
        private List<String> tagNameList;
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
        private String authorName;
        private String authorNickname;
        private String authorAvatar;
    }
}
