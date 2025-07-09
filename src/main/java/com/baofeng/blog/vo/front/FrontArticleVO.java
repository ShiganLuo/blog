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
        Long id;
        String authorName;
        Integer type;
        String origin_url;
        Integer thumbs_up_times;
        Long author_id;
        String article_content;
        String article_cover;
        String article_title;
        Integer view_times;
        List<String> categoryNameList;
        List<String> tagNameList;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }


}
