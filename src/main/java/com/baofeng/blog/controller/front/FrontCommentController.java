package com.baofeng.blog.controller.front;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.baofeng.blog.service.CommentService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontCommentVO.*;

@RestController
@RequestMapping("/api/front/comments")
@RequiredArgsConstructor
@Validated

public class FrontCommentController {

    private final CommentService commentService;

    /**
     * 添加评论
     * @param createCommentRequest
     * @return
     */
    @PostMapping("/addComment")
    public ApiResponse<String> addComment(@RequestBody CreateCommentRequest createCommentRequest) {
        return commentService.CreateComment(createCommentRequest);
    }

    /**
     * 获取评论总数
     * @param commentTotalRequest
     * @return
     */
    @PostMapping("/getCommentTotal")
    public ApiResponse<Integer> getCommentTotal(@RequestBody CommentTotalRequest commentTotalRequest) {
        return commentService.getCommentTotal(commentTotalRequest);
    }

    /**
     * 获取评论中用户相关信息
     * @param request
     * @return
     */
    @PostMapping("/getNotifyPage")
    public ApiResponse<NotifyPageResponse> getNotifyPageInfo(@RequestBody NotifyPageRequest request) {
        return commentService.getNotifyPage(request);
    }

    /**
     * 获取评论分页
     * @param request
     * @return
     */
    @PostMapping("/getCommentPage")
    public ApiResponse<CommentPageResponse> getCommentPageInfo(@RequestBody CommentPageRequest request) {
        return commentService.getCommentPage(request);
    }

    @GetMapping("/deleteCommentById/{id}")
    public ApiResponse<String> deleteCommentById(@PathVariable Long id) {
        return commentService.deleteCommentById(id);
    }


    
}
