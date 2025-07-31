package com.baofeng.blog.vo.front;

// import lombok.Data;

public class FrontArticleLikeVO {

    /**
     * 文章点赞
     */
    public record LikeRequest(
        Long article_id,
        Long user_id
    ) {}
    
}
