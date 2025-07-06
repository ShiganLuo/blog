package com.baofeng.blog.service;

import com.baofeng.blog.vo.admin.AdminCommentPageVO.CommentPageRequestVO;
import com.baofeng.blog.vo.admin.AdminCommentPageVO.CommentPageResponseVO;

public interface CommentService {
    /**
     * 分页查询评论列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    CommentPageResponseVO getCommentPage(CommentPageRequestVO request);

    /**
     * 删除评论（包括其子评论）
     * @param id 评论ID
     * @return 是否删除成功
     * @throws RuntimeException 当评论不存在时抛出
     */
    boolean deleteComment(Long id);
} 