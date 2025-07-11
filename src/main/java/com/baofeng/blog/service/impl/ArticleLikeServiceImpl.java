package com.baofeng.blog.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.baofeng.blog.service.ArticleLikeService;
import com.baofeng.blog.mapper.ArticleLikeMapper;
import com.baofeng.blog.entity.ArticleLike;

@Service
// 替代@Autowerid显示注入
@RequiredArgsConstructor
public class ArticleLikeServiceImpl implements ArticleLikeService{

    private final ArticleLikeMapper articleLikeMapper;

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
        Long rowUpdated = articleLikeMapper.deleteLikeByArticleAndUserId(articleId, userId);
        if ( rowUpdated > 0) {
            return true;
        } else {
            return false;
        }

    }

    @Override 
    public Boolean addLike(Long articleId,Long userId) {
        ArticleLike articleLike = new ArticleLike();
        articleLike.setArticleId(articleId);
        articleLike.setUserId(userId);
        Long rowUpdated = articleLikeMapper.addLike(articleLike);

        if ( rowUpdated > 0) {
            return true;
        } else {
            return false;
        }
    }
    
}
