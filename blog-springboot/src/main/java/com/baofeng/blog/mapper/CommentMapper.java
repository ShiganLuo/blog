package com.baofeng.blog.mapper;

import com.baofeng.blog.dto.front.FrontCommentDTO.*;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentResult;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentPageRequest;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentStatusUpateRequest;
import com.baofeng.blog.entity.Comment;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
     * 获取前台论分页信息
     * @param commentPageRequest
     * @return
     */
    List<FrontArticleCommentResponse> getCommentsByCondition(FrontCommentPageRequest request);


    /**
     * 获取后台评论分页信息
     * @param request
     * @return
     */
    List<AdminCommentResult> getAdminCommentsByCondition(AdminCommentPageRequest request);

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
    List<FrontCommentResponse> selectChildCommentsById(Long id);

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

    /**
     * 批量更新评论状态
     * @param ids
     * @param status
     * @return
     */
    int updateCommentsStatusByIds(AdminCommentStatusUpateRequest request);
} 