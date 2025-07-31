package com.baofeng.blog.service;

import com.baofeng.blog.vo.front.FrontCommentVO.*;

public interface CommentService {

    /**
     * 创建评论
     * @param createCommentRequest
     * @return
     */
    Boolean CreateComment(CreateCommentRequest createCommentRequest);

    /**
     * 评论总数查询
     * @param commentTotalRequest
     * @return
     */
    Integer getCommentTotal(CommentTotalRequest commentTotalRequest);

    /**
     * 获取用户通知信息
     * @param request
     * @return
     */
    NotifyPageResponse getNotifyPage(NotifyPageRequest request);

    /**
     * 获取评论分页信息
     * @param commentPageRequest
     * @return
     */
    CommentPageResponse getCommentPage(CommentPageRequest commentPageRequest);

    /**
     * 根据id删除评论
     * @param id
     * @return
     */
    Boolean deleteCommentById(Long id);

} 