package com.baofeng.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baofeng.blog.entity.ArticleLike;

@Mapper
public interface ArticleLikeMapper {

    /**
     * 判断是否点赞
     * @param articleId
     * @param userId
     * @return
     */
    Long getLikeByArticleAndUserId(Long articleId, Long userId);
    
    /**
     * 取消点赞
     * @param articleId
     * @param userId
     * @return
     */
    Long deleteLikeByArticleAndUserId(Long articleId, Long userId);

    /**
     * 点赞
     * @param articleId
     * @param userId
     * @return
     * 使用了回填；useGeneratedKeys="true" keyProperty="id"
     */
    Long addLike(ArticleLike articleLike);
}
