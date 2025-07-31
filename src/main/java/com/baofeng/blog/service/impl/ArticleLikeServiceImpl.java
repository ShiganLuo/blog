package com.baofeng.blog.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.baofeng.blog.service.ArticleLikeService;
import com.baofeng.blog.mapper.ArticleLikeMapper;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.entity.ArticleLike;

@Service
// 替代@Autowerid显示注入
@RequiredArgsConstructor
public class ArticleLikeServiceImpl implements ArticleLikeService{

    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleMapper articleMapper;

    @Override
    public Boolean getIsLikeByArticleAndUserId(Long articleId,Long userId) {
        Long id = articleLikeMapper.getLikeByArticleAndUserId(articleId, userId);
        if ( id == null ){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Boolean deleteLike(Long articleId,Long userId) {
        Long rowUpdated = (long) 0;
        if (userId == null) {
            rowUpdated = articleMapper.decreaseLikeById(articleId);
        } else {
            rowUpdated = articleLikeMapper.deleteLikeByArticleAndUserId(articleId, userId);
        }
        return rowUpdated > 0;

    }

    @Override 
    public Boolean addLike(Long articleId,Long userId) {
        Long rowUpdated = (long) 0;
        if (userId == null) {
            // 游客点赞
            rowUpdated = articleMapper.incrementLikeById(articleId);
        } else {
            // 用户点赞
            ArticleLike articleLike = new ArticleLike();
            articleLike.setArticleId(articleId);
            articleLike.setUserId(userId);
            rowUpdated = articleLikeMapper.addLike(articleLike);
        }

        return rowUpdated > 0;

    }
    
}
