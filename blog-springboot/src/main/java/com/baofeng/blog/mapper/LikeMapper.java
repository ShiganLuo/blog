package com.baofeng.blog.mapper;
import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baofeng.blog.entity.Like;

@Mapper
public interface LikeMapper {
    /**
     * 判断是否点赞,status为1
     * @param articleId
     * @param userId
     * @return
     */
    Long selectIdByLikeRequestAndStatus(Long targetId, String type, Long userId, Boolean status);
    
    
    /**
     * 取消点赞
     * @param articleId
     * @param userId
     * @return
     */
    int updateLikesByLikeRequestAndStatus(Long targetId, String type, Long userId, Boolean status);

    /**
     * 点赞
     * @param articleId
     * @param userId
     * @return
     * 使用了回填；useGeneratedKeys="true" keyProperty="id"
     */
    int insertLikeByLike(Like like);

    long selectLikeCountWhenSpecifiedTime(@Param("createdAt") LocalDateTime createdAt);

}
