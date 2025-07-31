package com.baofeng.blog.service;

public interface ArticleLikeService {

    /**
     * 判断是否点赞
     * @param articleId
     * @param userId
     * @return 
     */
    Boolean getIsLikeByArticleAndUserId(Long articleId,Long userId);

    /**
     * 取消点赞
     * @param articleId
     * @param userId
     * @return
     */
    Boolean deleteLike(Long articleId,Long userId);
    
    /**
     * 点赞
     * @param articleId
     * @param userId
     * @return
     */
    Boolean addLike(Long articleId,Long userId);
}

