package com.baofeng.blog.service;

public interface LikeService {

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
    Boolean  addArticleLike(Long articleId,Long userId);
}

