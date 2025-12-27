package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontCommentDTO.*;
import com.baofeng.blog.entity.Comment;
import com.baofeng.blog.service.CommentService;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/front/comments")
@Validated

public class FrontCommentController {

    private final CommentService commentService;

    public FrontCommentController (
        CommentService commentService
    ) {
        this.commentService = commentService;
    }

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
    @GetMapping("/getCommentTotal/{id}")
    public ApiResponse<Integer> getCommentTotal(@PathVariable Long id) {
        return commentService.getCommentTotal(id);
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

    /**
     * 根据id删除评论
     * @param id
     * @return
     */
    @GetMapping("/deleteCommentById/{id}")
    public ApiResponse<String> deleteCommentById(@PathVariable Long id) {
        return commentService.deleteCommentById(id);
    }

    @GetMapping("/getChileComment/{id}")
    public ApiResponse<List<Comment>> getChileCommentById(@PathVariable Long id) {
        return commentService.getChildComment(id);
    }

    /**
     * 获取所有留言
     * @return
     */
    @PostMapping("/getMessageTalkPage")
    public ApiResponse<MessagePageResponse> getAllMessage(@RequestBody MessageTalkPageRequest request) {
        return commentService.getAllMessage(request);
    }
    
}
