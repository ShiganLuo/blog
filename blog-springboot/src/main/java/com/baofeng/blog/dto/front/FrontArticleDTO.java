package com.baofeng.blog.dto.front;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
public class FrontArticleDTO {
    /**
     * 取消文章点赞
     */
    public record LikeRequest(
        Long article_id,
        Long user_id
    ) {}
    
    /**
     * 返回文章详情
     */
    @Data
    public static class ArticleDetailResponse {
        private Long id;
        private String authorName;
        private Integer type;
        private String originUrl;
        private Integer thumbsUpTimes;
        private Long authorId;
        private String articleContent;
        private String articleCover;
        private String articleTitle;
        private Integer viewTimes;
        private List<String> categoryNameList;
        private List<String> tagNameList;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * 推荐文章以及前后发布文章返回对象
     */
    @Data
    public static class ArticleRecommendResponse {
        private ArticleDetailResponse previous;
        private ArticleDetailResponse next;
        private List<ArticleDetailResponse> recommend;
    }

    @Data
    public static class ArticleDetailResponsePair {
        private ArticleDetailResponse previous;
        private ArticleDetailResponse next;
    }

    public record TimeLineRequest(
        Integer current,
        Integer size
    ) {}

    @Data
    public static class ArticleAbstractResponse {
        private Long id;
        private String articleTitle;
        private String articleCover;
        private LocalDateTime createdAt;
    }
    @Data
    public static class ArticleAbstractsResponse {
        private Long total;
        private List<ArticleAbstractResponse> list;
    }

    public record CategoryOrTagRequest(
        Integer current,
        Integer size,
        Long id
    ) {}

    /**
     * 文章分页响应数据
     */
    @Data
    public static class FrontArticlePageResponse {

        private long total;          // 总记录数
        private int pages;           // 总页数
        private List<FrontArticle> list; // 文章列表
    }

    /**
     * 文章信息（改为 class，MyBatis 需要 setter）
     */
    @Data
    public static class FrontArticle {
        private Long id;
        private String articleTitle;
        private String articleDescription;
        private String articleContent;
        private Integer viewTimes;
        private Integer commentsCount;
        private Integer thumbsUpTimes;
        private Integer type;
        private String originUrl;
        private LocalDateTime  createdAt;
        private LocalDateTime updatedAt;
        private ArticleStatus status;
        private String articleCover;
        private Boolean isTop;
        private Boolean isFeatured;
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
