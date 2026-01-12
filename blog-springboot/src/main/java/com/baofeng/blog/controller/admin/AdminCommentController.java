package com.baofeng.blog.controller.admin;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentPageRequest;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentPageResponse;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentStatusUpateRequest;
import com.baofeng.blog.service.CommentService;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;
    public AdminCommentController (
        CommentService commentService
    ) {
        this.commentService = commentService;
    }
    /**
     * 获取后台评论分页信息
     * @param request
     * @return
     */
    @PostMapping("/getAdminCommentPage")
    public ApiResponse<AdminCommentPageResponse> getAdminCommentPage(@RequestBody @Validated AdminCommentPageRequest request) {
        return commentService.getAdminCommentPage(request);
    }

    @PostMapping("/updateCommentsStatusByIds")
    public ApiResponse<String> updateCommentsStatusByIds(@RequestBody @Validated AdminCommentStatusUpateRequest request) {
        return commentService.updateCommentsStatusByIds(request);
    }
}
