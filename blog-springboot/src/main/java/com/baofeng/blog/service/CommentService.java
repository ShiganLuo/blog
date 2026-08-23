package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontCommentDTO.*;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentPageResponse;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentPageRequest;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentStatusUpateRequest;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkPageRequest;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkPageResponse;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkResult;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkSaveRequest;
import com.baofeng.blog.entity.Comment;

import java.util.List;

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
    public ApiResponse<Integer> getCommentTotal(Long rootId);

    /**
     * 获取用户通知信息
     * @param request
     * @return
     */
    public ApiResponse<NotifyPageResponse> getNotifyPage(NotifyPageRequest request);

    /**
     * 获取前台评论分页信息
     * @param commentPageRequest
     * @return
     */
    public ApiResponse<FrontCommentPageResponse> getFrontCommentPage(FrontCommentPageRequest request);

    /**
     * 获取后台评论分页信息
     * @return
     */
    public ApiResponse<AdminCommentPageResponse> getAdminCommentPage(AdminCommentPageRequest request);

    /**
     * 根据id删除评论
     * @param id
     * @return
     */
    public ApiResponse<String> deleteCommentById(Long id);

    /**
     * 获取某评论的子评论
     * @param commentId
     * @return
     */
    public ApiResponse<List<Comment>> getChildComment(Long commentId);

    /**
     * 获取所有留言
     * @return
     */
    public ApiResponse<MessagePageResponse> getAllMessage(MessageTalkPageRequest request);

    /**
     * 批量更新评论状态
     * @param request
     * @return
     */
    public ApiResponse<String> updateCommentsStatusByIds(AdminCommentStatusUpateRequest request);

    /** 后台说说分页列表 */
    ApiResponse<AdminTalkPageResponse> getAdminTalkPage(AdminTalkPageRequest request);

    /** 根据ID获取后台说说 */
    ApiResponse<AdminTalkResult> getAdminTalkById(Long id);

    /** 新增或修改说说 */
    ApiResponse<String> saveOrUpdateAdminTalk(AdminTalkSaveRequest request);

    /** 删除说说 */
    ApiResponse<String> deleteAdminTalks(List<Long> ids);
} 