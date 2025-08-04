package com.baofeng.blog.service;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontCommentVO.*;

public interface CommentService {

    /**
     * 创建评论
     * @param createCommentRequest
     * @return
     */
    public ApiResponse<String> CreateComment(CreateCommentRequest createCommentRequest);

    /**
     * 评论总数查询
     * @param commentTotalRequest
     * @return
     */
    public ApiResponse<Integer> getCommentTotal(CommentTotalRequest commentTotalRequest);

    /**
     * 获取用户通知信息
     * @param request
     * @return
     */
    public ApiResponse<NotifyPageResponse> getNotifyPage(NotifyPageRequest request);

    /**
     * 获取评论分页信息
     * @param commentPageRequest
     * @return
     */
    public ApiResponse<CommentPageResponse> getCommentPage(CommentPageRequest commentPageRequest);

    /**
     * 根据id删除评论
     * @param id
     * @return
     */
     public ApiResponse<String> deleteCommentById(Long id);

} 