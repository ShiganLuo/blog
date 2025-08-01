package com.baofeng.blog.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.baofeng.blog.entity.Like;

@Mapper
public interface LikeMapper {
    /**
     * 判断是否点赞,status为1
     * @param articleId
     * @param userId
     * @return
     */
    Long getLikeByArticleAndUserId(Long targetId, Long userId);
    
    /**
     * 判断status为0点赞记录是否存在
     * @param targetId
     * @param userId
     * @return
     */
    Long getLikeStatusByArticleAndUserId(Long targetId, Long userId);
    
    /**
     * 取消点赞
     * @param articleId
     * @param userId
     * @return
     */
    Long deleteLikeByArticleAndUserId(Long targetId, Long userId);


    /**
     * 恢复点赞
     * @param targetId
     * @param userId
     * @return
     */
    Long resumeLikeByArticleAndUserId(Long targetId, Long userId);

    /**
     * 点赞
     * @param articleId
     * @param userId
     * @return
     * 使用了回填；useGeneratedKeys="true" keyProperty="id"
     */
    Long addLike(Like like);


}
