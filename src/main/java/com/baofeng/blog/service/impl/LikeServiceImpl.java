package com.baofeng.blog.service.impl;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.baofeng.blog.service.LikeService;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.LikeMapper;
import com.baofeng.blog.entity.Like;

@Service
// 替代@Autowerid显示注入
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService{

    private final ArticleMapper articleMapper;
    private final LikeMapper likeMapper;

    @Override
    public Boolean getIsLikeByArticleAndUserId(Long articleId,Long userId) {
        Long id = likeMapper.getLikeByArticleAndUserId(articleId, userId);
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
            rowUpdated = likeMapper.deleteLikeByArticleAndUserId(articleId, userId);
        }
        return rowUpdated > 0;

    }

    @Override 
    public Boolean addArticleLike(Long articleId,Long userId) {
        Long isExist = likeMapper.getLikeStatusByArticleAndUserId(articleId,userId);
        Long rowUpdated = (long) 0;
        if (userId == null) {
            // 游客点赞
            rowUpdated = articleMapper.incrementLikeById(articleId);
        } else {
            // 用户点赞
            if (isExist != null) {
                rowUpdated = likeMapper.resumeLikeByArticleAndUserId(articleId, userId);
            } else {
                LocalDateTime now = LocalDateTime.now();
                Like like = new Like();
                like.setUserId(userId);
                like.setTargetId(articleId);
                like.setTargetType("post");
                like.setCreatedAt(now);
                like.setStatus(true);

                rowUpdated = likeMapper.addLike(like);
            }

        }

        return rowUpdated > 0;

    }
    
}
