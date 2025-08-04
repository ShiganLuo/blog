package com.baofeng.blog.mapper;


import org.apache.ibatis.annotations.Mapper;
import java.util.List;

import com.baofeng.blog.entity.Comment;
import com.baofeng.blog.vo.front.FrontCommentVO.*;

@Mapper
public interface CommentMapper {

    /**
     * 创建评论
     * @param commentRequest
     * @return Comment
     */
    int insertComment(Comment comment);

    /**
     * 获取文章评论总数
     * @param forId
     * @param type
     * @return
     */
    int getCommentTotal(Long forId, String type);

    /**
     * 获取用户相关消息
     * @param notifyPageRequest
     * @return
     */
    List<NotifyResponse> getNotifyPage(NotifyPageRequest notifyPageRequest);

    /**
     * 获取评论分页信息
     * @param commentPageRequest
     * @return
     */
    List<CommentResponse> getCommentPage(CommentPageRequest commentPageRequest);

    /**
     * 根据id删除评论
     * @param id
     * @return
     */
    int deleteCommentById(Long id);

    /**
     * 根据id增加likes
     * @param id
     * @return
     */
    int incrementLikeById(Long id);

    /**
     * 根据id减少likes
     * @param id
     * @return
     */
    int decreaseLikeById(Long id);

    /**
     * 检查评论是否存在
     * @param id
     * @return
     */
    Boolean selectCountById(Long id);
} 