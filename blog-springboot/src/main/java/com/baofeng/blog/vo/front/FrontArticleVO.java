package com.baofeng.blog.vo.front;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
public class FrontArticleVO {
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
        private String origin_url;
        private Integer thumbs_up_times;
        private Long author_id;
        private String article_content;
        private String article_cover;
        private String article_title;
        private Integer view_times;
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
        private String article_title;
        private String article_cover;
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

}
