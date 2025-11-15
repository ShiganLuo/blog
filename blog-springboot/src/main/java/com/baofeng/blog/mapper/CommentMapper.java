package com.baofeng.blog.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.baofeng.blog.dto.front.FrontCommentDTO.*;
import com.baofeng.blog.entity.Comment;


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
    int getCommentTotal(Long rootId);

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
    List<ArticleCommentResponse> getCommentsByCondition(CommentPageRequest commentPageRequest);

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

    /**
     * 获取某评论的所有子评论
     * @param commentId
     * @return
     */
    List<Comment> selectChildComment(Long commentId);

    /**
     * 获取所有留言
     * @return
     */
    List<MessageResponse> selectAllMessageTalk(MessageTalkPageRequest messageTalkPageRequest);

    /**
     * 根据评论id获取其子评论
     * @param commentId
     * @return
     */
    List<CommentResponse> selectChildCommentsById(Long id);

    /**
     * 批量删除评论
     * @param ids
     * @return
     */
    int deleteCommentsByIds(Set<Long> ids);

    /**
     * 查看指定时间已经存在的各种评论总数
     * @param createdAt
     * @return
     */
    long selectCommentCountWhenSpecifiedTime(@Param("createdAt") LocalDateTime createdAt);

} 